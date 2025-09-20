<script setup lang="ts">
import { computed } from 'vue'
import dayjs from 'dayjs'

const props = withDefaults(defineProps<{ minTime?: string; maxTime?: string }>(), {
  minTime: '',
  maxTime: ''
})
const emit = defineEmits<{
  'update:minTime': [value: string]
  'update:maxTime': [value: string]
}>()
const timeRange = computed({
  get: () => {
    dayjs()
    return [
      props.minTime ? dayjs(props.minTime, 'HH:mm:ss').toDate() : new Date(),
      props.maxTime ? dayjs(props.maxTime, 'HH:mm:ss').toDate() : new Date()
    ] as [Date, Date]
  },
  set: (value) => {
    if (value[0] && value[1]) {
      console.log(dayjs(value[0]).format('HH:mm:ss'))
      emit('update:minTime', dayjs(value[0]).format('HH:mm:ss'))
      emit('update:maxTime', dayjs(value[1]).format('HH:mm:ss'))
    }
  }
})
</script>

<template>
  <el-time-picker
    v-model="timeRange"
    is-range
    range-separator="至"
    start-placeholder="开始日期"
    end-placeholder="结束日期"
  />
</template>

<style scoped lang="scss"></style>
