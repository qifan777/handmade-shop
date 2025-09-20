<script lang="ts" setup>
import type { Scope } from '@/typings'
import type {
  HandicraftBookingConfigInput,
  HandicraftBookingView
} from '@/apis/__generated/model/static'
import { Close, Plus } from '@element-plus/icons-vue'

withDefaults(defineProps<{ update?: boolean }>(), { update: false })
type HandicraftBookingScope = Scope<HandicraftBookingView>
const bookings = defineModel<HandicraftBookingConfigInput[]>('bookings', { required: true })
const handleCreate = () => {
  bookings.value.push({
    startTime: '00:00:00',
    endTime: '00:00:00',
    price: 0,
    peopleLimit: 0,
    locked: false
  })
}
const handleRemove = (row: HandicraftBookingView) => {
  bookings.value = bookings.value.filter((item) => item != row)
}
</script>
<template>
  <div>
    <div class="button-section">
      <el-button type="success" size="small" @click="handleCreate">
        <el-icon>
          <plus />
        </el-icon>
        新增
      </el-button>
    </div>
    <el-table ref="table" :data="bookings" :border="true">
      <el-table-column label="开始时间" prop="startTime" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-time-picker
            style="width: 120px"
            size="small"
            v-model="row.startTime"
            value-format="HH:mm:ss"
          ></el-time-picker>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" prop="endTime" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-time-picker
            style="width: 120px"
            size="small"
            v-model="row.endTime"
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
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column label="是否锁定" prop="locked" sortable="custom" min-width="120">
        <template v-slot:default="{ row }: HandicraftBookingScope">
          <el-switch size="small" v-model="row.locked"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" v-if="!update">
        <template #default="{ row }: HandicraftBookingScope">
          <el-button :icon="Close" link type="danger" @click="handleRemove(row)"> </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
.button-section {
  margin: 20px 0;
}

.page {
  display: flex;
  justify-content: flex-end;
}
</style>
