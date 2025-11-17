<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/no-mutating-props -->
<!--表头：排序、冻结、筛选-->
<template>
  <el-popover v-model:visible="visible" placement="bottom" :width="220" trigger="click" @hide="hidePopover">
    <!--  popover 内容  -->
    <div>
      <div class="column-list">
        <!--  升序    -->
        <el-button v-show="!field.noSort" link icon="el-icon-SortUp" style="text-align: left" @click="handleAsc">
          升序
          <el-icon v-if="field.isAsc" class="selected text-success"><Select /></el-icon>
        </el-button>
        <!--  降序     -->
        <el-button v-show="!field.noSort" link icon="el-icon-SortDown" @click="handleDes">
          降序
          <el-icon v-if="field.isDes" class="selected text-success"><Select /></el-icon>
        </el-button>
        <!--  冻结    -->
        <el-button v-if="!field.fixed" link icon="el-icon-Lock" @click="handleFreeze"> 冻结 </el-button>
        <!--  取消冻结    -->
        <el-button v-if="field.fixed" link icon="el-icon-Unlock" @click="handleUnfreeze"> 取消冻结 </el-button>
        <!--  隐藏此列  -->
        <el-button v-if="isHide" link icon="el-icon-hide" @click="handleHide"> 隐藏此列 </el-button>
      </div>
      <div v-show="!field.noSearch" class="filter-footer">
        <el-divider />
        <!--  筛选 -->
        <div class="my-3">
          <i class="iconfont se-dig-icon-a-Filtershaixuan" />
          筛选
          <el-button icon="el-icon-plus" link type="primary" @click="addCondition">添加筛选项</el-button>
        </div>

        <el-form ref="form" class="filter-box" :model="field" label-width="0">
          <el-row v-for="(item, index) in field.conditions" :key="index" class="mb-2">
            <!--  删除按钮  -->
            <el-col :span="4" class="pt-1 align-right">
              <el-button class="mr-1" icon="el-icon-delete" link @click="handleDel(index)" />
            </el-col>
            <!--  筛选公式  -->
            <el-col :span="20" class="filter-condition">
              <el-form-item :prop="`conditions.${index}.type`" :rules="data.commonRules.selectRequired">
                <el-select v-model="item.type" placeholder="Select" :teleported="false" @change="(val) => handleChangeType(val, item)">
                  <el-option
                    v-for="(typeOption, index) in typeOptions"
                    v-show="typeOption?.visible"
                    :key="index"
                    :label="typeOption.label"
                    :value="typeOption.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <!--  筛选值  -->
            <el-col :span="20" :offset="4" class="filter-condition">
              <!--  输入框筛选  -->
              <template v-if="getValueCategory(field.dataType, item.type) === 'input'">
                <!--  介于 有两个输入框  -->
                <template v-if="item.type === 'between'">
                  <el-row>
                    <el-col :span="11">
                      <el-form-item :prop="`conditions.${index}.value1`" :rules="data.commonRules.inputRequired">
                        <el-input ref="value1" v-model="item.value1" clearable @input="onChange" @keyup="item.value1 = numberFormat(item.value1)" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="2" class="align-center mt-2">-</el-col>
                    <el-col :span="11">
                      <template>
                        <el-form-item :prop="`conditions.${index}.value2`" :rules="data.commonRules.inputRequired">
                          <el-input ref="value2" v-model="item.value2" clearable @input="onChange" @keyup="item.value2 = numberFormat(item.value2)" />
                        </el-form-item>
                      </template>
                    </el-col>
                  </el-row>
                </template>
                <!--  1个输入框情况  -->
                <template v-else>
                  <el-form-item :prop="`conditions.${index}.value`" :rules="data.commonRules.inputRequired">
                    <el-input key="value" v-model="item.value" clearable @input="onChange" @keyup="item.value = numberFormat(item.value)" />
                  </el-form-item>
                </template>
              </template>

              <!--  下拉框筛选  -->
              <template v-else-if="getValueCategory(field.dataType, item.type) === 'select'">
                <el-form-item :prop="`conditions.${index}.value`" :rules="data.commonRules.selectRequired">
                  <el-select v-model="item.value" placeholder="Select" :teleported="false">
                    <el-option
                      v-for="(valueOption, index) in field.valueOptions"
                      :key="index"
                      :label="valueOption.label"
                      :value="valueOption.value"
                    />
                  </el-select>
                </el-form-item>
              </template>

              <!--  时间筛选  -->
              <template v-else-if="getValueCategory(field.dataType, item.type) === 'date'">
                <!--  介于 有两个选择框  -->
                <el-form-item :prop="`conditions.${index}.value`" :rules="data.commonRules.selectRequired">
                  <el-date-picker
                    v-if="item.type === 'between'"
                    :key="item.type + 'range'"
                    v-model="item.value"
                    class="w100p mb-2"
                    type="datetimerange"
                    range-separator="-"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    :teleported="false"
                  >
                  </el-date-picker>
                  <!--  处于 下拉选择  -->
                  <el-select v-else-if="item.type === 'within'" v-model="item.value" :teleported="false" @change="onChange">
                    <el-option v-for="(dateOption, index) in dateOptions" :key="index" :label="dateOption.label" :value="dateOption.value" />
                  </el-select>
                  <!--  一个日期  -->
                  <el-date-picker
                    v-else
                    :key="item.type"
                    v-model="item.value"
                    class="w100p mb-2"
                    type="datetime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="选择日期时间"
                    :teleported="false"
                  >
                  </el-date-picker>
                </el-form-item>
              </template>
            </el-col>
          </el-row>
        </el-form>

        <el-divider />
        <!--  筛选、清除按钮  -->
        <div class="buttons">
          <el-button size="small" icon="el-icon-refreshLeft" title="清除" @click="clearClick" />
          <el-button class="filter_btn font-size-08" size="small" type="primary" title="筛选、清除按钮" @click="filterClick">
            <template #icon>
              <el-icon class="iconfont se-dig-icon-a-Filtershaixuan" />
            </template>
          </el-button>
        </div>
      </div>
    </div>

    <!--  表头 内容  -->
    <template #reference>
      <span :ref="field.fieldName" class="no-wrap-header" @mouseover="onMouseOver(field.fieldName)">
        <div class="header-content" @mouseenter.stop="textOverflow($event.target, field)">
          <el-tooltip effect="dark" :disabled="field.tipShow" :content="field.title" placement="top-start">
            {{ field.title }}
          </el-tooltip>
        </div>
        <!-- 更多 -->
        <el-icon v-if="isMoreShow" class="more fr"> <MoreFilled /> </el-icon>
        <!-- 筛选 + 降序 -->
        <el-icon v-else-if="isFilterAsc" class="iconfont se-dig-icon-a-Filtershaixuanshang fr text-success" />
        <!-- 筛选 + 升序 -->
        <el-icon v-else-if="isFilterDes" class="iconfont se-dig-icon-a-Filtershaixuanxia fr text-success" />
        <!-- 升序 -->
        <el-icon v-else-if="field.isAsc" class="fr text-success"><SortUp /></el-icon>
        <!-- 降序 -->
        <el-icon v-else-if="field.isDes" class="fr text-success"><SortDown /></el-icon>
        <!-- 筛选 -->
        <el-icon v-else-if="field.isFilter" class="iconfont se-dig-icon-a-Filtershaixuan fr text-success font-size-080" />
      </span>
      <!--        </el-tooltip>-->
    </template>
  </el-popover>
</template>

<script setup>
import { computed, inject, reactive, ref, unref, watchEffect, watch, getCurrentInstance } from 'vue';
import { MoreFilled, SortUp, SortDown } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const props = defineProps({
  field: Object,
  isHide: Boolean,
  // 日期类型 处于选项
  dateOptions: Array
});
// const language = computed(() => proxy.$store.getters.language.toString());
const language = computed(() => 'zh');

/**------------数据-------------*/
// dom ref
let form = ref(null);
// 下拉框式否显示
let visible = ref(false);
// 数据
let data = reactive({
  form: {
    // 筛选公式
    type: undefined,
    // 筛选值
    value: undefined,
    value1: undefined,
    value2: undefined
  },
  commonRules: {
    inputRequired: [{ required: true, message: ' ', trigger: 'blur' }],
    selectRequired: [{ required: true, message: ' ', trigger: 'change' }]
  }
});
// 计算属性
// 是否显示more按钮
const isMoreShow = computed(() => {
  return !(props.field.isAsc || props.field.isDes || props.field.isFilter || isFilterAsc.value || isFilterDes.value);
});
// 是否筛选+升序
const isFilterAsc = computed(() => {
  return props.field.isAsc && props.field.isFilter;
});
// 是否筛选+降序
const isFilterDes = computed(() => {
  return props.field.isDes && props.field.isFilter;
});
// 筛选方法下拉选项
const typeOptions = computed(() => {
  const dataType = props.field.dataType;
  return [
    {
      label: language.value === 'zh' ? '为空' : 'is empty',
      value: "=''",
      visible: true
    },
    {
      label: language.value === 'zh' ? '不为空' : 'is not empty',
      value: "!=''",
      visible: true
    },
    {
      label: language.value === 'zh' ? '等于' : 'equals',
      value: '=',
      visible: true
    },
    {
      label: language.value === 'zh' ? '不等于' : 'does not equal',
      value: '!=',
      visible: true
    },
    {
      label: language.value === 'zh' ? '大于' : 'is greater than',
      value: '>',
      visible: dataType !== 'string' && dataType !== 'enum'
    },
    {
      label: language.value === 'zh' ? '大于等于' : 'is greater than or equal to',
      value: '>=',
      visible: dataType !== 'string' && dataType !== 'enum'
    },
    {
      label: language.value === 'zh' ? '小于' : 'is less than',
      value: '<',
      visible: dataType !== 'string' && dataType !== 'enum'
    },
    {
      label: language.value === 'zh' ? '小于等于' : 'is less than or equal to',
      value: '<=',
      visible: dataType !== 'string' && dataType !== 'enum'
    },
    {
      label: language.value === 'zh' ? '介于' : 'is between',
      value: 'between',
      visible: dataType !== 'string' && dataType !== 'enum'
    },
    {
      label: language.value === 'zh' ? '处于' : 'is within',
      value: 'within',
      visible: dataType === 'date'
    },
    {
      label: language.value === 'zh' ? '包括' : 'contains',
      value: 'like',
      visible: dataType === 'string'
    },
    {
      label: language.value === 'zh' ? '不包含' : 'does not contain',
      value: 'not like',
      visible: dataType === 'string'
    }
  ];
});

watchEffect(() => {
  if (visible.value && unref(form)) {
    // 展开关闭表头下拉内容是，清楚校验
    unref(form).clearValidate();
  }
});
/**------------方法-------------*/
/**注入方法*/
const { filter, sort, freeze, hide } = inject('chooseDataContext');
/** tooltip isShow */
function textOverflow(el, field) {
  let parentWidth = el.clientWidth;
  let innerWidth = el.childNodes[1].offsetWidth;
  field.tipShow = parentWidth > innerWidth;
}
/** 强制更新*/
function onChange() {
  proxy.$forceUpdate();
}
/** 筛选条件变化时 */
function handleChangeType(val, item) {
  unref(form).clearValidate();
  proxy.$set(item, 'type', val);
  item.value = undefined;
  item.value1 = undefined;
  item.value2 = undefined;
  // 为空，不为空是value值为一组单引号('')
  if (val === "=''" || val === "!=''") {
    item.value = "''";
    item.type = val;
    item.value = "''";
  }
}
/** 格式化数字 */
function numberFormat(val) {
  if (props.field.dataType === 'number') {
    return proxy.$numberFormatDecimal(val);
  } else {
    return val;
  }
}
/** 点击升序 ascending */
function handleAsc() {
  field.isAsc = !props.field.isAsc;
  field.isDes = false;
  visible.value = false;
  sort(true, props.field.fieldName, 'ascending', props.field.isAsc); // 是否表头传参, 排序列名， 升序or降序， 是否排序
}
/** 点击降序 descending */
function handleDes() {
  field.isAsc = false;
  field.isDes = !props.field.isDes;
  visible.value = false;
  sort(true, props.field.fieldName, 'descending', props.field.isDes); // 是否表头传参, 排序列名， 升序or降序， 是否排序
}
/** 点击冻结列 */
function handleFreeze() {
  freeze(true, props.field.fieldName);
  visible.value = false;
}
/** 点击取消冻结列 */
function handleUnfreeze() {
  freeze(false, props.field.fieldName);
  visible.value = false;
}
/** 点击隐藏列 */
function handleHide() {
  hide(props.field);
  visible.value = false;
}
/** 点击筛选 */
function handleFilter() {
  filter('header', props.field.conditions);
  visible.value = false;
}
/** 关闭窗口时，将查询条件置为空，并重新刷新父组件的列表为查询所有 */
function clearClick() {
  field.isFilter = false;
  field.conditions = [
    {
      fieldType: props.field.fieldType,
      prop: props.field.fieldName, // 筛选列
      type: '', // 筛选公式
      value: undefined, // 输入框中的值
      value1: undefined, // 两个值
      value2: undefined // 两个值
    }
  ];
  unref(form).clearValidate();
  handleFilter();
}
/**点击查询时，关闭窗口，并构建查询对象，传递给父组件，父组件接受到数据后重新渲染列表*/
function filterClick() {
  // 已选则列的类型进行筛选
  if (props.field.conditions.length) {
    unref(form).validate((valid) => {
      if (valid) {
        props.field.conditions.forEach((condition) => {
          // 处理两个值
          if (props.field.dataType === 'number' && condition.type === 'between') {
            // number介于
            condition.value = condition.value1 + '#' + condition.value2;
          }
        });
        // 表头按钮icon显示
        field.isFilter = true;
        handleFilter();
      }
    });
  }
}

/** 增加一条筛选条件 */
function addCondition() {
  // eslint-disable-next-line vue/no-mutating-props
  props.field.conditions.push({
    fieldType: props.field.fieldType,
    prop: props.field.fieldName, // 筛选列
    type: '', // 筛选公式
    value: '', // 输入框中的值
    value1: undefined, // 两个值
    value2: undefined // 两个值
  });
}

/** 删除筛选条件 */
function handleDel(index) {
  field.conditions.splice(index, 1);
}

/** 获取value输入形式 */
function getValueCategory(dataType, type) {
  if (!(dataType === 'date' || dataType === 'enum') && !(type === "=''" || type === "!=''")) {
    return 'input';
  } else if (dataType === 'enum' && !(type === "=''" || type === "!=''")) {
    return 'select';
  } else if (dataType === 'date' && !(type === "=''" || type === "!=''")) {
    return 'date';
  }
}

/** 隐藏当前组件时 去除校验*/
function hidePopover() {
  unref(form).clearValidate();
}
/** 判断是否显示tooltip */
function onMouseOver(str) {}
</script>
<style lang="scss" scoped>
.fr {
  float: right;
}
/* Set a 16px margin on the top & bottom */
.my-3 {
  margin-top: 16px !important;
  margin-bottom: 16px !important;
}
.text-success {
  color: #3dcd58;
}
.font-size-080 {
  // 13px
  font-size: 0.8125rem !important;
}
.font-size-075 {
  // 12px
  font-size: 0.75rem !important;
}
/* Set a 8px margin on the bottom */
.mb-2 {
  margin-bottom: 8px !important;
}
.w100p {
  width: 100% !important;
}
.align-center {
  text-align: center;
}
/* Set a 8px margin on the top */
.mt-2 {
  margin-top: 8px !important;
}

.filter-box {
  :deep(.filter-condition) {
    .el-form-item {
      margin-bottom: 8px !important;
    }
  }
}

.el-icon {
  margin-top: 4px;
}

.se-dig-icon-filter {
}

.el-icon-finished {
  margin-top: 6px;
}

svg {
  width: 13px;
  height: 13px;
  vertical-align: -2px;
  overflow: hidden;
  fill: currentColor;
}

.border-red {
  border: 1px solid #ea6c6c;
  border-radius: 4px;
}

.column-list {
  font-size: 14px;
  :deep(.el-button) {
    justify-content: start;
    width: 100%;
    height: 36px;
    padding: 0 10px;
    margin-top: 2px;
    margin-left: 0 !important;
    line-height: 36px;
    text-align: left;
    .selected {
      position: absolute;
      right: 16px;
    }

    &.is-disabled {
      color: #c0ccc2;
    }

    &:hover {
      background-color: #f5f5f5;
    }
  }
}

.filter-footer {
  :deep(.el-divider) {
    margin: 8px 0;
  }

  .buttons {
    text-align: right;

    svg {
      height: 12px;
    }
    :deep(.el-button) {
      display: inline-flex;
      width: 48px;
      height: 32px;
    }
  }

  .filter_btn {
    color: white;
    margin-left: 20px !important;

    svg {
      width: 12px;
    }
  }
}

.no-wrap-header {
  display: flex;
  justify-content: space-between;
  vertical-align: middle;

  .header-content {
    flex-basis: calc(100% - 14px);
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow-x: hidden;
  }
  .more {
    transform: rotate(90deg);
    float: right;
    margin-top: 6px;
    visibility: hidden;
  }

  &:hover {
    .more {
      visibility: visible;
    }
  }
  :deep(.el-icon) {
    margin-top: 4px;
  }
}
</style>
