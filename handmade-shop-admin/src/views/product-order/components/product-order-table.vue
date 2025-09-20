<script lang="ts" setup>
import { inject, onMounted } from 'vue'
import { api } from '@/utils/api-instance'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Scope } from '@/typings'
import type { ProductOrderDto } from '@/apis/__generated/model/dto'
import { Close, Promotion } from '@element-plus/icons-vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import DictColumn from '@/components/dict/dict-column.vue'
import { DictConstants } from '@/apis/__generated/model/enums/DictConstants'

type ProductOrderScope = Scope<ProductOrderDto['ProductOrderRepository/COMPLEX_FETCHER_FOR_ADMIN']>
type ProductOrderItemScope = Scope<
  ProductOrderDto['ProductOrderRepository/COMPLEX_FETCHER_FOR_ADMIN']['items'][0]
>

const productOrderTableHelper = inject(
  'productOrderTableHelper',
  useTableHelper(api.productOrderForAdminController.query, api.productOrderForAdminController, {})
)
const { loadTableData, handleSortChange, reloadTableData, pageData, loading, queryRequest, table } =
  productOrderTableHelper
onMounted(() => {
  reloadTableData()
})
const handleClose = (row: { id: string }) => {
  ElMessageBox.confirm('是否确认退款?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    api.productOrderForAdminController.paidCancelForAdmin({ id: row.id }).then(() => {
      ElMessage.success('退款成功')
      reloadTableData()
    })
  })
}
const handleDeliver = (row: { id: string }) => {
  ElMessageBox.prompt('请输入物流单号', '发货', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then((res) => {
    api.productOrderForAdminController
      .deliver({ id: row.id, trackingNumber: res.value })
      .then(() => {
        reloadTableData()
        ElMessage.success('发货成功')
      })
  })
}
</script>
<template>
  <div>
    <el-table
      ref="table"
      v-loading="loading"
      :border="true"
      :data="pageData.content"
      @sort-change="handleSortChange"
    >
      <el-table-column type="expand">
        <template v-slot:default="{ row }: ProductOrderScope">
          <el-table :data="row.items">
            <el-table-column label="购买数量" prop="name" sortable="custom">
              <template v-slot:default="{ row }: ProductOrderItemScope">
                {{ row.productCount }}
              </template>
            </el-table-column>
            <el-table-column label="商品名称" prop="name" sortable="custom">
              <template v-slot:default="{ row }: ProductOrderItemScope">
                {{ row.product.name }}
              </template>
            </el-table-column>
            <el-table-column label="封面" prop="cover" sortable="custom">
              <template v-slot:default="{ row }: ProductOrderItemScope">
                <el-avatar :src="row.product.cover" alt=""></el-avatar>
              </template>
            </el-table-column>
            <el-table-column label="价格" prop="price" sortable="custom">
              <template v-slot:default="{ row }: ProductOrderItemScope">
                {{ row.product.price }}
              </template>
            </el-table-column>
            <el-table-column label="库存" prop="stock" sortable="custom">
              <template v-slot:default="{ row }: ProductOrderItemScope">
                {{ row.product.stock }}
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column
        label="订单状态"
        prop="status"
        sortable="custom"
        min-width="100"
        show-overflow-tooltip
      >
        <template v-slot:default="{ row }: ProductOrderScope">
          <dict-column
            :dict-id="DictConstants.PRODUCT_ORDER_STATUS"
            :value="row.status"
          ></dict-column>
        </template>
      </el-table-column>
      <el-table-column label="支付详情">
        <el-table-column label="VIP优惠" prop="payAmount" sortable="custom">
          <template
            v-slot:default="{
              row: {
                baseOrder: { payment }
              }
            }: ProductOrderScope"
          >
            {{ payment.vipAmount }}
          </template>
        </el-table-column>
        <el-table-column label="邮费" prop="payAmount" sortable="custom">
          <template
            v-slot:default="{
              row: {
                baseOrder: { payment }
              }
            }: ProductOrderScope"
          >
            {{ payment.deliveryFee }}
          </template>
        </el-table-column>
        <el-table-column label="商品金额" prop="payAmount" sortable="custom">
          <template
            v-slot:default="{
              row: {
                baseOrder: { payment }
              }
            }: ProductOrderScope"
          >
            {{ payment.productAmount }}
          </template>
        </el-table-column>
        <el-table-column label="实付金额" prop="payAmount" sortable="custom">
          <template
            v-slot:default="{
              row: {
                baseOrder: { payment }
              }
            }: ProductOrderScope"
          >
            {{ payment.payAmount }}
          </template>
        </el-table-column>
        <el-table-column
          label="支付时间"
          prop="payTime"
          sortable="custom"
          min-width="130"
          show-overflow-tooltip
        >
          <template
            v-slot:default="{
              row: {
                baseOrder: { payment }
              }
            }: ProductOrderScope"
          >
            {{ payment.payTime }}
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column label="地址详情">
        <el-table-column label="姓名" prop="realName">
          <template v-slot:default="{ row: { baseOrder } }: ProductOrderScope">
            {{ baseOrder.address?.realName }}
          </template>
        </el-table-column>
        <el-table-column label="电话" prop="phoneNumber" width="130" show-overflow-tooltip>
          <template v-slot:default="{ row: { baseOrder } }: ProductOrderScope">
            {{ baseOrder.address?.phoneNumber }}
          </template>
        </el-table-column>
        <el-table-column label="地址信息" prop="address" width="100" show-overflow-tooltip>
          <template v-slot:default="{ row: { baseOrder } }: ProductOrderScope">
            <div>
              {{ baseOrder.address?.details + ' ' + baseOrder.address?.houseNumber }}
            </div>
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column
        label="备注"
        prop="remark"
        sortable="custom"
        min-width="100"
        show-overflow-tooltip
      >
        <template v-slot:default="{ row: { baseOrder } }: ProductOrderScope">
          {{ baseOrder.remark }}
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        prop="createdTime"
        show-overflow-tooltip
        sortable="custom"
        min-width="150"
      >
        <template v-slot:default="{ row }: ProductOrderScope">
          {{ row.createdTime }}
        </template>
      </el-table-column>
      <el-table-column
        label="更新时间"
        prop="editedTime"
        show-overflow-tooltip
        sortable="custom"
        min-width="150"
      >
        <template v-slot:default="{ row }: ProductOrderScope">
          {{ row.editedTime }}
        </template>
      </el-table-column>
      <el-table-column
        label="创建人"
        prop="creator.phone"
        show-overflow-tooltip
        sortable="custom"
        width="150"
      >
        <template v-slot:default="{ row }: ProductOrderScope">
          {{ row.creator.nickname }}({{ row.creator.phone }})
        </template>
      </el-table-column>
      <el-table-column
        label="更新人"
        prop="editor.phone"
        show-overflow-tooltip
        sortable="custom"
        width="150"
      >
        <template v-slot:default="{ row }: ProductOrderScope">
          {{ row.editor.nickname }}({{ row.editor.phone }})
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作">
        <template v-slot:default="{ row }: ProductOrderScope">
          <div>
            <el-button
              class="edit-btn"
              link
              size="small"
              type="primary"
              @click="handleClose(row)"
              v-if="row.status === 'TO_BE_RECEIVED' || row.status === 'TO_BE_DELIVERED'"
            >
              <el-icon>
                <close />
              </el-icon>
            </el-button>
            <el-button
              class="edit-btn"
              link
              size="small"
              type="success"
              v-if="row.status === 'TO_BE_DELIVERED' || row.status == 'TO_BE_RECEIVED'"
              @click="handleDeliver(row)"
            >
              <el-icon>
                <promotion></promotion>
              </el-icon>
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div class="page">
      <el-pagination
        :current-page="queryRequest.pageNum"
        :page-size="queryRequest.pageSize"
        :page-sizes="[10, 20, 30, 40, 50]"
        :total="pageData.totalElements"
        background
        layout="prev, pager, next, jumper, total, sizes"
        small
        style="margin-top: 30px"
        @current-change="(pageNum) => loadTableData({ pageNum })"
        @size-change="(pageSize) => loadTableData({ pageSize })"
      />
    </div>
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
