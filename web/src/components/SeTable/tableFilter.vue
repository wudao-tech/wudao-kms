<!--筛选-->
<template>
  <div class="table-filter">
    <!-- 固定按钮 -->
    <div v-if="!filterBarVisible" class="location" @click="locationRight">
      <el-icon><Location color="#3dcd58" /></el-icon>
    </div>
    <!-- 关闭右侧栏按钮 -->
    <div v-else class="close-bar">
      <i class="iconfont se-dig-icon-a-Filtershaixuan" /> 筛选条件
      <el-button icon="el-icon-close" link class="fr" @click="closeBar" />
    </div>
    <!-- and：全部 or：任一 -->
    <div class="mb-2">
      选择方式
      <el-select v-model="andType" placeholder=" " class="and-or" :teleported="false" @change="onChange">
        <el-option label="all" :value="true"></el-option>
        <el-option label="any" :value="false"></el-option>
      </el-select>
      筛选项
    </div>
    <!--  增加筛选条件  -->
    <el-button icon="el-icon-plus" link type="primary" @click="addCondition"> 增加筛选 </el-button>
    <!--   逐条生成筛选条件   -->
    <div class="conditions">
      <el-form ref="form" :model="data">
        <el-row v-for="(item, index) in data.filterList" :key="index" :gutter="8">
          <!--  筛选字段  -->
          <el-col :span="filterBarVisible ? 14 : 8" class="mb-2">
            <el-form-item :prop="`filterList.${index}.condition.prop`" :rules="data.commonRules.selectRequired">
              <el-select v-model="item.condition.prop" placeholder="搜索" :teleported="false" @change="(val) => handleChangeField(val, item)">
                <el-option
                  v-for="option in fieldList"
                  v-show="!option.noSearch"
                  :key="option.fieldName"
                  :label="option.title"
                  :value="option.fieldName"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <!--  筛选公式  -->
          <el-col :span="filterBarVisible ? 10 : 7" class="mb-2">
            <el-form-item :prop="`filterList.${index}.condition.type`" :rules="data.commonRules.selectRequired">
              <el-select v-model="item.condition.type" placeholder="搜索" :teleported="false" @change="(val) => handleChangeType(val, item)">
                <el-option
                  v-for="(typeOption, i) in item.typeOptions"
                  v-show="typeOption?.visible"
                  :key="i"
                  :label="typeOption.label"
                  :value="typeOption.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <!--  筛选值  -->
          <el-col :span="filterBarVisible ? 22 : 8" class="mb-2">
            <!--  输入框筛选  -->
            <template v-if="getValueCategory(item.dataType, item.condition.type) === 'input'">
              <!--  介于 有两个输入框  -->
              <template v-if="item.condition.type === 'between'">
                <el-row>
                  <el-col :span="11">
                    <el-form-item :prop="`filterList.${index}.condition.value1`" :rules="data.commonRules.inputRequired">
                      <el-input
                        ref="value1"
                        v-model="item.condition.value1"
                        clearable
                        @input="onChange"
                        @keyup="item.condition.value1 = numberFormat(item.dataType, item.condition.value1)"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="2" class="align-center mt-2">-</el-col>
                  <el-col :span="11">
                    <template>
                      <el-form-item :prop="`filterList.${index}.condition.value2`" :rules="data.commonRules.inputRequired">
                        <el-input
                          ref="value2"
                          v-model="item.condition.value2"
                          clearable
                          @input="onChange"
                          @keyup="item.condition.value2 = numberFormat(item.dataType, item.condition.value2)"
                        />
                      </el-form-item>
                    </template>
                  </el-col>
                </el-row>
              </template>
              <!--  1个输入框情况  -->
              <template v-else>
                <el-form-item :prop="`filterList.${index}.condition.value`" :rules="data.commonRules.inputRequired">
                  <el-input
                    key="value"
                    v-model="item.condition.value"
                    clearable
                    @input="onChange"
                    @keyup="item.condition.value = numberFormat(item.dataType, item.condition.value)"
                  />
                </el-form-item>
              </template>
            </template>

            <!--  下拉框筛选  -->
            <template v-else-if="getValueCategory(item.dataType, item.condition.type) === 'select'">
              <el-form-item :prop="`filterList.${index}.condition.value`" :rules="data.commonRules.selectRequired">
                <el-select v-model="item.condition.value" placeholder="搜索" :teleported="false">
                  <el-option v-for="(valueOption, i) in item.valueOptions" :key="i" :label="valueOption.label" :value="valueOption.value" />
                </el-select>
              </el-form-item>
            </template>

            <!--  时间筛选  -->
            <template v-else-if="getValueCategory(item.dataType, item.condition.type) === 'date'">
              <!--  介于 有两个选择框  -->
              <el-form-item :prop="`filterList.${index}.condition.value`" :rules="data.commonRules.selectRequired">
                <el-date-picker
                  v-if="item.condition.type === 'between'"
                  :key="item.condition.type + 'range'"
                  v-model="item.condition.value"
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
                <el-select v-else-if="item.condition.type === 'within'" v-model="item.condition.value" :teleported="false" @change="onChange">
                  <el-option v-for="(dateOption, i) in dateOptions" :key="i" :label="dateOption.label" :value="dateOption.value" />
                </el-select>
                <!--  一个日期  -->
                <el-date-picker
                  v-else
                  :key="item.condition.type"
                  v-model="item.condition.value"
                  class="w100p mb-2"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择"
                  :teleported="false"
                >
                </el-date-picker>
              </el-form-item>
            </template>
          </el-col>
          <!--  删除按钮  -->
          <el-col :span="1" class="mb-2">
            <el-button icon="el-icon-delete" link @click="handleDel(item, index)" />
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="footer">
      <el-button @click="reset">清除</el-button>
      <el-button type="primary" @click="handleFilter">确认</el-button>
    </div>
  </div>
</template>
<script setup>
import { computed, getCurrentInstance, inject, reactive, ref, unref, watch, watchEffect } from 'vue';
import { Location } from '@element-plus/icons-vue';
const { filter } = inject('chooseDataContext');
const { proxy } = getCurrentInstance();
const props = defineProps({
  fieldList: Object,
  dateOptions: Array, // 日期类型 处于选项
  filterBarVisible: Boolean,
  filterPopVisible: Boolean
});
const emit = defineEmits(['update:filterPopVisible']);
// const language = computed(() => proxy.$store.getters.language.toString());
const language = computed(() => 'zh');
/** ------------- 数据 ------------- */
// dom ref
let form = ref(null);
// true所有 false任一
let andType = ref(true); // true:and, false:or
// bar visible
let barVisible = ref(false);
// pop visible
let popVisible = ref(false);

// 数据
let data = reactive({
  // 筛选条件list 动态校验只能是针对对象
  filterList: [],
  // 表头上已被筛选的列 完整信息
  filterColumnList: [],
  // 追加的筛选条件
  additionalData: [],
  // 校验规则
  commonRules: {
    inputRequired: [{ required: true, message: ' ', trigger: 'blur' }],
    selectRequired: [{ required: true, message: ' ', trigger: 'change' }]
  }
});

let isFilter = computed(() => {
  return props.fieldList.findIndex((item) => item.isFilter) >= 0;
});

/** ------------- 监视数据 ------------- */

watch(barVisible, (val) => {
  if (val) {
    getFieldList();
  }
});
watch(popVisible, (val) => {
  if (val) {
    getFieldList();
  }
});

watchEffect(() => {
  popVisible.value = props.filterPopVisible;
  barVisible.value = props.filterBarVisible;
});

/** 获取数据 */
function getFieldList() {
  let fieldList = props.fieldList.filter((item) => item.isFilter);
  let Result = [];
  // let condition = {}
  // 根据类型加载计算公式(表头搜索后确保搜索框里下拉有内容) 把表头里的conditions 分开为一个个独立的condition，以便更改筛选列
  fieldList.forEach((item) => {
    item.typeOptions = getTypeOptions(item.dataType);
    item.conditions.forEach((c) => {
      let field = proxy.$deepClone(item);
      let condition = proxy.$deepClone(c);
      field.condition = condition;
      Result.push(field);
    });
  });
  Result.forEach((item) => delete item.conditions);
  data.filterList = proxy.$deepClone(Result);
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

/** 增加一条筛选条件 */
function addCondition() {
  data.filterList.push({
    fieldName: '',
    title: '',
    dataType: '',
    isFilter: true,
    condition: {
      fieldType: '',
      prop: '',
      type: undefined,
      value: undefined
    },
    typeOptions: [],
    valueOptions: []
  });
}

/** 筛选列变化时 */
function handleChangeField(val, item) {
  const field = props.fieldList.filter((f) => f.fieldName === val)[0];
  // 数据类型
  item.dataType = field.dataType;
  // 筛选列变化时计算公式下拉列表变化
  item.typeOptions = getTypeOptions(field.dataType);
  item.valueOptions = getValueOptions(field.fieldName);
  // 默认item.condition.type值
  item.condition.fieldType = field.fieldType;
  item.condition.type = undefined;
  item.condition.value = undefined;
  item.condition.value1 = undefined;
  item.condition.value2 = undefined;
}
/** 筛选条件变化时 */
function handleChangeType(val, item) {
  item.condition.value = undefined;
  proxy.$set(item.condition, 'value1', undefined);
  proxy.$set(item.condition, 'value2', undefined);
  if (val === "=''" || val === "!=''") {
    // 为空，不为空
    item.condition.value = "''";
  } else {
    item.condition.value = undefined;
  }
}
/** 格式化数字 */
function numberFormat(dataType, value) {
  if (dataType === 'number') {
    return proxy.$numberFormatDecimal(value);
  } else {
    return value;
  }
}
/** 获取计算公式下拉列表 */
function getTypeOptions(dataType) {
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
}
/** 获取筛选值下拉列表 */
function getValueOptions(fieldName) {
  return props.fieldList.find((item) => item.fieldName === fieldName)?.valueOptions;
}
/** and or select 强制更新*/
function onChange() {
  proxy.$forceUpdate();
}
/** 删除筛选条件 */
function handleDel(item, index) {
  data.filterList.splice(index, 1);
}
/** 筛选 */
function handleFilter() {
  // 有筛选条件
  if (data.filterList.length) {
    unref(form).validate((valid) => {
      if (valid) {
        data.filterList.map((item) => (item.fieldName = item.condition.prop));
        let fieldList = [];
        data.filterList.forEach((field) => {
          // 处理两个值
          if (field.condition.value1 !== undefined && field.condition.value2 !== undefined) {
            // 筛选条件value1,value2有值(两个值)取值范围都有值
            if (field.dataType === 'number' && field.condition.type === 'between') {
              // number介于
              field.condition.value = field.condition.value1 + '#' + field.condition.value2; // 两个值用#连接 赋值给value
            }
          }
          // 将筛选中相同列的筛选条件放到一起
          let filterField = fieldList.find((item) => item.fieldName === field.fieldName);
          if (filterField) {
            // 如果列已筛选，在列的筛选信息中（conditions）追加筛选条件
            filterField.conditions.push(field.condition);
          } else {
            // 如果列未筛选，增加列的筛选信息
            field.conditions = [field.condition];
            fieldList.push(field);
          }
        });
        // 以下赋值是为了同步表头状态
        props.fieldList.forEach((field) => {
          // 清空表头筛选条件，根据筛选popover内容重新赋值
          field.isFilter = false;
          field.conditions = [
            {
              fieldType: field.fieldType, // 数据类型
              prop: field.fieldName, // 筛选列
              type: '', // 筛选公式
              value: '', // 输入框中的值
              value1: undefined, // 两个值
              value2: undefined // 两个值
            }
          ];
          fieldList.forEach((item) => {
            if (field.fieldName === item.fieldName) {
              field.isFilter = true;
              field.conditions = item.conditions;
            }
          });
        });
        unref(form).clearValidate();
        filter('icon', data.filterList, andType.value);
      }
    });
  } else {
    reset();
  }
}
/** 重置筛选 */
function reset() {
  props.fieldList.forEach((field) => {
    field.conditions = [
      {
        fieldType: field.fieldType, // 数据类型
        prop: field.fieldName, // 筛选列
        type: '', // 筛选公式
        value: '', // 输入框中的值
        value1: undefined, // 两个值
        value2: undefined // 两个值
      }
    ];
    field.isFilter = false;
  });
  andType.value = true;
  data.filterList = [];
  filter('icon', data.filterList, andType.value);
}
/** 固定右侧 */
function locationRight() {
  proxy.$emit('showBar', true);
}
/** 关闭右侧 */
function closeBar() {
  proxy.$emit('showBar', false);
}
</script>

<style lang="scss" scoped>
.fr {
  float: right;
}
/* Set a 8px margin on the bottom */
.mb-2 {
  margin-bottom: 8px !important;
}
/* Set a 8px margin on the top */
.mt-2 {
  margin-top: 8px !important;
}
.w100p {
  width: 100% !important;
}
.align-center {
  text-align: center;
}

.table-filter {
  font-size: 14px;
  padding: 16px;
}

.location {
  position: absolute;
  top: 0;
  right: 0;
  padding: 10px 10px 16px 16px;
  border-bottom-left-radius: 40px;
  background: #f2fff4;
  cursor: pointer;

  .el-icon-location-information {
    font-size: 16px;
    color: #3dcd58;
  }
}

.close-bar {
  line-height: 36px;
  margin-top: -16px;
}

.conditions {
  max-height: 430px;
  //overflow-x: hidden;
  //overflow-y: visible;
  margin: 8px 0;
}

.footer {
  text-align: right;
}

.and-or {
  width: 60px;

  :deep(.el-select__wrapper) {
    width: 60px;
    padding: 0;
    box-shadow: none;
    border-bottom: solid 1px #3dcd58;
    text-align: center;
    font-size: 14px;
  }
}

:deep(.and-or) {
  .el-input__suffix {
    // right: -6px;
    display: none;
  }
}
</style>
