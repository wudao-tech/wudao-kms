<template>
  <el-form size="small">
    <el-form-item>
      <el-radio v-model="radioValue" :label="1"> 周，允许的通配符[, - * / L #] </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="2"> 不指定 </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="3">
        周期从星期
        <el-input-number v-model="cycle01" :min="1" :max="cycle02" /> -
        <el-input-number v-model="cycle02" :min="1" :max="7" />
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="4">
        第
        <el-input-number v-model="average01" :min="1" :max="4" /> 周的星期
        <el-input-number v-model="average02" :min="1" :max="7" />
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="5">
        本月最后一个星期
        <el-input-number v-model="weekday" :min="1" :max="7" />
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="6">
        指定
        <el-select v-model="checkboxList" clearable placeholder="可多选" multiple style="width: 200px">
          <el-option v-for="(item, index) of weekList" :key="index" :value="item.value" :label="item.label"> </el-option>
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'CrontabWeek',
  props: ['check', 'cron'],
  data() {
    return {
      radioValue: 2,
      weekday: 1,
      cycle01: 1,
      cycle02: 1,
      average01: 1,
      average02: 1,
      checkboxList: [],
      // weekList: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      weekList: [
        { value: '1', label: '周日' },
        { value: '2', label: '周一' },
        { value: '3', label: '周二' },
        { value: '4', label: '周三' },
        { value: '5', label: '周四' },
        { value: '6', label: '周五' },
        { value: '7', label: '周六' }
      ]
      // checkNum: this.$options.propsData.check,
    };
  },
  computed: {
    // 计算两个周期值
    cycleTotal: function () {
      this.cycle01 = this.check(this.cycle01, 1, 7);
      this.cycle02 = this.check(this.cycle02, 1, 7);
      return this.cycle01 + '-' + this.cycle02;
    },
    // 计算平均用到的值
    averageTotal: function () {
      this.average01 = this.check(this.average01, 1, 4);
      this.average02 = this.check(this.average02, 1, 7);
      return this.average01 + '#' + this.average02;
    },
    // 最近的工作日（格式）
    weekdayCheck: function () {
      this.weekday = this.check(this.weekday, 1, 7);
      return this.weekday;
    },
    // 计算勾选的checkbox值合集
    checkboxString: function () {
      const str = this.checkboxList.join();
      return str == '' ? '*' : str;
    }
  },
  watch: {
    radioValue: 'radioChange',
    cycleTotal: 'cycleChange',
    averageTotal: 'averageChange',
    weekdayCheck: 'weekdayChange',
    checkboxString: 'checkboxChange'
  },
  methods: {
    // 单选按钮值变化时
    radioChange() {
      if (this.radioValue === 1) {
        this.$emit('update', 'week', '*');
        this.$emit('update', 'year', '*');
      }

      switch (this.radioValue) {
        case 2:
          this.$emit('update', 'week', '?');
          break;
        case 3:
          this.$emit('update', 'week', this.cycle01 + '-' + this.cycle02);
          break;
        case 4:
          this.$emit('update', 'week', this.average01 + '#' + this.average02);
          break;
        case 5:
          this.$emit('update', 'week', this.weekday + 'L');
          break;
        case 6:
          this.$emit('update', 'week', this.checkboxString);
          break;
      }
    },
    // 根据互斥事件，更改radio的值

    // 周期两个值变化时
    cycleChange() {
      if (this.radioValue == '3') {
        this.$emit('update', 'week', this.cycleTotal);
      }
    },
    // 平均两个值变化时
    averageChange() {
      if (this.radioValue == '4') {
        this.$emit('update', 'week', this.averageTotal);
      }
    },
    // 最近工作日值变化时
    weekdayChange() {
      if (this.radioValue == '5') {
        this.$emit('update', 'week', this.weekday + 'L');
      }
    },
    // checkbox值变化时
    checkboxChange() {
      if (this.radioValue == '6') {
        this.$emit('update', 'week', this.checkboxString);
      }
    }
  }
};
</script>
