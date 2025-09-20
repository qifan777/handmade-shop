<script lang="ts" setup>
import HandicraftCategoryTable from './components/handicraft-category-table.vue'
import HandicraftCategoryQuery from './components/handicraft-category-query.vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import { api } from '@/utils/api-instance'
import { provide } from 'vue'
import type { HandicraftCategorySpec } from '@/apis/__generated/model/static'
import { useQueryHelper } from '@/components/base/query/query-helper'

const initQuery: HandicraftCategorySpec = {}
const tableHelper = useTableHelper(
  api.handicraftCategoryForAdminController.query,
  api.handicraftCategoryForAdminController,
  initQuery
)
const { query, restQuery } = useQueryHelper<HandicraftCategorySpec>(initQuery)
provide('handicraftCategoryTableHelper', tableHelper)
</script>
<template>
  <div class="handicraft-category-view">
    <handicraft-category-query
      v-model:query="query"
      @reset="restQuery"
      @search="tableHelper.reloadTableData({ query })"
    ></handicraft-category-query>
    <handicraft-category-table></handicraft-category-table>
  </div>
</template>

<style lang="scss" scoped>
.handicraft-category-view {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
