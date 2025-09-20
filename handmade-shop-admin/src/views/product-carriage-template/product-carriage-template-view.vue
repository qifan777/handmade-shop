<script lang="ts" setup>
import ProductCarriageTemplateTable from './components/product-carriage-template-table.vue'
import ProductCarriageTemplateQuery from './components/product-carriage-template-query.vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import { api } from '@/utils/api-instance'
import { provide } from 'vue'
import type { ProductCarriageTemplateSpec } from '@/apis/__generated/model/static'
import { useQueryHelper } from '@/components/base/query/query-helper'

const initQuery: ProductCarriageTemplateSpec = {}
const tableHelper = useTableHelper(
  api.productCarriageTemplateForAdminController.query,
  api.productCarriageTemplateForAdminController,
  initQuery
)
const { query, restQuery } = useQueryHelper<ProductCarriageTemplateSpec>(initQuery)
provide('productCarriageTemplateTableHelper', tableHelper)
</script>
<template>
  <div class="product-carriage-template-view">
    <product-carriage-template-query
      v-model:query="query"
      @reset="restQuery"
      @search="tableHelper.reloadTableData({ query })"
    ></product-carriage-template-query>
    <product-carriage-template-table></product-carriage-template-table>
  </div>
</template>

<style lang="scss" scoped>
.product-carriage-template-view {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
