<template>
  <div class="p-15">
    <div class="p-card">
      <el-row :gutter="10" style="margin-bottom: 10px">
        <el-col :span="1.5">
          <el-button v-hasPermi="['system:dept:add']" link icon="Plus" @click="handleAdd()">新增 </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button link>
            <template #icon>
              <i class="iconfont se-dig-icon-a-Logouttuichu" />
            </template>
            导出</el-button
          >
        </el-col>
        <el-col :span="1.5">
          <el-button link style="color: #3679de" icon="Sort" @click="handleToggleExpandAll">展开/折叠</el-button>
        </el-col>
      </el-row>
      <el-table
        ref="deptTableRef"
        v-loading="loading"
        :data="deptList"
        border
        :max-height="770"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="isExpandAll"
      >
        <el-table-column prop="name" label="团队名称"></el-table-column>
        <el-table-column prop="code" label="部门编码"></el-table-column>
        <el-table-column prop="sort" label="成员数量"></el-table-column>
        <el-table-column prop="status" align="center" label="状态" width="100">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdAt">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:dept:edit']" style="font-size: 16px" link icon="Edit" @click="handleUpdate(scope.row)" />
            </el-tooltip>
            <el-tooltip content="新增" placement="top">
              <el-button v-hasPermi="['system:dept:add']" style="font-size: 16px" link icon="Plus" @click="handleAdd(scope.row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:dept:remove']" style="font-size: 16px" link icon="Delete" @click="handleDelete(scope.row)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.title" destroy-on-close append-to-body width="600px">
      <el-form ref="deptFormRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col v-if="form.pid !== 0" :span="24">
            <el-form-item label="上级团队" prop="pid">
              <el-tree-select
                v-model="form.pid"
                :data="deptOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="选择上级团队"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="团队名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入团队名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="团队编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入类别编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sort">
              <el-input-number v-model="form.sort" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leaderId">
              <el-select v-model="form.leaderId" placeholder="请选择负责人">
                <el-option v-for="item in deptUserList" :key="item.id" :label="item.nickname" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="团队状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Dept" lang="ts">
import { listDept, getDept, delDept, addDept, updateDept, listDeptExcludeChild } from '@/api/system/dept';
import { DeptForm, DeptQuery, DeptVO } from '@/api/system/dept/types';
import { UserVO } from '@/api/system/user/types';
import { listUserByDeptId, listUser } from '@/api/system/user';

interface DeptOptionsType {
  id: number | string;
  name: string;
  children: DeptOptionsType[];
}

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

const deptList = ref<DeptVO[]>([]);
const loading = ref(false);
const showSearch = ref(true);
const deptOptions = ref<DeptOptionsType[]>([]);
const isExpandAll = ref(true);
const deptUserList = ref([]);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const deptTableRef = ref<ElTableInstance>();
const queryFormRef = ref<ElFormInstance>();
const deptFormRef = ref<ElFormInstance>();

const initFormData: DeptForm = {
  id: undefined,
  pid: undefined,
  name: undefined,
  code: undefined,
  sort: 0,
  leaderId: undefined,
  phone: undefined,
  email: undefined,
  status: '0'
};
const initData: PageData<DeptForm, DeptQuery> = {
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    code: undefined,
    status: undefined
  },
  rules: {
    // pid: [{ required: true, message: '上级团队不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '团队名称不能为空', trigger: 'blur' }],
    sort: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
    email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
    phone: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }]
  }
};
const data = reactive<PageData<DeptForm, DeptQuery>>(initData);

const { queryParams, form, rules } = toRefs<PageData<DeptForm, DeptQuery>>(data);

/** 查询菜单列表 */
const getList = async () => {
  loading.value = true;
  const res = await listDept(queryParams.value);
  const data = proxy?.handleTree<DeptVO>(res.data, 'id');
  if (data) {
    deptList.value = data;
  }
  loading.value = false;
};

/** 查询当前团队的所有用户 */
async function getDeptAllUser(deptId: any) {
  if (deptId !== null && deptId !== '' && deptId !== undefined) {
    let params = {
      deptId: deptId
    };
    const res = await listUserByDeptId(params);
    deptUserList.value = res.data;
  }
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};
/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  deptFormRef.value?.resetFields();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  getList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 展开/折叠操作 */
const handleToggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value;
  toggleExpandAll(deptList.value, isExpandAll.value);
};
/** 展开/折叠所有 */
const toggleExpandAll = (data: DeptVO[], status: number) => {
  data.forEach((item) => {
    deptTableRef.value?.toggleRowExpansion(item, status);
    if (item.children && item.children.length > 0) toggleExpandAll(item.children, status);
  });
};

/** 新增按钮操作 */
const handleAdd = async (row?: DeptVO) => {
  reset();
  const res = await listDept();
  const data = proxy?.handleTree<DeptOptionsType>(res.data, 'id');
  if (data) {
    deptOptions.value = data;
    if (row && row.id) {
      form.value.pid = row?.id;
    }
    dialog.visible = true;
    dialog.title = '添加团队';
  }
};

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset();
  //查询当前团队所有用户
  // getDeptAllUser(row.id);
  const res = await getDept(row.id);
  form.value = res.data;
  form.value.status = res.data.status.toString();
  console.log('form.value', form.value);
  const response = await listDeptExcludeChild(row.id);
  const data = proxy?.handleTree<DeptOptionsType>(response.data, 'id');
  if (data) {
    deptOptions.value = data;
    if (data.length === 0) {
      const noResultsOptions: DeptOptionsType = {
        id: res.data.pid,
        name: res.data.parentName,
        children: []
      };
      deptOptions.value.push(noResultsOptions);
    }
  }
  dialog.visible = true;
  dialog.title = '修改团队';
};
/** 提交按钮 */
const submitForm = () => {
  deptFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      form.value.id ? await updateDept(form.value) : await addDept(form.value);
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};
/** 删除按钮操作 */
const handleDelete = async (row: DeptVO) => {
  await proxy?.$modal.confirm('是否确认删除名称为"' + row.name + '"的数据项?');
  await delDept(row.id);
  await getList();
  proxy?.$modal.msgSuccess('删除成功');
};

onMounted(() => {
  getList();
  listUser().then(res => {
    deptUserList.value = res.data
  })
});
</script>
