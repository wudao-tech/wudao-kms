<!-- 排序集合 -->
<template>
  <div id="table_sort">
    <el-tooltip effect="dark" content="排序" placement="top">
      <span>
        <el-popover v-model:visible="sortVisible" placement="bottom-end" width="400" trigger="click">
          <!-- 触发按钮 -->
          <template #reference>
            <el-button link>
              <template #icon>
                <i class="iconfont se-dig-icon-a-Sort-onepaixu1" :style="{ color: isSort ? '#3dcd58' : 'inherit', fontSize: '16px' }" />
              </template>
            </el-button>
          </template>

          <!-- 排序内容 -->
          <div class="table-sort">
            <el-button class="mb-2" icon="el-icon-plus" link type="primary" @click="addCondition"> 增加排序 </el-button>
            <!--   逐条生成排序条件   -->
            <div class="conditions">
              <draggable
                v-model="sortList"
                chosen-class="chosen"
                ghost-class="ghost"
                item-key="id"
                :force-fallback="true"
                handle=".mover"
                animation="1000"
                @start="onStart"
                @end="onEnd"
              >
                <template #item="{ element, index }">
                  <div class="mover">
                    <el-row :gutter="8" class="mb-3">
                      <el-col :span="2">
                        <el-button class="mt-1" type="primary" link :icon="Sort" circle />
                      </el-col>
                      <el-col :span="14">
                        <el-select v-model="element.orderByColumn" placeholder="选择" :teleported="false">
                          <el-option
                            v-for="option in fieldList"
                            v-show="!option.noSearch"
                            :key="option.fieldName"
                            :label="option.title"
                            :value="option.fieldName"
                          ></el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="8">
                        <!-- 升序 -->
                        <el-radio v-model="element.isAsc" label="ascending" size="default"
                          ><i class="el-icon-top" />
                          升序
                        </el-radio>
                        <!-- 降序 -->
                        <el-radio v-model="element.isAsc" label="descending" size="default"
                          ><i class="el-icon-bottom" />
                          降序
                        </el-radio>
                        <el-button icon="el-icon-delete" link @click="handleDel(element, index)" />
                      </el-col>
                    </el-row>
                  </div>
                </template>
              </draggable>
            </div>
            <div class="footer">
              <el-button @click="reset"> 清除</el-button>
              <el-button type="primary" @click="handleSort"> 确认</el-button>
            </div>
          </div>
        </el-popover>
      </span>
    </el-tooltip>
  </div>
</template>
<script setup>
import { ref, computed, nextTick, h, getCurrentInstance, provide, reactive, watch, inject } from 'vue';
import draggable from 'vuedraggable';
import { Sort } from '@element-plus/icons-vue';
const { sort } = inject('chooseDataContext');
const { proxy } = getCurrentInstance();
const props = defineProps(['fieldList']);

// 排序条件list
let sortList = ref([]);
// 排序条件框
let sortVisible = ref(false);

// 是否排序
let isSort = computed(() => {
  return props.fieldList.findIndex((item) => item.isAsc || item.isDes) >= 0;
});

watch(sortVisible, (val) => {
  if (val) {
    sortList.value = [];
    props.fieldList.forEach((item) => {
      if (item.isAsc) {
        // 升序或降序
        sortList.value.push({
          isAsc: 'ascending',
          orderByColumn: item.fieldName,
          id: Math.random()
        });
      } else if (item.isDes) {
        // 升序或降序
        sortList.value.push({
          isAsc: 'descending',
          orderByColumn: item.fieldName,
          id: Math.random()
        });
      }
    });
  }
});

/**------------- 方法 --------------*/
/** 增加一条排序条件 */
function addCondition() {
  sortList.value.push({ isAsc: 'ascending', orderByColumn: '', id: Math.random() });
}
/** 删除排序条件 */
function handleDel(item, index) {
  sortList.value.splice(index, 1);
  if (item.orderByColumn) {
    handleSort();
  }
}
/** 清空排序条件 */
function reset() {
  props.fieldList.forEach((item) => {
    item.isAsc = false; // 升序
    item.isDes = false; // 降序
  });
  sortList.value = [];
  sort(false, []);
  // sortVisible.value = false;
  document.body.click();
}
/** 排序 */
function handleSort() {
  // 有排序条件
  if (sortList.value.length) {
    props.fieldList.forEach((item) => {
      const sortItem = sortList.value.find((list) => list.orderByColumn === item.fieldName);
      if (sortItem) {
        if (sortItem.isAsc === 'ascending') {
          item.isAsc = true; // 升序
          item.isDes = false; // 降序
        } else {
          item.isAsc = false; // 升序
          item.isDes = true; // 降序
        }
      } else {
        item.isAsc = false; // 升序
        item.isDes = false; // 降序
      }
    });
    // sortVisible.value = false
    sort(false, sortList.value);
    document.body.click();
  } else {
    reset();
  }
}
/** 开始拖拽事件 */
function onStart() {
  // this.drag = true;
}
/** 拖拽结束事件 */
function onEnd() {
  // this.drag = false;
}
</script>
<style lang="scss" scoped>
/* Set a 8px margin on the bottom */
.mb-2 {
  margin-bottom: 8px !important;
}
/* Set a 16px margin on the bottom */
.mb-3 {
  margin-bottom: 16px !important;
}
/* Set a 4px margin on the top */
.mt-1 {
  margin-top: 4px !important;
}

.se-dig-icon-a-Sort-onepaixu1 {
  margin-top: -2px;
}

#table_sort {
  display: inline-flex;
  justify-content: flex-end;
  font-size: 14px;
}

.table-sort {
  .conditions {
    max-height: 500px;
    margin: 8px 0;

    :deep(.el-radio) {
      margin-right: 10px;
      .el-radio__input {
        display: none;
      }
      .el-radio__label {
        padding-left: 2px;
        font-size: 14px;
      }
    }
  }

  .footer {
    text-align: right;
  }
}
</style>
