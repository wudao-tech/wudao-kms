<template>
  <div class="p-15">
    <div class="p-card">
      <el-row :gutter="10" style="margin-bottom: 10px; display: flex; justify-content: space-between">
        <div class="dp-c">
          <!-- v-hasPermi="['system:tenant:add']" -->
          <el-button v-hasPermi="['system:tenant:add']" link plain icon="Plus" @click="handleAdd">新增</el-button>
          <!-- <el-button v-hasPermi="['system:tenant:edit']" link icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button> -->
          <!-- <el-button link icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button> -->
          <el-button v-hasPermi="['system:tenant:export']" link @click="handleExport">
            <template #icon>
              <i class="iconfont se-dig-icon-a-Logouttuichu" />
            </template>
            导出</el-button>
        </div>
        <el-input v-model="queryParams.companyName" style="width: 250px" :prefix-icon="Search" placeholder="请输入租户名称" clearable @keyup.enter="handleQuery" />
      </el-row>
      <el-table v-loading="loading" border :max-height="730" :data="tenantList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="id" align="center" prop="id" />
        <el-table-column label="租户编号" align="center" prop="tenantId" />
        <el-table-column label="租户名称" align="center" prop="companyName" />
        <el-table-column label="管理员名称" align="center" prop="contactUserName" />
        <el-table-column label="管理员电话" align="center" prop="contactPhone" />
        <el-table-column label="管理员邮箱" align="center" prop="contactEmail" />
        <el-table-column label="启用状态" align="center" prop="status">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column width="250" label="操作" align="center" fixed="right" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:tenant:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <!-- <el-tooltip content="同步" placement="top">
              <el-button v-hasPermi="['system:tenant:edit']" link type="primary" icon="Refresh" @click="handleSyncTenantPackage(scope.row)">
              </el-button>
            </el-tooltip> -->
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:tenant:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>

            <el-dropdown v-has-permi="['system:user:add']" trigger="click" @command="handleCommand" style="margin: 2px 0 0 12px">
              <el-button link icon="Plus" type="primary" title="邀请用户"></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <!-- <el-dropdown-item command="a">单个添加</el-dropdown-item> -->
                  <!-- <el-dropdown-item command="b">快速添加</el-dropdown-item> -->
                  <el-dropdown-item command="c">邮箱邀请</el-dropdown-item>
                  <el-dropdown-item command="d">链接邀请</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </div>
    <!-- 添加或修改租户对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="tenantFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="租户名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入租户名称" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="管理员名称" prop="contactUserName">
          <el-input v-model="form.contactUserName" placeholder="请输入管理员名称" />
        </el-form-item>
        <el-form-item v-else label="管理员名称" prop="adminUserId">
          <el-select v-model="form.adminUserId" filterable remote reserve-keyword placeholder="请选择管理员" :remote-method="getUserList" clearable style="width: 100%" @change="getUserEmail">
            <el-option v-for="item in userList" :key="item.userId" :label="item.userName" :value="item.userId" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="管理员名称" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员名" maxlength="30" />
        </el-form-item> -->
        <el-form-item label="管理员电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入管理员电话" />
        </el-form-item>
        <el-form-item label="管理员邮箱" prop="contactEmail">
          <el-input v-model="form.contactEmail" placeholder="请输入管理员邮箱" maxlength="30" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="管理员密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入管理员密码" maxlength="20" />
        </el-form-item>
        <el-form-item label="可使用应用" prop="menuIds">
          <el-select v-model="form.menuIds" multiple collapse-tags collapse-tags-tooltip placeholder="请选择应用" clearable style="width: 100%" @change="changeModel">
            <el-option v-for="item in menuOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 邮箱邀请 -->
    <UInviteMail v-model="dialogInviteMail.visible"></UInviteMail>

    <!-- 链接邀请 -->
    <UInviteLink v-model="dialogInviteLink.visible"></UInviteLink>
  </div>
</template>

<script setup name="Tenant" lang="ts">
import { listTenant, getTenant, delTenant, addTenant, updateTenant, changeTenantStatus, syncTenantPackage } from '@/api/system/tenant';
import { selectTenantPackage, addTenantPackage } from '@/api/system/tenantPackage';
import { treeselect as menuTreeselect, tenantPackageMenuTreeselect } from '@/api/system/menu';
import { TenantForm, TenantQuery, TenantVO } from '@/api/system/tenant/types';
import { listUser } from '@/api/system/user';
import { TenantPkgVO } from '@/api/system/tenantPackage/types';
import { Search } from '@element-plus/icons-vue';
import { MenuTreeOption } from '@/api/system/menu/types';
import { UInviteLink, UInviteMail } from '@/components/Business';
import { useBusinessDialog } from '@/components/Business/hooks/useBusinessDialog';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { dialogInviteLink, dialogInviteMail } = useBusinessDialog();

const tenantList = ref<TenantVO[]>([]);
const packageList = ref<TenantPkgVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const modelList = ref([]);
const userList = ref([]);

const queryFormRef = ref<ElFormInstance>();
const tenantFormRef = ref<ElFormInstance>();

const menuOptions = ref<MenuTreeOption[]>([]); // 应用
/** 查询菜单树结构 */
const getMenuTreeselect = async () => {
  const { data } = await menuTreeselect();
  data.forEach((item) => {
    item.id = String(item.id);
  });
  menuOptions.value = data;
  console.log('menuOptions.value', menuOptions.value);
};

const dialog = reactive<DialogOption>({
  visible: false,
  title: '新增租户'
});

const initFormData = {
  id: undefined,
  tenantId: undefined,
  contactUserName: '',
  contactPhone: '',
  contactEmail: '',
  username: '',
  adminUserId: '',
  password: '',
  companyName: '',
  licenseNumber: '',
  domain: '',
  address: '',
  intro: '',
  remark: '',
  menuId: [],
  menuIds: [],
  expireTime: '',
  accountCount: 0,
  status: '0',
  userId: '',
  packageId: 0
};
const menuIds = ref([]);
const data = reactive({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    tenantId: '',
    contactUserName: '',
    contactPhone: '',
    companyName: ''
  },
  rules: {
    id: [{ required: true, message: 'id不能为空', trigger: 'blur' }],
    tenantId: [{ required: true, message: '租户编号不能为空', trigger: 'blur' }],
    contactUserName: [
      { required: true, message: '用户名称不能为空', trigger: 'blur' },
      { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间', trigger: 'blur' }
    ],
    contactPhone: [{ required: true, message: '管理员电话不能为空', trigger: 'blur' }],
    companyName: [{ required: true, message: '租户名称不能为空', trigger: 'blur' }],
    userId: [{ required: true, message: '管理员不能为空', trigger: 'change' }],
    menuIds: [{ required: true, message: '应用不能为空', trigger: 'change' }],
    // username: [
    //   { required: true, message: '管理员不能为空', trigger: 'blur' },
    //   { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间', trigger: 'blur' }
    // ],
    password: [
      { required: true, message: '密码不能为空', trigger: 'blur' },
      { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
    ]
  }
});

const handleCommand = (type) => {
  switch (type) {
    case 'c':
      dialogInviteMail.go({})
      break
    case 'd':
      dialogInviteLink.go({})
      break
  }
}

const { queryParams, form, rules } = toRefs(data);

/** 查询所有租户套餐 */
const getTenantPackage = async () => {
  const res = await selectTenantPackage();
  packageList.value = res.data;
};

/** 查询租户列表 */
const getList = async () => {
  loading.value = true;
  const res = await listTenant(queryParams.value);
  tenantList.value = res.data;
  tenantList.value.forEach((item) => {
    if (!item.status) {
      item.status = '1';
    }
  });
  total.value = res.total;
  loading.value = false;
};

const changeModel = (id) => {
  const ids = [];
  // id.forEach((i) => {
  //   const finds = menuOptions.value.find((item) => item.id === i);
  //   deepIds(finds, ids);
  // });
  console.log('id', id);
  menuIds.value = ids;
};

const deepIds = (item, ids) => {
  ids.push(item.id);
  item.children &&
    item.children.forEach((child) => {
      deepIds(child, ids);
    });
};

// 租户套餐状态修改
const handleStatusChange = async (row: TenantVO) => {
  console.log('row', row);
  let text = row.status === '0' ? '启用' : '停用';
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.companyName + '"租户吗？');
    await changeTenantStatus(row.id, row.tenantId, row.status);
    proxy?.$modal.msgSuccess(text + '成功');
  } catch {
    row.status = row.status === '0' ? '1' : '0';
  }
};

const getUserEmail = (val) => {
  let finds = userList.value.find((item) => item.userId === val);
  console.log(finds);
  form.value.contactPhone = finds.phonenumber;
  form.value.contactEmail = finds.email;
  form.value.username = finds.userName;
  form.value.contactUserName = finds.userName;
  // form.value.username = find.
};

// 取消按钮
const cancel = () => {
  reset();
  dialog.visible = false;
};

// 表单重置
const reset = () => {
  modelList.value = [];
  form.value = { ...initFormData };
  tenantFormRef.value?.resetFields();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

// 多选框选中数据
const handleSelectionChange = (selection: TenantVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加租户';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: TenantVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  console.log('_id', _id);
  const res = await getTenant(_id);
  Object.assign(form.value, res.data);
  form.value.menuIds = res.data.menuIds && res.data.menuIds.split(', ');
  console.log('form.value.menuIds', form.value.menuIds);
  // if (res.data.menuIds) {
  //   menuIds.value = res.data.menuIds && res.data.menuIds.split(', ');
  //   form.value.menuId = menuOptions.value.filter((i) => menuIds.value.includes(String(i.id))).map((y) => y.id);
  // }
  dialog.visible = true;
  dialog.title = '修改租户';
};

/** 提交按钮 */
const submitForm = () => {
  tenantFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      form.value.username = form.value.contactUserName;
      // form.value.menuIds = menuIds.value;
      if (form.value.id) {
        await updateTenant(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addTenant(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: TenantVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除租户编号为"' + _ids + '"的数据项？');
  loading.value = true;
  await delTenant(_ids).finally(() => (loading.value = false));
  await getList();
  proxy?.$modal.msgSuccess('删除成功');
};

/** 同步租户套餐按钮操作 */
const handleSyncTenantPackage = async (row: TenantVO) => {
  try {
    await proxy?.$modal.confirm('是否确认同步租户套餐租户编号为"' + row.tenantId + '"的数据项？');
    loading.value = true;
    await syncTenantPackage(row.tenantId, row.packageId);
    await getList();
    proxy?.$modal.msgSuccess('同步成功');
  } catch {
    return;
  } finally {
    loading.value = false;
  }
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/tenant/export',
    {
      ...queryParams.value
    },
    `tenant_${new Date().getTime()}.xlsx`
  );
};

const getUserList = async (query) => {
  if (query) {
    let params = {
      pageNum: 1,
      pageSize: 10,
      userName: query
    };
    const { rows } = await listUser(params);
    userList.value = rows;
  }
};
onMounted(() => {
  getList();
  getMenuTreeselect();
});
</script>
