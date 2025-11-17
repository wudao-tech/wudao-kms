<template>
  <div class="p-15" style="height: 100%">
    <div class="p-card">
      <div class="dp-c fun">
        <div>
          <el-button v-hasPermi="['system:dict:add']" link plain icon="Plus" @click="handleAdd">新增</el-button>
          <el-button v-hasPermi="['system:dict:remove']" link plain icon="Delete" :disabled="multiple" @click="handleDelete()">
            删除
          </el-button>
          <el-button v-hasPermi="['system:dict:export']" link plain icon="Download" @click="handleExport">导出</el-button>
        </div>
        <el-form ref="queryFormRef" :model="queryParams" :inline="true">
          <el-form-item label="字典名称" prop="name" style="margin-bottom: 0">
            <el-input v-model="queryParams.name" placeholder="请输入字典名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="字典类型" prop="category" style="margin-bottom: 0">
            <el-input v-model="queryParams.category" placeholder="请输入字典类型" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <!-- <el-form-item label="创建时间" style="width: 308px">
            <el-date-picker
              v-model="dateRange"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
            ></el-date-picker>
          </el-form-item> -->
          <el-form-item style="margin-right: 0; margin-bottom: 0">
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table v-loading="loading" :data="typeList" border :max-height="660" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="字典编号" align="center" prop="type" />
        <el-table-column label="字典名称" align="center" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="字典类型" align="center" :show-overflow-tooltip="true">
          <template #default="scope">
            <router-link :to="'/system/dict-data/index/' + scope.row.type" class="link-type">
              <span>{{ scope.row.type }}</span>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:dict:edit']" link  icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:dict:remove']" link icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </div>
    <!-- 添加或修改参数配置对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="dictFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="type">
          <el-input v-model="form.type" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
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

<script setup name="Dict" lang="ts">
import useDictStore from '@/store/modules/dict';
import { listType, getType, delType, addType, updateType, refreshCache } from '@/api/system/dict/type';
import { DictTypeForm, DictTypeQuery, DictTypeVO } from '@/api/system/dict/type/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const typeList = ref<DictTypeVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<number | string>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref<[DateModelType, DateModelType]>(['', '']);

const dictFormRef = ref<ElFormInstance>();
const queryFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: DictTypeForm = {
  type: undefined,
  name: '',
  category: '',
  remark: ''
};
const data = reactive<PageData<DictTypeForm, DictTypeQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: '',
    category: ''
  },
  rules: {
    name: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }],
    category: [{ required: true, message: '字典类型不能为空', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询字典类型列表 */
const getList = () => {
  loading.value = true;
  listType(proxy?.addDateRange(queryParams.value, dateRange.value)).then((res) => {
    typeList.value = res.data;
    total.value = res.total;
    loading.value = false;
  });
};
/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};
/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  dictFormRef.value?.resetFields();
};
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  dateRange.value = ['', ''];
  queryFormRef.value?.resetFields();
  handleQuery();
};
/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加字典类型';
};
/** 多选框选中数据 */
const handleSelectionChange = (selection: DictTypeVO[]) => {
  ids.value = selection.map((item) => item.type);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};
/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset();
  console.log('row',row)
  const type = row?.type || ids.value[0];
  const res = await getType(type);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改字典类型';
};
/** 提交按钮 */
const submitForm = () => {
  dictFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      form.value.type ? await updateType(form.value) : await addType(form.value);
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      getList();
    }
  });
};
/** 删除按钮操作 */
const handleDelete = async (row?: DictTypeVO) => {
  const types = row?.type || ids.value;
  await proxy?.$modal.confirm('是否确认删除字典编号为"' + types + '"的数据项？');
  await delType(types);
  getList();
  proxy?.$modal.msgSuccess('删除成功');
};
/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/dict/type/export',
    {
      ...queryParams.value
    },
    `dict_${new Date().getTime()}.xlsx`
  );
};
/** 刷新缓存按钮操作 */
const handleRefreshCache = async () => {
  await refreshCache();
  proxy?.$modal.msgSuccess('刷新成功');
  useDictStore().cleanDict();
};

onMounted(() => {
  getList();
});
</script>
<style scoped>
.fun {
  justify-content: space-between;
  margin-bottom: 10px;
}
</style>
