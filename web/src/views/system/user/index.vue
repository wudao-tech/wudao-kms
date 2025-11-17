<template>
  <div class="p-15">
    <div class="p-card">
      <el-row :gutter="10" style="margin-bottom: 10px; display: flex; justify-content: space-between">
        <div class="dp-c">
          <el-dropdown  v-has-permi="['system:user:add']"  trigger="click" @command="handleCommand">
            <el-button  link icon="Plus">新增</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="a">单个添加</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button v-has-permi="['system:user:delete']"  link :disabled="multiple" icon="Delete" @click="handleDelete()"> 删除 </el-button>
        </div>
        <el-input v-model="queryParams.username" :prefix-icon="Search" style="width: 200px" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
      </el-row>
      <el-table v-loading="loading" :data="userList" border :max-height="700" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="用户编号" align="center" prop="id" />
            <el-table-column label="用户名称" align="center" prop="username" :show-overflow-tooltip="true" />
            <el-table-column label="用户昵称" align="center" prop="nickname" :show-overflow-tooltip="true" />
            <el-table-column label="部门" align="center" prop="deptName" :show-overflow-tooltip="true" />
            <el-table-column label="手机号码" align="center" prop="phoneNumber" width="120" />
            <el-table-column label="状态" align="center">
              <template #default="scope">
                <el-switch v-model="scope.row.status" :active-value="0" :inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
              </template>
            </el-table-column>

            <el-table-column label="创建时间" align="center" prop="createdAt" width="160">
              <template #default="scope">
                <span>{{ scope.row.createdAt }}</span>
              </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" width="180" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-tooltip v-if="scope.row.id !== 1" content="修改" placement="top">
                  <el-button v-hasPermi="['system:user:edit']"  link icon="Edit" @click="handleUpdate(scope.row)"></el-button>
                </el-tooltip>
                <el-tooltip v-if="scope.row.id !== 1" content="删除" placement="top">
                  <el-button v-hasPermi="['system:user:remove']"  link icon="Delete" @click="handleDelete(scope.row)"></el-button>
                </el-tooltip>

                <el-tooltip v-if="scope.row.id !== 1" content="重置密码" placement="top">
                  <el-button v-hasPermi="['system:user:resetPwd']"  link icon="Key" @click="handleResetPwd(scope.row)"></el-button>
                </el-tooltip>

                <el-tooltip v-if="scope.row.id !== 1" content="分配角色" placement="top">
                  <el-button v-hasPermi="['system:user:edit']"  link icon="CircleCheck" @click="handleAuthRole(scope.row)"></el-button>
                </el-tooltip>
              </template>
            </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </div>

    <!-- 添加单个 -->
    <el-dialog ref="formDialogRef" v-model="dialog.visible_a" :title="dialog.title" width="600px" append-to-body @close="closeDialog">
      <!-- <div style="margin-bottom: 20px">
        <span style="color: #ff0000">Tips:</span>
        添加成员账号，成员通过用户名和初始密码登陆系统，修改初始密码后进入组织
      </div> -->
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="deptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' } as any"
                value-key="id"
                placeholder="请选择归属部门"
                check-strictly
                @change="handleDeptChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phoneNumber">
              <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.id == undefined" label="用户名称" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.id == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.gender" placeholder="请选择">
                <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择">
                <el-option
                  v-for="item in postOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleIds">
              <el-select v-model="form.roleIds" filterable multiple placeholder="请选择">
                <el-option
                  v-for="item in roleList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel()">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="User" lang="ts">
import api from '@/api/system/user';
import { Search } from '@element-plus/icons-vue';
import { UserForm, UserQuery, UserVO } from '@/api/system/user/types';
import { listRole } from '@/api/system/role';
import { listPost, optionselect } from '@/api/system/post';
import { DeptVO } from '@/api/system/dept/types';
import { RoleVO } from '@/api/system/role/types';
import { PostQuery, PostVO } from '@/api/system/post/types';
import { treeselect } from '@/api/system/dept';
import { globalHeaders } from '@/utils/request';
import { to } from 'await-to-js';

import { useBusinessDialog } from '@/components/Business/hooks/useBusinessDialog';

const router = useRouter();
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable, sys_user_sex } = toRefs<any>(proxy?.useDict('sys_normal_disable', 'sys_user_sex'));
const { dialogInviteLink, dialogInviteMail } = useBusinessDialog();

const userList = ref<UserVO[]>([]);
const loading = ref(false);
const ids = ref<Array<number | string>>([]);
const single = ref(true);
const multiple = ref(true);
const linkUrl = ref(null);
const clipboard = ref(null);
const total = ref(0);
const dateRange = ref<[DateModelType, DateModelType]>(['', '']);
const deptName = ref('');
const deptOptions = ref<DeptVO[]>([]);
const initPassword = ref<string>('');
const postOptions = ref([]);
const roleOptions = ref<RoleVO[]>([]);
/*** 用户导入参数 */
const upload = reactive<ImportOption>({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: '',
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: globalHeaders(),
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + '/system/user/importData'
});
// 列显隐信息
const columns = ref<FieldOption[]>([
  { key: 0, label: `用户编号`, visible: false, children: [] },
  { key: 1, label: `用户名称`, visible: true, children: [] },
  { key: 2, label: `用户昵称`, visible: true, children: [] },
  { key: 3, label: `部门`, visible: true, children: [] },
  { key: 4, label: `手机号码`, visible: true, children: [] },
  { key: 5, label: `状态`, visible: true, children: [] },
  { key: 6, label: `创建时间`, visible: true, children: [] }
]);
const rules1 = {};
const deptTreeRef = ref<ElTreeInstance>();
const queryFormRef = ref<ElFormInstance>();
const userFormRef = ref<ElFormInstance>();
const uploadRef = ref<ElUploadInstance>();
const formDialogRef = ref<ElDialogInstance>();
const userFormRef_b = ref({});

const dialog = reactive({
  visible_a: false,
  visible_b: false,
  visible_c: false,
  visible_d: false,
  title: '单个添加'
});

const handleCommand = (el) => {
  if (el === 'a') {
    dialog.visible_a = true;
    dialog.title = '单个添加';
    handleAdd();
  } else if (el === 'b') {
    dialog.visible_b = true;
    dialog.title = '快速添加';
  } else if (el === 'c') {
    dialogInviteMail.go({})
  } else {
    dialogInviteLink.go({})
  }
};

const getInviteLink = () => {
  linkUrl.value = '';
  inviteLink([{}]).then((res) => {
    linkUrl.value = res.data[0].url;
  });
};

const handleTabInvite = () => {
  console.log(urlActive.value);
  if (urlActive.value === 'second') {
    let params = {
      bo: {},
      pageQuery: {
        pageSize: 9999,
        pageNum: 1
      }
    };
    inviteLinkList(params).then((res) => {
      console.log('res', res);
      emailList.value = res.data;
    });
  }
};

const initFormData = {
  id: undefined,
  deptId: undefined,
  username: '',
  nickname: undefined,
  password: '',
  phoneNumber: undefined,
  email: undefined,
  gender: undefined,
  status: 0,
  remark: '',
  postIds: [],
  roleIds: []
};

const initData = {
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    username: '',
    phoneNumber: '',
    status: '',
    deptId: '',
    roleId: ''
  },
  rules: {
    username: [
      { required: true, message: '用户名称不能为空', trigger: 'blur' },
      {
        min: 2,
        max: 20,
        message: '用户名称长度必须介于 2 和 20 之间',
        trigger: 'blur'
      }
    ],
    nickname: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
    password: [
      { required: true, message: '用户密码不能为空', trigger: 'blur' },
      {
        min: 5,
        max: 20,
        message: '用户密码长度必须介于 5 和 20 之间',
        trigger: 'blur'
      },
      { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > " \' \\ |', trigger: 'blur' }
    ],
    email: [
      {
        type: 'email',
        message: '请输入正确的邮箱地址',
        trigger: ['blur', 'change']
      }
    ],
    phoneNumber: [
      // { required: true, message: '手机号不能为空', trigger: 'blur' },
      {
        pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
        message: '请输入正确的手机号码',
        trigger: 'blur'
      }
    ],
    roleIds: [{ required: true, message: '用户角色不能为空', trigger: 'blur' }]
  }
};
const data = reactive<PageData<UserForm, UserQuery>>(initData);

const { queryParams, form, rules } = toRefs<PageData<UserForm, UserQuery>>(data);

// 快速添加
const form1 = ref({
  phoneNumber: ['123']
});

const addPhone = () => {
  form1.value.phoneNumber.push('');
};

const delPhone = (ind) => {
  form1.value.phoneNumber.splice(ind, 1);
};

const emailActive = ref('first');
const form2 = ref({
  email: ['']
});

const emailList = ref([{}]);

const addEmail = () => {
  form2.value.email.push('');
};

const delEmail = (ind) => {
  form2.value.email.splice(ind, 1);
};

const roleList = ref([]);
const getRole = () => {
  let queryParams = {
    pageNum: 1,
    pageSize: 999
  };
  let dateRange = ['', ''];
  listRole(proxy?.addDateRange(queryParams, dateRange)).then((res) => {
    roleList.value = res.data;
  });
};
// const postList = ref([]);
const getPost = async () => {
  let queryParams = {
    pageNum: 1,
    pageSize: 999
  };
  const res = await listPost(queryParams);
  postOptions.value = res.data;
}

/** 查询部门下拉树结构 */
const getDeptTree = async () => {
  const res = await api.deptTreeSelect();
  deptOptions.value = res.data;
};

const urlActive = ref('first');

/** 根据名称筛选部门树 */
watchEffect(
  () => {
    deptTreeRef.value?.filter(deptName.value);
  },
  {
    flush: 'post' // watchEffect会在DOM挂载或者更新之前就会触发，此属性控制在DOM元素更新后运行
  }
);

const getJs = (el) => {};

/** 查询用户列表 */
const getList = async () => {
  loading.value = true;
  const res = await api.listUser(proxy?.addDateRange(queryParams.value, dateRange.value));
  loading.value = false;
  userList.value = res.data;
  total.value = res.total;
};

/** 节点单击事件 */
const handleNodeClick = (data: DeptVO) => {
  queryParams.value.deptId = data.id;
  handleQuery();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 删除按钮操作 */
const handleDelete = async (row?: UserVO) => {
  const userIds = row?.id || ids.value;
  const [err] = await to(proxy?.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？') as any);
  if (!err) {
    await api.delUser(userIds);
    await getList();
    proxy?.$modal.msgSuccess('删除成功');
  }
};

/** 用户状态修改  */
const handleStatusChange = async (row) => {
  let text = row.status === 0 ? '启用' : '停用';
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.username + '"用户吗?');
    await api.changeUserStatus(row.id, row.status);
    proxy?.$modal.msgSuccess(text + '成功');
  } catch (err) {
    row.status = row.status === 0 ? 1 : 0;
  }
};
/** 跳转角色分配 */
const handleAuthRole = (row) => {
  const id = row.id;
  router.push('/system/user-auth/role/' + id);
};

/** 重置密码按钮操作 */
const handleResetPwd = async (row) => {
  const [err, res] = await to(
    ElMessageBox.prompt('请输入"' + row.username + '"的新密码', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      closeOnClickModal: false,
      inputPattern: /^.{5,20}$/,
      inputErrorMessage: '用户密码长度必须介于 5 和 20 之间',
      inputValidator: (value) => {
        if (/<|>|"|'|\||\\/.test(value)) {
          return '不能包含非法字符：< > " \' \\ |';
        }
      }
    })
  );
  if (!err && res) {
    await api.resetUserPwd(row.id, res.value);
    proxy?.$modal.msgSuccess('修改成功，新密码是：' + res.value);
  }
};

/** 选择条数  */
const handleSelectionChange = (selection: UserVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};


/** 重置操作表单 */
const reset = () => {
  form.value = { ...initFormData };
  userFormRef.value?.resetFields();
};
/** 取消按钮 */
const cancel = () => {
  dialog.visible_a = false;
  dialog.visible_b = false;
  dialog.visible_c = false;
  dialog.visible_d = false;
  reset();
};

/** 新增按钮操作 */
const handleAdd = async () => {
  reset();
  dialog.visible_a = true;
  dialog.title = '新增用户';
  form.value.password = initPassword.value.toString();
};

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset();
  const id = row?.id || ids.value[0];
  const { data } = await api.getUser(id);
  console.log('data', data);
  dialog.visible_a = true;
  dialog.title = '修改用户';
  Object.assign(form.value, data);
  form.value.status = data.status === 1 ? '1' : '0';
  form.value.postIds = data.postIds;
  form.value.roleIds = data.roleIds;
  // form.value.password = '';
};

/** 提交按钮 */
const submitForm = () => {
  userFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      form.value.id ? await api.updateUser(form.value) : await api.addUser(form.value);
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible_a = false;
      await getList();
    }
  });
};

/**
 * 关闭用户弹窗
 */
const closeDialog = () => {
  dialog.visible_a = false;
  resetForm();
};

/**
 * 重置表单
 */
const resetForm = () => {
  userFormRef.value?.resetFields();
  userFormRef.value?.clearValidate();

  form.value.id = undefined;
  form.value.status = 1;
};
onMounted(() => {
  getList(); // 初始化列表数据
  getRole();
  getPost()
  getDeptTree(); // 初始化部门数据
  proxy?.getConfigKey('sys.user.initPassword').then((response) => {
    initPassword.value = response.data.value;
  });
});

async function handleDeptChange(value: number | string) {
  const response = await optionselect(value);
  postOptions.value = response.data;
  form.value.postIds = [];
}
</script>

<style lang="scss" scoped></style>
