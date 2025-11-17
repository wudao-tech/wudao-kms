<template>
  <div class="p-2">
    <!-- <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="search">
        <el-form ref="queryFormRef" :model="queryParams" :inline="true">
          <el-form-item label="用户名称" prop="username">
            <el-input v-model="queryParams.username" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phoneNumber">
            <el-input v-model="queryParams.phoneNumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </transition> -->
    <el-card shadow="never">
      <el-row :gutter="10" style="margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center;">
        <div style="display: flex">
          <el-col :span="1.5">
            <el-button link icon="Plus" size="small" @click="openSelectUser">添加用户</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button  link icon="CircleClose" size="small" :disabled="multiple" @click="cancelAuthUserAll">
              批量取消授权
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button link icon="Back" size="small" @click="handleClose">返回</el-button>
          </el-col>
        </div>
        <el-form ref="queryFormRef" :model="queryParams" :inline="true" style="height: 32px;">
          <el-form-item label="用户名称" prop="username">
            <el-input v-model="queryParams.username" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phoneNumber">
            <el-input v-model="queryParams.phoneNumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item style="margin-right: 0;">
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <!-- <right-toolbar v-model:showSearch="showSearch" :search="true" @query-table="getList"></right-toolbar> -->
      </el-row>
      <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange"  :max-height="530" border>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="用户名称" prop="username" :show-overflow-tooltip="true" />
        <el-table-column label="用户昵称" prop="nickname" :show-overflow-tooltip="true" />
        <el-table-column label="邮箱" prop="email" :show-overflow-tooltip="true" />
        <el-table-column label="手机" prop="phoneNumber" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createdAt" width="220">
          <template #default="scope">
            <span>{{ scope.row.createdAt }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="80" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="取消授权" placement="top">
              <el-button  link icon="CircleClose" @click="cancelAuthUser(scope.row)"> </el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
      <select-user ref="selectRef" :role-id="queryParams.roleId" @ok="handleQuery" />
    </el-card>
  </div>
</template>

<script setup name="AuthUser" lang="ts">
import { allocatedUserList, authUserCancel, authUserCancelAll } from '@/api/system/role';
import { UserQuery } from '@/api/system/user/types';
import { UserVO } from '@/api/system/user/types';
import SelectUser from './selectUser.vue';
import { RouteLocationNormalized } from 'vue-router';

const route = useRoute();
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

const userList = ref<UserVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const multiple = ref(true);
const total = ref(0);
const userIds = ref<Array<string | number>>([]);

const queryFormRef = ref<ElFormInstance>();
const selectRef = ref<InstanceType<typeof SelectUser>>();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleId: route.params.roleId as string,
  username: undefined,
  phoneNumber: ''
});

/** 查询授权用户列表 */
const getList = async () => {
  loading.value = true;
  const res = await allocatedUserList(queryParams);
  userList.value = res.data;
  total.value = res.total;
  loading.value = false;
};
// 返回按钮
const handleClose = () => {
  const obj: RouteLocationNormalized = {
    path: '/system/permission/role',
    fullPath: '',
    hash: '',
    matched: [],
    meta: undefined,
    name: undefined,
    params: undefined,
    query: undefined,
    redirectedFrom: undefined
  };
  proxy?.$tab.closeOpenPage(obj);
};
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};
// 多选框选中数据
const handleSelectionChange = (selection) => {
  userIds.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
};
/** 打开授权用户表弹窗 */
const openSelectUser = () => {
  selectRef.value?.show();
};
/** 取消授权按钮操作 */
const cancelAuthUser = async (row) => {
  await proxy?.$modal.confirm('确认要取消该用户"' + row.username + '"角色吗？');
  await authUserCancel({ userId: row.userId, roleId: queryParams.roleId });
  await getList();
  proxy?.$modal.msgSuccess('取消授权成功');
};
/** 批量取消授权按钮操作 */
const cancelAuthUserAll = async () => {
  const roleId = queryParams.roleId;
  const uIds = userIds.value.join(',');
  await proxy?.$modal.confirm('是否取消选中用户授权数据项?');
  await authUserCancelAll({ roleId: roleId, userIds: uIds });
  await getList();
  proxy?.$modal.msgSuccess('取消授权成功');
};

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped></style>
