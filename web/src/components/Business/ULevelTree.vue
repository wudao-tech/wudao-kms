<template>
  <div class="tree-wrap">
    <el-row class="search-wrap" justify="space-between" align="middle">
      <el-input :suffix-icon="Search" v-model="filterKey" placeholder="搜索" clearable></el-input>

      <el-dropdown trigger="click">
        <el-button text><i class="iconfont se-dig-icon-a-Group39"></i></el-button>

        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="t in operate.rootList" :key="t.type" @click="operate.trigger(t.type, null)">
              <i :class="['iconfont', t.icon]"></i>
              <span class="operate-text">{{ t.label }}</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-row>

    <el-scrollbar class="tree-box">
      <el-tree v-if="defaultCheckedKeys" ref="treeRef" :data="data" :props="props" @node-click="nodeClick" :highlight-current="true" default-expand-all :expand-on-click-node="false" node-key="id" :current-node-key="defaultCheckedKeys" :filter-node-method="filterNode">
        <template #default="{ node }">
          <div class="custom-tree-node">
            <svg-icon icon-class="tree-icon" class="el-input__icon input-icon" />
            <div class="node-label oneline" :title="node.label">{{ node.label }}</div>

            <el-dropdown trigger="click" >
              <el-button class="operate-btn" text @click.stop><i class=" iconfont se-dig-icon-a-Group39"></i></el-button>

              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="t in operate.childList" :key="t.type" @click.stop="operate.trigger(t.type, node)">
                    <i :class="['iconfont', t.icon]"></i>
                    <span class="operate-text">{{ t.label }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-tree>
    </el-scrollbar>

    <UEquipGroup v-model="dialogEquipGroup.visible" :data="dialogEquipGroup.data" @update:group="updateGroup" @update:equip="updateEquip"></UEquipGroup>

    <UUploadTree v-model="dialogUploadTree.visible" :data="dialogUploadTree.data"></UUploadTree>
  </div>
</template>
<script setup>
import { Search } from '@element-plus/icons-vue'
import { nextTick, ref, watch, getCurrentInstance } from 'vue'
import { UEquipGroup, UUploadTree } from '@/components/Business/'
import { useBusinessDialog } from '@/components/Business/hooks/useBusinessDialog'
import { ElMessageBox } from 'element-plus'
import { ApiGroup } from '@/api/Business'

const prop = defineProps({
  data: {
    type: Array,
    required: true
  },
  props: {
    type: Object,
    default: () => {
      return {}
    }
  },
  checkedGroup: {
    type: String
  }
})
const {proxy} = getCurrentInstance()
const { dialogEquipGroup, dialogUploadTree } = useBusinessDialog()

const treeRef = ref()
const filterKey = ref()
watch(filterKey, (val) => {
  treeRef.value.filter(val)
})
const filterNode = (value, data) => {
  if (!value) return true
  return data.name.includes(value)
}
const emits = defineEmits(['node-click', 'update:group', 'update:equip'])

const nodeClick = (...args) => {
  emits('node-click', args[0].id)
}

const updateGroup = () => {
  emits('update:group')
}
const updateEquip = () => {
  emits('update:equip')
}

const defaultCheckedKeys = ref()
const setDefaultChecked = (val) => {

  emits('node-click', val)
  nextTick(() => {
    defaultCheckedKeys.value = `${val}`
  })
}

watch(() => prop.checkedGroup, () => {
  // (!defaultCheckedKeys.value) && setDefaultChecked()
  if (prop.checkedGroup) {
    console.log('prop.checkedGroup', prop.checkedGroup)
    setDefaultChecked(prop.checkedGroup)
  }
}, {
  immediate: true,
  deep: true
})

const operate = reactive({
  rootList: [
    {
      type: 'addGroup',
      label: '添加分组',
      icon: 'se-dig-icon-a-Plusjia'
    },
    {
      type: 'sync',
      label: '数据同步',
      icon: 'se-dig-icon-a-Data-displayshujuxianshi'
    },
    {
      type: 'import',
      label: '导入',
      icon: 'se-dig-icon-a-Loginjinru'
    },
    {
      type: 'export',
      label: '导出',
      icon: 'se-dig-icon-a-Logouttuichu'
    }
  ],
  childList: [
    {
      type: 'addGroup',
      label: '子分组',
      icon: 'se-dig-icon-a-Plusjia'
    },
    {
      type: 'addEquip',
      label: '新增设备',
      icon: 'se-dig-icon-a-Plusjia'
    },
    {
      type: 'editGroup',
      label: '编辑',
      icon: 'se-dig-icon-cds-edit-batch'
    },
    {
      type: 'del',
      label: '删除',
      icon: 'se-dig-icon-a-Deleteshanchu'
    }
  ],
  trigger(type, node) {
    switch (type) {
      case 'addGroup':
      case 'addEquip':
      case 'editGroup':
        dialogEquipGroup.go({
          type,
          node,
          tree: prop.data,
          props: prop.props
        })
        break
      case 'import':
        dialogUploadTree.go({})
        break
      case 'del':
        ElMessageBox.confirm(`确认删除分组【${node.label}】?`)
          .then(() => {
            return ApiGroup({
              method: 'delete',
              params: {id: node.key}
            })
          }).then((res) => {
            if (res.code === 'ok') {
              proxy.$modal.msgSuccess('成功删除')
              updateGroup()
            }
          })
          .catch(() => {
            // catch error
          })
        break
    }
  }
})

</script>
<style lang='scss' scoped>
.tree-wrap {
  display: flex;
  flex-direction: column;
  flex: 0 0 208px;
  height: 100%;
  overflow: hidden;
  padding: 12px 0;
  background: #fff;
  .search-wrap {
    padding: 0 0 8px 12px;
    .el-input {
      width: 148px;
    }
  }
  :deep(.el-tree-node__content) {
    height: 38px;
  }
  .tree-box {
    flex: 1;
    overflow: auto;
  }
}
.custom-tree-node {
  flex: 1;
  display: flex;
  height: 38px;
  align-items: center;
  overflow: hidden;
  &:hover {
    .operate-btn {
      opacity: 1;
    }
  }
  .node-label {
    margin-left: 6px;
    flex: 1;
    overflow: hidden;
  }
  .operate-btn {
    opacity: 0;
  }
}
.operate-text {
  margin-left: 4px;
}
</style>
