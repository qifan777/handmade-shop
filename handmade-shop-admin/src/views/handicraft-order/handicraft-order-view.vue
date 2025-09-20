<script lang="ts" setup>
import HandicraftOrderTable from './components/handicraft-order-table.vue'
import HandicraftOrderQuery from './components/handicraft-order-query.vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import { api } from '@/utils/api-instance'
import { provide } from 'vue'
import type { HandicraftOrderSpec } from '@/apis/__generated/model/static'
import { useQueryHelper } from '@/components/base/query/query-helper'

const initQuery: HandicraftOrderSpec = {}
const tableHelper = useTableHelper(
  api.handicraftOrderForAdminController.query,
  api.handicraftOrderForAdminController,
  initQuery
)
const { query, restQuery } = useQueryHelper<HandicraftOrderSpec>(initQuery)
provide('handicraftOrderTableHelper', tableHelper)
</script>
<template>
  <div class="handicraft-order-view">
    <handicraft-order-query
      v-model:query="query"
      @reset="restQuery"
      @search="tableHelper.reloadTableData({ query })"
    ></handicraft-order-query>
    <handicraft-order-table></handicraft-order-table>
  </div>
</template>

<style lang="scss" scoped>
.handicraft-order-view {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
