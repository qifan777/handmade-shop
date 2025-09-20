<script lang="ts" setup>
import ProductOrderTable from './components/product-order-table.vue'
import ProductOrderQuery from './components/product-order-query.vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import { api } from '@/utils/api-instance'
import { provide } from 'vue'
import type { ProductOrderSpec } from '@/apis/__generated/model/static'
import { useQueryHelper } from '@/components/base/query/query-helper'

const initQuery: ProductOrderSpec = {}
const tableHelper = useTableHelper(
  api.productOrderForAdminController.query,
  api.productOrderForAdminController,
  initQuery
)
const { query, restQuery } = useQueryHelper<ProductOrderSpec>(initQuery)
provide('productOrderTableHelper', tableHelper)
</script>
<template>
  <div class="product-order-view">
    <product-order-query
      v-model:query="query"
      @reset="restQuery"
      @search="tableHelper.reloadTableData({ query })"
    ></product-order-query>
    <product-order-table></product-order-table>
  </div>
</template>

<style lang="scss" scoped>
.product-order-view {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
