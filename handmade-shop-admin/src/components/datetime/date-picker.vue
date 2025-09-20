<script setup lang="ts">
import { computed } from 'vue'
import dayjs from 'dayjs'

const props = withDefaults(defineProps<{ minDate?: string; maxDate?: string }>(), {
  minDate: '',
  maxDate: ''
})
const emit = defineEmits<{
  'update:minDate': [value: string]
  'update:maxDate': [value: string]
}>()

const timeRange = computed({
  get: () => {
    console.log([
      props.minDate ? dayjs(props.minDate).toDate() : new Date(),
      props.maxDate ? dayjs(props.maxDate).toDate() : new Date()
    ])
    return [
      props.minDate ? dayjs(props.minDate).toDate() : new Date(),
      props.maxDate ? dayjs(props.maxDate).toDate() : new Date()
    ] as [Date, Date]
  },
  set: (value) => {
    if (value[0] && value[1]) {
      emit('update:minDate', dayjs(value[0]).format('YYYY-MM-DD'))
      emit('update:maxDate', dayjs(value[1]).format('YYYY-MM-DD'))
    }
  }
})
</script>

<template>
  <el-date-picker
    v-model="timeRange"
    type="daterange"
    range-separator="至"
    start-placeholder="开始日期"
    end-placeholder="结束日期"
  />
</template>

<style scoped lang="scss"></style>
