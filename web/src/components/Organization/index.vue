<template>
  <el-dialog v-model="dialogVisible" title="创建组织" width="500" :before-close="handleClose">
    <el-form ref="formRef" :model="wordForm" :rules="wordRules" label-width="110">
      <el-form-item prop="companyName" label="组织名称:">
        <el-input v-model="wordForm.companyName" auto-complete="off" placeholder="请输入组织名称"></el-input>
      </el-form-item>
      <el-form-item prop="contactUserName" label="管理员:">
        <el-input v-model="wordForm.contactUserName" disabled placeholder="请输入管理员名称" />
        <!-- <el-select
          v-model="wordForm.adminUserId"
          filterable
          remote
          reserve-keyword
          placeholder="请选择管理员"
          :remote-method="getUserList"
          clearable
          style="width: 100%"
          @change="changeUser"
        >
          <el-option v-for="item in userList" :key="item.userId" :label="item.userName" :value="item.userId" />
        </el-select> -->
      </el-form-item>
      <el-form-item label="管理员密码:" prop="password">
        <el-input v-model="wordForm.password" type="password" placeholder="请输入管理员密码" maxlength="20" />
      </el-form-item>
      <el-form-item prop="email" label="管理员邮箱:">
        <el-input v-model="wordForm.contactEmail" auto-complete="off" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item prop="phone" label="管理员手机号:">
        <el-input v-model="wordForm.contactPhone" auto-complete="off" placeholder="请输入手机号"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="updatePassword(formRef)"> 确认 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { addTenant } from '@/api/system/tenant';
import { listUserTenant } from '@/api/system/user';
const { proxy } = getCurrentInstance();
const props = defineProps({
  organ: {
    type: Boolean,
    default: () => {
      return false;
    }
  },
  userInfo: {
    type: Object,
    default: () => {}
  }
});

const emit = defineEmits(['closeOrgan']);
const dialogVisible = ref(true);
const formRef = ref(null);
const wordForm = ref({
  companyName: '',
  contactUserName: '',
  contactEmail: '',
  contactPhone: '',
  password: '',
  packageId: 0,
  accountCount: 0,
  status: '0',
  menuIds: []
});
const userList = ref([]);
const wordRules = {
  companyName: [{ required: true, trigger: 'blur', message: '请输入您的组织' }],
  contactUserName: [{ required: true, trigger: 'change', message: '请输入您的用户' }],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
  ]
};
watch(
  () => props.organ,
  async (newValue) => {
    console.log('newValue', newValue);
    dialogVisible.value = newValue;
    wordForm.value.contactUserName = props.userInfo.user.userName;
    wordForm.value.contactPhone = props.userInfo.user.phonenumber;
    wordForm.value.contactEmail = props.userInfo.user.email;
  },
  { immediate: true, deep: true }
);
const handleClose = () => {
  dialogVisible.value = false;
  formRef.value.resetFields();
  emit('closeOrgan');
};

const updatePassword = () => {
  formRef.value?.validate(async (valid, fields) => {
    if (valid) {
      await addTenant(wordForm.value).then((res) => {
        dialogVisible.value = false;
        proxy?.$modal.msgSuccess('创建成功');
        emit('closeOrgan');
      });
    }
  });
};
const changeUser = (val) => {
  const finds = userList.value.find((item) => item.userId === val);
  console.log(finds);
  wordForm.value.contactPhone = finds.phonenumber;
  wordForm.value.contactEmail = finds.email;
  wordForm.value.username = finds.userName;
  wordForm.value.contactUserName = finds.userName;
};
const getUserList = async (query) => {
  if (query) {
    let params = {
      pageNum: 1,
      pageSize: 10,
      userName: query
    };
    const { rows } = await listUserTenant(params);
    userList.value = rows;
  }
};
</script>
<style scoped lang="scss"></style>
