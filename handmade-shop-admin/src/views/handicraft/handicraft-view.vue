<script lang="ts" setup>
import HandicraftTable from './components/handicraft-table.vue'
import HandicraftQuery from './components/handicraft-query.vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import { api } from '@/utils/api-instance'
import { provide } from 'vue'
import type { HandicraftSpec } from '@/apis/__generated/model/static'
import { useQueryHelper } from '@/components/base/query/query-helper'

const initQuery: HandicraftSpec = {}
const tableHelper = useTableHelper(
  api.handicraftForAdminController.query,
  api.handicraftForAdminController,
  initQuery
)
const { query, restQuery } = useQueryHelper<HandicraftSpec>(initQuery)
provide('handicraftTableHelper', tableHelper)
</script>
<template>
  <div class="handicraft-view">
    <handicraft-query
      v-model:query="query"
      @reset="restQuery"
      @search="tableHelper.reloadTableData({ query })"
    ></handicraft-query>
    <handicraft-table></handicraft-table>
  </div>
</template>

<style lang="scss" scoped>
.handicraft-view {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
