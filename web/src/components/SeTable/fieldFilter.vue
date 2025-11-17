<!-- 显示列 -->
<template>
  <div class="field-filter">
    <el-tooltip effect="dark" :content="$t('navbar.layout')" placement="top">
      <el-button link class="p-0 mr-2 field-filter-btn" :type="selectedLayoutId === '-1' ? '' : 'success'" @click="openDialog">
        <template #icon>
          <i class="iconfont se-dig-icon-a-File-display-onexianshiwenjian1" />
        </template>
      </el-button>
    </el-tooltip>

    <el-dialog v-model="open" title="" append-to-body draggable>
      <el-tabs v-model="selectedLayoutId" closable @tab-remove="removeTab" @tab-click="fieldFilterChange">
        <el-tab-pane
          v-for="item in layoutList"
          :key="item.columnSettingId"
          :name="item.columnSettingId"
          :label="item.columnSettingName"
          :value="item.columnSettingName"
        >
          <el-row :gutter="32" class="mb-2">
            <!-- 左边 -->
            <el-col :span="12">
              <div class="select-box">
                <!-- 可选列 -->
                <!-- <div class="total-number mb-3 ml-2">{{ $t('common.optional') }} ({{ fieldList.length }})</div> -->
                <div class="total-number mb-3 ml-2">选择 ({{ fieldList.length }})</div>
                <el-input v-model="searchValueAll" placeholder="搜索" prefix-icon="el-icon-search" clearable @change="handleFilterAll" />
                <div class="select-list" :style="{ height: height - 220 + 'px' }">
                  <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate" @change="selectAll">选择所有 </el-checkbox>
                  <el-checkbox-group v-model="selectedFieldName" class="field-list" @change="handleSelectChange">
                    <el-checkbox
                      v-for="(item, index) in fieldList"
                      v-show="item.isAllShow"
                      :key="index"
                      :label="item.fieldName"
                      :value="item.fieldName"
                    >
                      {{ item.title }}
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </div>
            </el-col>
            <!-- 右边 -->
            <el-col :span="12">
              <div class="select-box">
                <!-- <div class="total-number mb-3 ml-2">{{ $t('common.selected') }} ({{ selectedField.length }})</div> -->
                <div class="total-number mb-3 ml-2">选中 ({{ selectedField.length }})</div>
                <el-input v-model="searchValueSelect" placeholder="搜索" prefix-icon="el-icon-search" clearable @change="handleFilterSelect" />
                <div class="select-list" :style="{ height: height - 220 + 'px' }">
                  <!-- <span class="font-size-085">{{ $t('common.frozen') }} ({{ fixedField.length }})</span> -->
                  <span class="font-size-085">冻结 ({{ fixedField.length }})</span>
                  <el-checkbox-group v-model="fixedField" class="field-list">
                    <draggable
                      v-model="selectedField"
                      chosen-class="chosen"
                      ghost-class="ghost"
                      item-key="fieldName"
                      :force-fallback="true"
                      handle=".mover"
                      animation="1000"
                      @start="onStart"
                      @end="onEnd"
                    >
                      <template #item="{ element, index }">
                        <el-checkbox
                          class="mover"
                          :value="element.fieldName"
                          :label="element.title"
                          @change="(val) => handleFixedChange(element.fieldName, val)"
                        />
                      </template>
                    </draggable>
                  </el-checkbox-group>
                </div>
              </div>
            </el-col>
          </el-row>

          <!-- 创建模板 -->
          <!-- <el-popover :ref="`model-popover-${item.columnSettingId}`" width="340" trigger="click" placement="top-start">
            <el-form ref="templateForm" :model="formData" :rules="rules" label-position="top" label-width="120px">
              <el-form-item :label="$t('common.schemeName')" prop="filterName">
                <el-input v-model.trim="formData.filterName" :placeholder="$t('common.schemeNamePh')" :maxlength="20"></el-input>
              </el-form-item>
            </el-form>
            <div style="text-align: center; margin: 0">
              <el-button size="small" style="width: 120px" @click="cancel(`model-popover-${item.columnSettingId}`)">
                {{ $t('common.cancelBtn') }}
              </el-button>
              <el-button type="primary" size="small" style="width: 120px" @click="submit(`model-popover-${item.columnSettingId}`)">
                {{ $t('common.assureBtn') }}
              </el-button>
            </div>
            <template #reference>
              <el-button v-show="selectedLayoutId === '-1'" link>
                <span class="text-info">{{ $t('common.asTemplate') }}</span>
              </el-button>
            </template>
          </el-popover> -->
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>
<script setup>
import { computed, getCurrentInstance, onMounted, ref, watch } from 'vue';
import { addColumnSetting, delColumnSetting, treeListColumnSetting, updateColumnSetting } from '@/components/SeTable/columnSetting';
import draggable from 'vuedraggable';
import cache from '@/plugins/cache';
import { inject } from 'vue';
const emit = defineEmits(['update:modelValue', 'doLayout', 'getList']);
const { proxy } = getCurrentInstance();
const $t = proxy.$t;

const props = defineProps({
  fieldList: {
    type: Array,
    default: () => []
  },
  modelValue: {
    type: Array,
    default: () => {
      return [];
    }
  },
  filterKey: {
    require: true,
    type: String
  }
});
const open = ref(false);
// 控制全选
const checkAll = ref(false);
// 半选
const isIndeterminate = ref(true);
// 左侧所搜值
const searchValueAll = ref('');
// 右侧所搜值
const searchValueSelect = ref('');
// 布局方案 List：默认布局...
const layoutList = ref([]);
// 当前选择的布局方案
const selectedLayoutId = ref('-1');
// 冻结列
const fixedField = ref([]);
// 已选列表fieldName,即左侧侧list check状态
const selectedFieldName = ref([]);
const selectedField = ref([]);
const height = ref(null);
const formData = ref({
  filterName: ''
});

//refs
const templateForm = ref(null);

const rules = computed(() => {
  return {
    filterName: [
      {
        required: true,
        message: ' ', // 请输入方案名称
        trigger: 'blur'
      },
      {
        pattern: /^[\u4E00-\u9FA5A-Za-z0-9\s*]+$/,
        message: '仅支持中文、英文、数字', // 仅支持中文、英文、数字
        trigger: 'blur'
      }
    ]
  };
});

/** 监视列顺序 */
watch(
  () => selectedField.value,
  (val) => {
    // chooseData = val;
    emit('update:modelValue', val);
    const nameList = val.map((item) => item.fieldName);
    if (nameList.length) {
      selectedFieldName.value.forEach((item, index) => {
        if (nameList.findIndex((name) => name === item) === -1) {
          selectedFieldName.value.splice(index, 1);
        }
      });
    }
  }
);
/** 监视选中列 */
watch(
  () => selectedFieldName.value,
  (val) => {
    // 赋值selectedField
    selectedField.value = [];
    val.forEach((v) => {
      props.fieldList.forEach((item) => {
        item.fixed = false;
        if (item.fieldName === v) {
          selectedField.value.push(item);
        }
      });
    });
    // 冻结列
    fixedField.value.forEach((fixed) => {
      selectedField.value.forEach((item) => {
        if (fixed === item.fieldName) item.fixed = true;
      });
    });
  }
);

/** 打开创建模板对话框 */
function openDialog() {
  open.value = true;
}

/** 获取布局方案 */
function getFilterList() {
  const param = {
    columnSettingKey: props.filterKey + cache.local.get('plantId') + localStorage.getItem('username')
  };
  // 查询布局方案
  // treeListColumnSetting(param)
  //   .then((res) => {
  //     layoutList.value = [];
  //     if (res.data.length) {
  //       fixedField.value = [];
  //       // 默认显示所有列
  //       layoutList.value = res.data;
  //       // 找到最后更新的布局即所选布局
  //       let selectedLayout = layoutList.value[0];
  //       layoutList.value.forEach((item) => {
  //         if (item.updatedAt > selectedLayout.updatedAt) {
  //           selectedLayout = item;
  //         }
  //       });
  //       let selectedField = JSON.parse(selectedLayout.columnSettingContent);
  //       selectedLayoutId.value = selectedLayout.columnSettingId;
  //       selectedFieldName.value = selectedField.map((item) => item.fieldName);
  //       selectedField.forEach((item) => {
  //         if (item.fixed) fixedField.value.push(item.fieldName);
  //       });
  //     } else {
  //       selectedLayoutId.value = '-1';
  //       selectedFieldName.value = props.fieldList.map((item) => item.fieldName);
  //       isIndeterminate.value = false;
  //       checkAll.value = true;
  //     }
  //     layoutList.value.unshift({
  //       columnSettingName: '默认布局',
  //       columnSettingId: '-1',
  //       columnSettingContent: props.fieldList,
  //       updatedAt: '1999-1-1'
  //     });
  //     // 选中状态
  //     const checkedCount = selectedFieldName.value.length;
  //     checkAll.value = checkedCount === props.fieldList.length;
  //     isIndeterminate.value = checkedCount > 0 && checkedCount < props.fieldList.length;
  //   })
  //   .catch((error) => {
  //     console.log(error);
  //   });

  selectedLayoutId.value = '-1';
  selectedFieldName.value = props.fieldList.map((item) => item.fieldName);
  isIndeterminate.value = false;
  checkAll.value = true;
  layoutList.value.unshift({
    columnSettingName: '默认布局',
    columnSettingId: '-1',
    columnSettingContent: props.fieldList,
    updatedAt: '1999-1-1'
  });
  // 选中状态
  const checkedCount = selectedFieldName.value.length;
  checkAll.value = checkedCount === props.fieldList.length;
  isIndeterminate.value = checkedCount > 0 && checkedCount < props.fieldList.length;
}

/** 点击全选 */
function selectAll(val) {
  const defaultName = props.fieldList[0].fieldName;
  clearFixed();
  selectedFieldName.value = val ? props.fieldList.map((item) => item.fieldName) : [];
  if (val) {
    isIndeterminate.value = false;
  } else {
    // 当一个都没有选中的时候 默认选中第一个
    isIndeterminate.value = true;
    selectedFieldName.value = [defaultName];
  }
  saveUpdate();
  emit('doLayout');
}

/** 左侧选择布局变化时 */
function handleSelectChange(value) {
  if (!value.length) {
    const defaultName = props.fieldList[0].fieldName;
    selectedFieldName.value = [defaultName];
  }
  clearFixed();
  let checkedCount = selectedFieldName.value.length;
  checkAll.value = checkedCount === props.fieldList.length;
  isIndeterminate.value = checkedCount > 0 && checkedCount < props.fieldList.length;
  saveUpdate();
  emit('doLayout');
}

/** 右边冻结列选择变化时 */
function handleFixedChange(fieldName, val) {
  fixedField.value = [];
  let index = selectedField.value.findIndex((item) => item.fieldName === fieldName);
  if (val) {
    // 如果是冻结，index之前的全部冻结
    selectedField.value.forEach((item, i) => {
      if (i <= index) {
        item.fixed = true;
        fixedField.value.push(item.fieldName);
      }
    });
  } else {
    // 如果是取消冻结，index之后的全部取消冻结
    selectedField.value.forEach((item, i) => {
      if (i >= index) item.fixed = false;
      else fixedField.value.push(item.fieldName);
    });
  }
  saveUpdate();
}

/** 切换布局, 获取布局详情 */
function fieldFilterChange(tab) {
  proxy.$nextTick(() => {
    fixedField.value = [];
    if (tab.paneName === '-1') {
      selectedFieldName.value = props.fieldList.map((select) => select.fieldName);
    } else {
      layoutList.value.forEach((item) => {
        if (item.columnSettingId === selectedLayoutId.value) {
          selectedFieldName.value = JSON.parse(item.columnSettingContent).map((select) => select.fieldName);
          JSON.parse(item.columnSettingContent).forEach((select) => {
            if (select.fixed) fixedField.value.push(select.fieldName);
          });
        }
      });
    }
    const checkedCount = selectedFieldName.value.length;
    checkAll.value = checkedCount === props.fieldList.length;
    isIndeterminate.value = checkedCount > 0 && checkedCount < props.fieldList.length;
  });
}

/** 清空固定列 */
function clearFixed() {
  fixedField.value = [];
  selectedField.value.forEach((item) => (item.fixed = false));
}

/** 删除布局时 */
function removeTab(targetName) {
  if (targetName === '-1') {
    // proxy.$modal.notifyWarning($t('common.cannotDel'));
    proxy.$modal.notifyWarning('删除布局');
  } else {
    deleteFieldFilter(targetName);
  }
}

/** 开始拖拽事件 */
function onStart() {}

/** 拖拽结束事件 */
function onEnd() {
  clearFixed();
  saveUpdate();
  emit('getList');
}

/** 打开pop */
function openPop(refName) {}

/** 保存为模板 */
function submit(refName) {
  if (!templateForm.value) return;
  templateForm.value[0].validate((valid) => {
    if (!valid) return;
    const param = {
      columnSettingKey: props.filterKey + cache.local.get('plantId') + localStorage.getItem('username'),
      columnSettingId: 0,
      columnSettingName: formData.value.filterName,
      columnSettingContent: JSON.stringify(props.modelValue)
    };
    // addColumnSetting(param)
    //   .then((data) => {
    //     proxy.$modal.notifySuccess($t('common.saveSuccess'));
    //     getFilterList();
    //   })
    //   .catch((error) => {
    //     console.log(error);
    //   })
    //   .finally(() => {
    //     cancel(refName);
    //   });
  });
}

/** 取消创建模板 */
function cancel(refName) {
  templateForm.value[0].resetFields();
  proxy.$refs[refName][0].hide();
}

/** 更新模板 */
function saveUpdate() {
  if (selectedLayoutId.value > -1) {
    const param = {
      columnSettingKey: props.filterKey + cache.local.get('plantId') + localStorage.getItem('username'),
      columnSettingId: selectedLayoutId.value,
      columnSettingName: '',
      columnSettingContent: JSON.stringify(props.modelValue)
    };
    // updateColumnSetting(param)
    //   .then(() => {})
    //   .catch((error) => {
    //     console.log(error);
    //   });
  }
}

/** 删除模板 */
function deleteFieldFilter(id) {
  const cv = layoutList.value.find((item) => {
    return item.filterId === selectedLayoutId.value;
  });
  let str = '当前布局方案'; // '当前布局方案'
  if (cv && cv.filterName) {
    str = cv.filterName;
  }
  proxy.$modal
    .confirm('确认删除' + str)
    .then(() => {
      // delColumnSetting(id)
      //   .then((data) => {
      //     proxy.$modal.notifySuccess($t('common.deleteSuccess'));
      //     getFilterList();
      //   })
      //   .catch((error) => {
      //     console.log(error);
      //   });
    })
    .catch((error) => {
      console.log(error);
    });
}

/**左侧筛选*/
function handleFilterAll(val) {
  if (val) {
    props.fieldList.forEach((item) => {
      if (item.title.includes(val)) item.isAllShow = true;
      else item.isAllShow = false;
    });
  } else {
    props.fieldList.forEach((item) => (item.isAllShow = true));
  }
}

/**右侧筛选*/
function handleFilterSelect(val) {
  if (val) {
    selectedField.value.forEach((item) => {
      if (item.title.includes(val)) item.isSelectShow = true;
      else item.isSelectShow = false;
    });
  } else {
    selectedField.value.forEach((item) => (item.isSelectShow = true));
  }
}

onMounted(() => {
  height.value = window.innerHeight - 104;
  window.addEventListener('resize', () => {
    height.value = window.innerHeight - 104;
  });
});
// 从服务器获取布局方案
getFilterList();

defineExpose({
  fixedField
});
</script>
<style lang="scss" scoped>
/* Set a 8px margin on the right */
.mr-2 {
  margin-right: 8px !important;
}
/* Set a 0 padding to all sides */
.p-0 {
  padding: 0 !important;
}
/* Set a 8px margin on the bottom */
.mb-2 {
  margin-bottom: 8px !important;
}
/* Set a 16px margin on the bottom */
.mb-3 {
  margin-bottom: 16px !important;
}
/* Set a 8px margin on the left */
.ml-2 {
  margin-left: 8px !important;
}
.font-size-085 {
  // 14px
  font-size: 0.875rem !important;
}
.text-info {
  color: #1890ff;
}

:deep(.el-drawer__header) {
  text-align: left;
}

.field-filter {
  font-size: 14px;

  display: inline-flex;
  justify-content: flex-end;

  :deep(.field-filter-btn) {
    .el-icon {
      top: 2px;
    }
  }
}
.select-box {
  .select-list {
    min-height: 150px;
    padding: 8px 16px;
    border: solid 1px #dce5de;
    border-top: none;
    border-bottom-left-radius: 2px;
    border-bottom-right-radius: 2px;
    overflow-y: auto;

    .field-list {
      :deep(.el-checkbox) {
        display: block;
        height: 32px;
        line-height: 32px;
      }
    }
  }
}
</style>
