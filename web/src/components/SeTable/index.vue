<!--table组件-->
<template>
  <div id="se_table">
    <div :class="filterBarVisible ? 'main-table' : ''">
      <!--   表格上方按钮栏   -->
      <div class="btn-box my-2">
        <!--   表格左上方按钮插槽   -->
        <slot name="buttons"></slot>

        <!--   表格右上方按钮   -->
        <div class="filter-box">
          <!--   表格右上方按钮插槽   -->
          <slot name="buttons-right"></slot>
          <!--  列操作  -->
          <fieldFilter ref="field" v-model="chooseData" :field-list="fieldList" :filter-key="filterKey" @get-list="getList" @do-layout="doLayout" />

          <!--  筛选操作  -->
          <el-tooltip effect="dark" content="筛选" placement="top" :teleported="false">
            <span>
              <el-popover v-model:visible="filterPopVisible" placement="bottom-end" width="640" trigger="click" :teleported="false">
                <template #reference>
                  <el-button v-show="isTableFilter" class="mr-2" link :disabled="filterBarVisible">
                    <template #icon>
                      <i class="iconfont se-dig-icon-a-Filtershaixuan" :style="{ color: isFilter ? '#3dcd58' : 'inherit' }" />
                    </template>
                  </el-button>
                </template>

                <!-- 筛选内容 -->
                <tableFilter
                  ref="popFilter"
                  :field-list="fieldList"
                  :date-options="dateOptions"
                  :filter-bar-visible="filterBarVisible"
                  :filter-pop-visible="filterPopVisible"
                  @filter="filter"
                  @show-bar="showBar"
                />
              </el-popover>
            </span>
          </el-tooltip>

          <!--  排序操作  -->
          <!-- <tableSort v-if="isTableSort" :field-list="fieldList" :popper-ref="chooseData" @sort="sort" /> -->
        </div>
      </div>
      <!--   表格   -->
      <el-table
        v-bind="$attrs"
        ref="refTable"
        :key="tableKey"
        border
        :data="data"
        :height="height"
        :max-height="maxHeight"
        :min-height="50"
        :stripe="stripe"
        :border="border"
        :size="lineHeight === 'fit' ? 'default' : lineHeight"
        :fit="fit"
        :show-header="showHeader"
        :highlight-current-row="highlightCurrentRow"
        :highlight-selection-row="highlightSelectionRow"
        :current-row-key="currentRowKey"
        :row-class-name="rowClassName"
        :row-style="rowStyle"
        :cell-class-name="cellClassName"
        :cell-style="cellStyle"
        :header-row-class-name="headerRowClassName"
        :header-row-style="headerRowStyle"
        :header-cell-class-name="headerCellClassName"
        :header-cell-style="headerCellStyle"
        :row-key="rowKey"
        :empty-text="emptyText"
        :default-expand-all="defaultExpandAll"
        :expand-row-keys="expandRowKeys"
        :default-sort="defaultSort"
        :tooltip-effect="tooltipEffect"
        :indent="indent"
        :lazy="lazy"
        :load="load"
        :tree-props="treeProps"
        @selection-change="handleSelectionChange"
        @expand-change="expandChange"
        @row-click="handleRowClick"
      >
        <template #empty></template>
        <el-table-column v-if="expand" type="expand" width="20" fixed="left">
          <template #default="scope">
            <!-- expand 插槽 -->
            <slot name="expand" :row="scope.row"></slot>
          </template>
        </el-table-column>
        <el-table-column v-if="selection" type="selection" width="50" align="center" fixed="left" />
        <el-table-column label="编号" type="index" :index="indexMethod" width="50" align="center" fixed="left" />
        <el-table-column
          v-for="(col, index) in chooseData"
          :key="index"
          :prop="col.fieldName"
          :class-name="col.cellClass"
          :column-key="col.fieldName"
          :label="col.title"
          :show-overflow-tooltip="col.showOverflowTooltip"
          :align="col.align ? col.align : 'left'"
          :min-width="col.minWidth"
          :width="col.width"
          :fixed="col.fixed"
        >
          <!-- 表头 -->
          <template #header>
            <selectFilterHeader :field="col" :is-hide="chooseData.length !== 1" :date-options="dateOptions" />
          </template>
          <!-- 表格内容 -->
          <template #default="scope">
            <!--   column 内插槽   -->
            <slot :row="scope.row" :field-name="col.fieldName"></slot>
          </template>
        </el-table-column>
      </el-table>

      <!--   分页   -->
      <div class="page-box">
        <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <i v-show="lineHeight === 'large'" class="iconfont se-dig-icon-hanggaoda" />
            <i v-show="lineHeight === 'default'" class="iconfont se-dig-icon-hanggaozhong" />
            <i v-show="lineHeight === 'small'" class="iconfont se-dig-icon-hanggaoxiao" />
            <i v-show="lineHeight === 'fit'" class="iconfont se-dig-icon-hanggao" />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="large">
                <i class="iconfont se-dig-icon-hanggaoda"></i>
                高
              </el-dropdown-item>
              <el-dropdown-item command="default">
                <i class="iconfont se-dig-icon-hanggaozhong"></i>
                中
              </el-dropdown-item>
              <el-dropdown-item command="small">
                <i class="iconfont se-dig-icon-hanggaoxiao"></i>
                小
              </el-dropdown-item>
              <el-dropdown-item command="fit">
                <i class="iconfont se-dig-icon-hanggao"></i>
                自适用
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div class="clearfix" />
      </div>
    </div>

    <!--   表格右侧固定筛选栏   -->
    <div v-show="filterBarVisible" class="right-filter">
      <!--  筛选操作  -->
      <!-- <tableFilter
        ref="barFilter"
        :field-list="fieldList"
        :date-options="dateOptions"
        :filter-bar-visible="filterBarVisible"
        :choose-data="chooseData"
        @filter="filter"
        @show-bar="showBar"
      /> -->
    </div>
  </div>
</template>
<script>
export default {
  name: 'SeTable'
};
</script>
<script setup>
import { ref, computed, nextTick, getCurrentInstance, provide, watch, reactive } from 'vue';
import { useRoute } from 'vue-router';
import draggable from 'vuedraggable';

// import fieldFilter from './fieldFilter.vue';
// import tableSort from './tableSort.vue';
// import tableFilter from './tableFilter.vue';
import selectFilterHeader from './selectFilterHeader.vue';
const { proxy } = getCurrentInstance();
console.log('proxy', proxy);
const route = useRoute();

const emit = defineEmits(['getList', 'selection-change', 'expand-change', 'row-click', 'update:fieldList']);

const props = defineProps({
  // 分页total
  total: {
    require: true
  },
  expand: {
    type: Boolean,
    default: false
  },
  selection: {
    type: Boolean,
    default: true
  },
  expandLoading: {
    type: Boolean
  },
  // 是否显示 筛选操作
  isTableFilter: {
    type: Boolean,
    default: true
  },
  // 是否显示 排序操作
  isTableSort: {
    type: Boolean,
    default: true
  },
  // 列list
  fieldList: {
    type: Array,
    require: true,
    default: () => []
  },
  // 表头显示的列字段
  fieldHeader: {
    type: Array,
    require: true,
    default: () => []
  },
  // 用于查询table的列设置信息，不能为空且整个系统的不同table不能重复
  filterKey: {
    require: true,
    type: String
  },
  // 查询参数(如果不是默认查询条件，需要传入查询参数)
  queryParams: {
    type: Object,
    default: function () {
      return {
        pageNum: 1,
        pageSize: 20,
        conditions: [],
        orderByColumn: '',
        isAsc: '',
        andType: true,
        sorts: [{ orderByColumn: 'updatedTime', isAsc: 'descending' }]
      };
    }
  },
  /*--------------- 以下为自带组件参数 ---------------*/
  // 表格数据
  data: {
    require: true,
    type: Array,
    default: () => []
  },
  height: {},
  maxHeight: {},
  // 斑马纹 false
  stripe: {
    type: Boolean,
    default: false
  },
  // 是否带有纵向边框 false
  border: {
    type: Boolean,
    default: false
  },
  // Table 的尺寸
  // size:{
  //   type: Number||String,
  //   default: '100%'
  // },
  // 默认的排序列的 prop 和顺序。它的prop属性指定默认的排序的列，order指定默认排序的顺序
  defaultSort: {
    type: Object,
    // eslint-disable-next-line vue/require-valid-default-prop
    default: {
      orderByColumn: 'updatedTime',
      isAsc: 'descending'
    }
  },
  // 列的宽度是否自撑开 true
  fit: {},
  // 是否显示表头 true
  showHeader: {},
  // 是否要高亮当前行 false 单选
  highlightCurrentRow: {},
  // 是否要高亮复选框选中行（仅针对开启 selection 有效） false 多选
  highlightSelectionRow: {},
  // 当前行的 key，只写属性
  currentRowKey: {},
  // 行的 className 的回调方法，也可以使用字符串为所有行设置一个固定的 className。
  rowClassName: {},
  // 行的 style 的回调方法，也可以使用一个固定的 Object 为所有行设置一样的 Style。
  rowStyle: {},
  // 单元格的 className 的回调方法，也可以使用字符串为所有单元格设置一个固定的 className。
  cellClassName: {},
  // 单元格的 style 的回调方法，也可以使用一个固定的 Object 为所有单元格设置一样的 Style。
  cellStyle: {},
  // 表头行的 className 的回调方法，也可以使用字符串为所有表头行设置一个固定的 className。
  headerRowClassName: {},
  // 表头行的 style 的回调方法，也可以使用一个固定的 Object 为所有表头行设置一样的 Style。
  headerRowStyle: {},
  // 表头单元格的 className 的回调方法，也可以使用字符串为所有表头单元格设置一个固定的 className
  headerCellClassName: {},
  //表头单元格的 style 的回调方法，也可以使用一个固定的 Object 为所有表头单元格设置一样的 Style。
  headerCellStyle: {},
  // 行数据的 Key，用来优化 Table 的渲染；在使用 reserve-selection 功能与显示树形数据时，该属性是必填的。类型为 String 时，支持多层访问：user.info.id，但不支持 user.info[0].id，此种情况请使用 Function。
  rowKey: {},
  // 空数据时显示的文本内容，也可以通过 slot="empty" 设置
  emptyText: {},
  // 是否默认展开所有行，当 Table 包含展开行存在或者为树形表格时有效
  defaultExpandAll: {},
  // 可以通过该属性设置 Table 目前的展开行，需要设置 row-key 属性才能使用，该属性为展开行的 keys 数组。
  expandRowKeys: {},

  // tooltip effect 属性
  tooltipEffect: {},
  // 展示树形数据时，树节点的缩进
  indent: {},
  // 是否懒加载子节点数据
  lazy: {},
  // 加载子节点数据的函数，lazy 为 true 时生效，函数第二个参数包含了节点的层级信息
  load: {},
  // 渲染嵌套数据的配置选项
  treeProps: {}
});

// 侧栏显示筛选条件
const filterBarVisible = ref(false);
// 筛选pop是否显示
const filterPopVisible = ref(false);
// 表格的key值
const tableKey = ref('');
// 当前方案选择显示的字段List
const chooseData = ref([]);
const dateOptions = ref([
  {
    label: '一周',
    value: 'week'
  },
  {
    label: '一月',
    value: 'month'
  },
  {
    label: '三月内',
    value: 'month3'
  },
  {
    label: '一年内',
    value: 'year'
  }
]);
// 行高
const lineHeight = ref('small');
const isFilter = computed(() => props.fieldList.findIndex((item) => item.isFilter === true) >= 0);
const language = computed(() => 'zh');
const $t = proxy.$t;

//refs
const refTable = ref(null);
const field = ref(null);

// 表头切换中英文
watch(language, (val) => {
  props.fieldList.forEach((item) => {
    let header = props.fieldHeader.find((header) => header.fieldName === item.fieldName);
    // item.title = $t(`pross.${item.fieldName}`);
    item.title = item.fieldName;
  });
});
/** 表格行高设置 */
function handleCommand(command) {
  lineHeight.value = command;

  props.fieldList.forEach((item) => {
    item.showOverflowTooltip = !(command === 'fit');
  });
  doLayout();
}

/** 重新渲染表格 */
function doLayout() {
  nextTick(() => {
    if (refTable.value) {
      refTable.value.doLayout();
    }
  });
}

/** 自定义序号 */
function indexMethod(index) {
  const i = index + 1;
  const { pageNum, pageSize } = props.queryParams;
  return (pageNum - 1) * pageSize + i;
}

/** 筛选 */
function filter(filterFrom, conditions, andType) {
  filterPopVisible.value = false;
  if (filterFrom === 'header') {
    // 表头传参来源：header：表头，icon：有上角按钮
    if (conditions.length === 1 && conditions[0].value === undefined) {
      // reset 清空筛选
      queryParams.conditions = queryParams.conditions.filter((item) => item.prop !== conditions[0].prop);
    } else {
      // filter 带有查询条件筛选
      const queryCondition = queryParams.conditions.find((item) => item.prop === conditions[0].prop);
      if (queryCondition) {
        // 已存在列筛选，更新筛选公式和值(选取所有不包含所选列的筛选条件，再加上传参的所选列的所有筛选条件)
        let otherConditions = queryParams.conditions.filter((item) => item.prop !== conditions[0].prop);
        queryParams.conditions = otherConditions.concat(conditions);
      } else {
        // 不存在列筛选，追加筛选条件
        queryParams.conditions = queryParams.conditions.concat(conditions);
      }
    }
  } else if (filterFrom === 'icon') {
    // 右上角按钮传参
    queryParams.conditions = conditions.map((item) => item.condition);
  }
  // and or 赋值
  if (andType !== undefined) {
    // 如果有传值，则使用传值, 如果没有传值，则使用默认值true，即and
    queryParams.andType = andType;
  }
  queryParams.pageNum = 1;
  getList();
}

/** 筛选侧栏显示设置 */
function showBar(val) {
  filterPopVisible.value = false;
  filterBarVisible.value = val;
}

/** 排序 */
function sort(isHeader, property, type, boo) {
  // 是否表头传参, 排序列名， 升序or降序， 是否排序
  if (isHeader) {
    // 表头传参
    // 删除默认排序
    const defaultIndex = props.queryParams.sorts.findIndex((item) => item.orderByColumn === props.defaultSort.orderByColumn);
    const sort = props.queryParams.sorts.find((item) => item.orderByColumn === property);
    const index = props.queryParams.sorts.findIndex((item) => item.orderByColumn === property);
    if (defaultIndex >= 0) {
      queryParams.sorts.splice(index, 1);
    }
    if (sort) {
      // 【排序列名】如果在排序列表里
      if (boo) {
        // 如果对该列排序
        // 更新列表里的信息
        sort.isAsc = type;
      } else {
        // 如果不对该列排序
        // 删除列表里的对应项
        queryParams.sorts.splice(index, 1);
      }
    } else {
      // 【排序列名】如果不在排序列表里
      // 向列表里推送新的排序规则
      queryParams.sorts.push({
        orderByColumn: property,
        isAsc: type
      });
    }
  } else {
    // 右上角传参
    queryParams.sorts = property;
  }
  // 如果排序列表为空，使用默认排序
  if (props.queryParams.sorts.length === 0) {
    queryParams.sorts.push({
      orderByColumn: props.defaultSort.orderByColumn,
      isAsc: props.defaultSort.isAsc
    });
  }
  getList();
}

/** 冻结列 */
function freeze(type, property) {
  if (field.value) {
    field.value.fixedField = [];
  }
  const index = chooseData.value.findIndex((item) => item.fieldName === property);
  if (type) {
    // 冻结列
    chooseData.value.forEach((item, i) => {
      if (i <= index) {
        item.fixed = true;
      } else item.fixed = false;
    });
  } else {
    // 取消冻结列
    chooseData.value.forEach((item, i) => {
      if (i >= index) {
        item.fixed = false;
      } else {
        if (field.value) {
          field.value.fixedField.push(item.fieldName);
        }
      }
    });
  }
}

/** 隐藏列 */
function hide(field) {
  let index = chooseData.value.findIndex((item) => item.fieldName === field.fieldName);
  chooseData.value.splice(index, 1);
}

/** 获取数据 */
function getList() {
  // 处理事件类型为【日期】的【介于】筛选值
  let conditions = props.queryParams.conditions.map((item) => {
    let itemValue = item.value;
    if (item.fieldType === 'java.util.Date' && item.type === 'between') {
      // date介于 时间类型value赋值
      itemValue = item.value[0] + '#' + item.value[1];
    }
    return { ...item, value: itemValue };
  });
  emit('getList', { ...props.queryParams, conditions: conditions });
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  emit('selection-change', selection);
}

/** 点击表头事件 */
function expandChange(row, expandedRows) {
  emit('expand-change', row, expandedRows);
}

/** 单击行事件 */
function handleRowClick(row) {
  // 选中行内文字不触发行点击事件
  if (window.getSelection().toString() === '') {
    emit('row-click', row);
  }
}

/** 格式化数据类型 */
function mapFieldType(type) {
  const typeMap = new Map([
    // 日期
    ['java.util.Date', 'date'],
    // 数字
    ['java.math.BigDecimal', 'number'],
    ['java.lang.Byte', 'number'],
    ['java.lang.Double', 'number'],
    ['java.lang.Float', 'number'],
    ['java.lang.Integer', 'number'],
    ['java.lang.Long', 'number'],
    ['java.lang.Short', 'number'],
    // 字符
    ['java.lang.String', 'string'],
    ['java.lang.Character', 'string'],
    // 布尔
    ['java.lang.Boolean', 'boolean']
  ]);
  return typeMap.get(type);
}

// 格式化fieldList
function modifyFieldList() {
  props.fieldList.forEach((item) => {
    if (!item.conditions) {
      item.conditions = [
        {
          fieldType: item.fieldType,
          prop: item.fieldName, // 筛选列
          type: '', // 筛选公式
          value: undefined, // 输入框中的值
          value1: undefined, // 两个值
          value2: undefined // 两个值
        }
      ];
    }
  });
  // 前端决定表格显示那些列（fieldHeader），并且设置中英文表头
  let arr = [];
  props.fieldHeader.forEach((item) => {
    let list = props.fieldList.find((list) => list.fieldName === item.fieldName);
    console.log('list', list);
    if (list) {
      // list.title = $t(`pross.${item.fieldName}`);
      list.title = item.fieldName;
      console.log('list.title', list.title);
      list.dataType = item.dataType;
      list.valueOptions = item.valueOptions;
      list.cellClass = item.cellClass; // detail-cell(表格中可点击查看详情单元格样式), link-cell(表格中可跳转单元格样式)
      arr.push(list);
    }
  });
  console.log('arr', arr);
  // 格式化列的数据类型
  arr.forEach((item) => {
    if (!item.dataType) {
      // 如果fieldHeader 没有定义dataType类型
      if (item.valueOptions) {
        // 后端返回枚举值
        item.dataType = 'enum';
        item.valueOptions = item.valueOptions; // 筛选公式选项 = 字典表值
        // proxy.$set(item, 'valueOptions', item.valueOptions); // 筛选公式选项 = 字典表值
      } else if (item.category) {
        // 字典类型枚举类型
        const options = [];
        proxy.getDicts(item.category).then((res) => {
          res.data.forEach((item) => {
            options.push({
              label: item.label,
              labelEn: item.dictLabelEn,
              value: item.value
            });
          });
        });
        item.dataType = 'enum';
        item.valueOptions = options; // 筛选公式选项 = 字典表值
        // proxy.$set(item, 'valueOptions', options); // 筛选公式选项 = 字典表值
      } else {
        // 其他类型(日期, 数字， 字符， 布尔)
        item.dataType = mapFieldType(item.fieldType);
        // proxy.$set(item, 'dataType', mapFieldType(item.fieldType));
      }
    }
    item.isAsc = false;
    item.isDes = false;
    // proxy.$set(item, 'isAsc', false); // 是否升序
    // proxy.$set(item, 'isDes', false); // 是否降序
    if (!item.isFilter) {
      item.isFilter = false;
      // proxy.$set(item, 'isFilter', false); // 是否筛选
    }
    item.fixed = false;
    item.isAllShow = true;
    item.isSelectShow = false;
    item.tipShow = true;
    // proxy.$set(item, 'fixed', false); // 是否冻结
    // proxy.$set(item, 'isAllShow', true); // 左侧筛选是否显示
    // proxy.$set(item, 'isSelectShow', false); // 右侧筛选是否显示
    // proxy.$set(item, 'tipShow', true); // 表头tooltip 是否disabled
    if (item.width) {
      item.minWidth = item.width;
      // proxy.$set(item, 'minWidth', item.width);
    } else {
      item.minWidth = 124;
      // proxy.$set(item, 'minWidth', 124);
    }
  });
  console.log('arrs', arr);
  emit('update:fieldList', arr);
}

// 格式化fieldList
modifyFieldList();

provide('chooseDataContext', { chooseData, filter, sort, freeze, hide });
defineExpose({ doLayout, filter });
</script>

<style scoped lang="scss">
/* Set a 8px margin on the top & bottom */
.my-2 {
  margin-top: 8px !important;
  margin-bottom: 8px !important;
}
/* Set a 8px margin on the right */
.mr-2 {
  margin-right: 8px !important;
}
.clearfix {
  &:after {
    visibility: hidden;
    display: block;
    font-size: 0;
    content: ' ';
    clear: both;
    height: 0;
  }
}
.el-dropdown-link {
  cursor: pointer;
  color: #3dcd58;
  margin-left: 5px;
}
:deep(.cell) {
  &:hover {
    .el-icon-more {
      visibility: visible !important;
    }
  }
}

#se_table {
  height: 100%;

  :deep(.el-table) {
    // 表格弹框单元格样式
    td.detail-cell {
      color: #1568ff;

      .el-button--text {
        color: #1568ff;
        text-align: left;
      }

      &:hover {
        background-color: #e7f7ea !important;
        cursor: pointer !important;
      }
    }

    // 表格跳转单元格样式
    td.link-cell {
      color: #1568ff;
      text-decoration: underline;

      .el-button--text {
        color: #1568ff;
        text-align: left;
        text-decoration: underline;
      }

      &:hover {
        background-color: #e7f7ea !important;
        cursor: pointer !important;

        &:after {
          content: '\ec94';
          position: absolute;
          top: calc(50% - 6px);
          right: 10px;
          font-family: 'iconfont';
          font-size: 12px;
          color: #979797;
        }
      }
    }
    .el-switch,
    .el-radio {
      height: 20px !important;
    }

    .el-table__header {
      .cell {
        padding: 0 8px;
      }
    }
  }

  .main-table {
    float: left;
    width: calc(100% - 278px);
    padding-right: 10px;
  }

  .right-filter {
    float: left;
    width: 278px;
    height: 100%;
    border-left: solid 1px #eeeeee;
    overflow-x: hidden;
    overflow-y: auto;
  }

  .btn-box {
    display: flex;
    justify-content: space-between;

    :deep(.el-button--text) {
      color: inherit;

      &:hover {
        color: #3dcd58;
      }

      &:focus {
        color: #37b94f;
      }

      &.is-disabled {
        color: #c0ccc2;

        &:hover {
          color: #c0ccc2;
        }

        &:focus {
          color: #c0ccc2;
        }
      }
    }

    .filter-box {
      margin-left: auto;
    }
  }
}

.page-box {
  display: flex;
  justify-content: right;

  :deep(.el-dropdown) {
    margin-left: 16px;
    margin-top: 22px;
  }
}
</style>
