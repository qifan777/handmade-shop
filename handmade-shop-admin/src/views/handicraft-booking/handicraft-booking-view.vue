<script lang="ts" setup>
import HandicraftBookingTable from './components/handicraft-booking-table.vue'
import HandicraftBookingQuery from './components/handicraft-booking-query.vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import { api } from '@/utils/api-instance'
import { provide } from 'vue'
import type { HandicraftBookingSpec } from '@/apis/__generated/model/static'
import { useQueryHelper } from '@/components/base/query/query-helper'

const initQuery: HandicraftBookingSpec = {}
const tableHelper = useTableHelper(
  api.handicraftBookingForAdminController.query,
  api.handicraftBookingForAdminController,
  initQuery
)
const { query, restQuery } = useQueryHelper<HandicraftBookingSpec>(initQuery)
provide('handicraftBookingTableHelper', tableHelper)
</script>
<template>
  <div class="handicraft-booking-view">
    <handicraft-booking-query
      v-model:query="query"
      @reset="restQuery"
      @search="tableHelper.reloadTableData({ query })"
    ></handicraft-booking-query>
    <handicraft-booking-table></handicraft-booking-table>
  </div>
</template>

<style lang="scss" scoped>
.handicraft-booking-view {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
