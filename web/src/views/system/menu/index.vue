<template>
  <div class="p-15" style="height: 100%">
    <div class="p-card">
      <el-row :gutter="10" style="margin-bottom: 10px">
        <el-col :span="1.5">
          <el-button v-hasPermi="['system:menu:add']"  link icon="Plus" @click="handleAdd()">新增 </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button link style="color: #3679de" icon="Sort" @click="handleToggleExpandAll">展开/折叠</el-button>
        </el-col>
      </el-row>
      <el-table
        ref="menuTableRef"
        v-loading="loading"
        :data="menuList"
        border
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="isExpandAll"
      >
        <el-table-column prop="name" label="菜单名称" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="" label="英文名称"></el-table-column>
        <el-table-column prop="icon" label="图标" align="center" width="100">
          <template #default="scope">
            <svg-icon :icon-class="scope.row.icon" />
          </template>
        </el-table-column>
        <el-table-column prop="isFrame" label="是否外链" width="100">
          <template #default="scope">
            <span>{{ scope.row.isFrame === true ? '是' : '不是' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="perms" label="权限字符" width="200" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="queryParam" label="路由参数" width="200"></el-table-column>
        <el-table-column prop="isCache" label="是否缓存" width="100">
          <template #default="scope">
            <span>{{ scope.row.isCache === true ? '缓存' : '不缓存' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="显示状态" width="100">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="" label="可用状态" width="100">
          <!-- <template #default="scope">
            <span>{{ scope.row.status }}</span>
          </template> -->
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createdAt">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:menu:edit']"  link style="font-size: 16px" icon="Edit" @click="handleUpdate(scope.row)" />
            </el-tooltip>
            <el-tooltip content="新增" placement="top">
              <el-button v-hasPermi="['system:menu:add']"  link style="font-size: 16px" icon="Plus" @click="handleAdd(scope.row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:menu:remove']"  link style="font-size: 16px" icon="Delete" @click="handleDelete(scope.row)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.title" destroy-on-close append-to-bo style="height: 540px" width="750px">
      <el-form ref="menuFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.pid"
                :data="menuOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="选择上级菜单"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">目录</el-radio>
                <el-radio value="C">菜单</el-radio>
                <el-radio value="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="24">
            <el-form-item label="菜单图标" prop="icon">
              <!-- 图标选择器 -->
              <icon-select v-model="form.icon" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.sort" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="12">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon> </el-tooltip
                  >是否外链
                </span>
              </template>
              <el-radio-group v-model="form.isFrame">
                <el-radio :value="true">是</el-radio>
                <el-radio :value="false">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="12">
            <el-form-item prop="path">
              <template #label>
                <span>
                  <el-tooltip content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头" placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon>
                  </el-tooltip>
                  路由地址
                </span>
              </template>
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType === 'C'" :span="12">
            <el-form-item prop="component">
              <template #label>
                <span>
                  <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon>
                  </el-tooltip>
                  组件路径
                </span>
              </template>
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'M'" :span="12">
            <el-form-item>
              <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
              <template #label>
                <span>
                  <el-tooltip content="控制器中定义的权限字符，如：@SaCheckPermission('system:user:list')" placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon>
                  </el-tooltip>
                  权限字符
                </span>
              </template>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType === 'C'" :span="12">
            <el-form-item>
              <el-input v-model="form.queryParam" placeholder="请输入路由参数" maxlength="255" />
              <template #label>
                <span>
                  <el-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`' placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon>
                  </el-tooltip>
                  路由参数
                </span>
              </template>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType === 'C'" :span="12">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致" placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon>
                  </el-tooltip>
                  是否缓存
                </span>
              </template>
              <el-radio-group v-model="form.isCache">
                <el-radio :value="true">缓存</el-radio>
                <el-radio :value="false">不缓存</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="12">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon>
                  </el-tooltip>
                  显示状态
                </span>
              </template>
              <el-radio-group v-model="form.hidden">
                <el-radio v-for="dict in sys_show_hide" :key="dict.value" :value="dict.value">{{ dict.label }} </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                    <el-icon>
                      <question-filled />
                    </el-icon>
                  </el-tooltip>
                  菜单状态
                </span>
              </template>
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
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

<script setup name="Menu" lang="ts">
import { addMenu, delMenu, getMenu, listMenu, updateMenu } from '@/api/system/menu';
import { MenuForm, MenuQuery, MenuVO } from '@/api/system/menu/types';
import { MenuTypeEnum } from '@/enums/MenuTypeEnum';

interface MenuOptionsType {
  id: number;
  name: string;
  children: MenuOptionsType[] | undefined;
}

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_show_hide, sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_show_hide', 'sys_normal_disable'));

const menuList = ref<MenuVO[]>([]);
const loading = ref(false);
const showSearch = ref(true);
const menuOptions = ref<MenuOptionsType[]>([]);
const isExpandAll = ref(false);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const queryFormRef = ref<ElFormInstance>();
const menuFormRef = ref<ElFormInstance>();
const initFormData = {
  path: '',
  id: undefined,
  pid: 0,
  name: '',
  icon: '',
  menuType: MenuTypeEnum.M,
  sort: 1,
  isFrame: false,
  isCache: true,
  hidden: false,
  status: '0'
};
const data = reactive<PageData<MenuForm, MenuQuery>>({
  form: { ...initFormData },
  queryParams: {
    name: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
    sort: [{ required: true, message: '菜单顺序不能为空', trigger: 'blur' }],
    path: [{ required: true, message: '路由地址不能为空', trigger: 'blur' }]
  }
});

const menuTableRef = ref<ElTableInstance>();

const { queryParams, form, rules } = toRefs<PageData<MenuForm, MenuQuery>>(data);
/** 查询菜单列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMenu(queryParams.value);
  if (res.data) {
    menuList.value = res.data;
  }
  loading.value = false;
};
/** 查询菜单下拉树结构 */
const getTreeselect = async () => {
  menuOptions.value = [];
  const response = await listMenu();
  console.log('response111111', response.data)
  const menu: MenuOptionsType = { id: 0, name: '主类目', children: [] };
  menu.children = proxy?.handleTree<MenuOptionsType>(response.data, 'id');
  menuOptions.value.push(menu);
};
/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};
/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  menuFormRef.value?.resetFields();
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
/** 新增按钮操作 */
const handleAdd = (row) => {
  console.log('sys_show_hide', sys_show_hide)
  reset();
  getTreeselect();
  row && row.id ? (form.value.pid = row.id) : (form.value.pid = 0);
  dialog.visible = true;
  dialog.title = '添加菜单';
};
/** 展开/折叠操作 */
const handleToggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value;
  toggleExpandAll(menuList.value, isExpandAll.value);
};
/** 展开/折叠所有 */
const toggleExpandAll = (data: MenuVO[], status: boolean) => {
  data.forEach((item: MenuVO) => {
    menuTableRef.value?.toggleRowExpansion(item, status);
    if (item.children && item.children.length > 0) toggleExpandAll(item.children, status);
  });
};
/** 修改按钮操作 */
const handleUpdate = async (row) => {
  console.log('row', sys_normal_disable)
  reset();
  await getTreeselect();
  if (row.id) {
    const { data } = await getMenu(row.id);
    form.value = data;

    console.log('form', form.value)
  }
  dialog.visible = true;
  dialog.title = '修改菜单';
};
/** 提交按钮 */
const submitForm = () => {
  menuFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      form.value.id ? await updateMenu(form.value) : await addMenu(form.value);
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};
/** 删除按钮操作 */
const handleDelete = async (row) => {
  await proxy?.$modal.confirm('是否确认删除名称为"' + row.name + '"的数据项?');
  await delMenu(row.id);
  await getList();
  proxy?.$modal.msgSuccess('删除成功');
};

onMounted(() => {
  getList();
});
</script>
