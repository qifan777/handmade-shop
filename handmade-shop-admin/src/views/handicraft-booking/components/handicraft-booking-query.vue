<script lang="ts" setup>
import RemoteSelect from '@/components/base/form/remote-select.vue'
import type { HandicraftBookingSpec } from '@/apis/__generated/model/static'
import { handicraftQueryOptions } from '@/views/handicraft/handicraft'
import DatePicker from '@/components/datetime/date-picker.vue'
import TimePicker from '@/components/datetime/time-picker.vue'

const emit = defineEmits<{ search: [value: HandicraftBookingSpec]; reset: [] }>()
const query = defineModel<HandicraftBookingSpec>('query', { required: true })
</script>
<template>
  <div class="search">
    <el-form inline label-width="80" size="small">
      <el-form-item label="开始时间">
        <time-picker v-model:min-time="query.minStartTime" v-model:max-time="query.maxStartTime">
        </time-picker>
      </el-form-item>
      <el-form-item label="结束时间">
        <time-picker v-model:min-time="query.minEndTime" v-model:max-time="query.maxEndTime">
        </time-picker>
      </el-form-item>
      <el-form-item label="日期">
        <date-picker v-model:min-date="query.minDate" v-model:max-date="query.maxDate">
        </date-picker>
      </el-form-item>
      <el-form-item label="价格">
        <el-input-number v-model="query.price" controls-position="right"></el-input-number>
      </el-form-item>
      <el-form-item label="人数限制">
        <el-input-number v-model="query.peopleLimit" controls-position="right"></el-input-number>
      </el-form-item>
      <el-form-item label="是否锁定">
        <el-switch v-model="query.locked"></el-switch>
      </el-form-item>
      <el-form-item label="手工">
        <remote-select
          label-prop="name"
          :query-options="handicraftQueryOptions"
          v-model="query.handicraftId"
        ></remote-select>
      </el-form-item>
      <el-form-item label=" ">
        <div class="btn-wrapper">
          <el-button type="primary" size="small" @click="() => emit('search', query)">
            查询
          </el-button>
          <el-button type="warning" size="small" @click="() => emit('reset')"> 重置</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-form-item) {
  margin-bottom: 5px;
}

.search {
  display: flex;
  flex-flow: column nowrap;
  width: 100%;

  .btn-wrapper {
    margin-left: 20px;
  }
}
</style>
