<script setup lang="ts">
import type { HandicraftOrderDto } from '@/apis/__generated/model/dto'
import { computed } from 'vue'
import type { Scope } from '@/typings'

type HandicraftBooking =
  HandicraftOrderDto['HandicraftOrderRepository/COMPLEX_FETCHER_FOR_ADMIN']['items'][0]['handicraftBookingView']
type HandicraftBookingScope = Scope<HandicraftBooking>

const props = defineProps<{
  order: HandicraftOrderDto['HandicraftOrderRepository/COMPLEX_FETCHER_FOR_ADMIN']
}>()

const bookings = computed(() => {
  return props.order.items.map((item) => {
    return item.handicraftBookingView
  })
})
</script>

<template>
  <div>
    <el-table ref="table" :data="bookings" :border="true">
      <el-table-column label="开始时间" prop="startTime" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-time-picker
            style="width: 120px"
            size="small"
            v-model="row.startTime"
            value-format="HH:mm:ss"
            disabled
          ></el-time-picker>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" prop="endTime" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-time-picker
            style="width: 120px"
            size="small"
            v-model="row.endTime"
            disabled
            value-format="HH:mm:ss"
          ></el-time-picker>
        </template>
      </el-table-column>
      <el-table-column label="价格" prop="price" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-input-number
            style="width: 120px"
            controls-position="right"
            size="small"
            v-model="row.price"
            :min="0"
            disabled
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column label="人数限制" prop="peopleLimit" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-input-number
            style="width: 120px"
            controls-position="right"
            size="small"
            v-model="row.peopleLimit"
            :min="0"
            disabled
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column label="是否锁定" prop="locked" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-switch size="small" v-model="row.locked" disabled></el-switch>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped lang="scss"></style>
