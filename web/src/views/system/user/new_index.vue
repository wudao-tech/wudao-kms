<template>
  <div class="p-2">
    <se-table
      v-if="fieldList.length"
      v-model:fieldList="fieldList"
      :field-header="fieldHeader"
      :data="tableData"
      :total="total"
      :max-height="tableHeight"
      filter-key="machine_exchange"
      @get-list="getList"
      @selection-change="handleSelectionChange"
      @row-click="handleUpdate"
    >
    </se-table>
  </div>
</template>

<script setup name="User">
import setable from '@/components/SeTable/index.vue';

// 表头全部字段
const fieldList = ref([{}]);
// 表头显示字段
const fieldHeader = ref([]);
// 表格数据
const tableData = ref([]);
// 选中数组
const ids = ref([]);
// 非单个禁用
const single = ref(false);
// 非多个禁用
const multiple = ref(false);

const total = ref(0);
const tableHeight = window.innerHeight - 250;
// 请求数据
const getList = () => {};

const handleSelectionChange = (selection) => {
  ids.value = selection.map((item) => item.busMachineMaterialFamilyExchangeId);
  single.value = !!(selection.length === 1);
  multiple.value = !!selection.length;
};

const handleUpdate = (row) => {};
</script>

<style lang="scss" scoped></style>
