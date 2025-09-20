<script lang="ts" setup>
import { inject, onMounted } from 'vue'
import { api } from '@/utils/api-instance'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Scope } from '@/typings'
import type { HandicraftOrderDto } from '@/apis/__generated/model/dto'
import { Close } from '@element-plus/icons-vue'
import { useTableHelper } from '@/components/base/table/table-helper'
import DictColumn from '@/components/dict/dict-column.vue'
import { DictConstants } from '@/apis/__generated/model/enums/DictConstants'
import HandicraftOrderRow from '@/views/handicraft-order/components/handicraft-order-row.vue'

type HandicraftOrderScope = Scope<
  HandicraftOrderDto['HandicraftOrderRepository/COMPLEX_FETCHER_FOR_ADMIN']
>
const handicraftOrderTableHelper = inject(
  'handicraftOrderTableHelper',
  useTableHelper(
    api.handicraftOrderForAdminController.query,
    api.handicraftOrderForAdminController,
    {}
  )
)
const { loadTableData, handleSortChange, reloadTableData, pageData, loading, queryRequest, table } =
  handicraftOrderTableHelper
onMounted(() => {
  reloadTableData()
})

const handleClose = (row: { id: string }) => {
  ElMessageBox.confirm('此操作将发起退款, 是否继续?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    api.handicraftOrderForAdminController.paidCancelForAdmin({ id: row.id }).then(() => {
      ElMessage.success('退款成功')
    })
  })
}
</script>
<template>
  <div>
    <el-table
      ref="table"
      :data="pageData.content"
      :border="true"
      @sort-change="handleSortChange"
      v-loading="loading"
    >
      <el-table-column type="expand">
        <template #default="{ row }: HandicraftOrderScope">
          <handicraft-order-row :order="row"></handicraft-order-row>
        </template>
      </el-table-column>

      <el-table-column
        label="手工名称"
        prop="name"
        sortable="custom"
        show-overflow-tooltip
        width="120"
      >
        <template v-slot:default="{ row }: HandicraftOrderScope">
          {{ row.handicraft.name }}
        </template>
      </el-table-column>
      <el-table-column
        label="封面"
        prop="cover"
        sortable="custom"
        show-overflow-tooltip
        width="120"
      >
        <template v-slot:default="{ row }: HandicraftOrderScope">
          <el-avatar :src="row.handicraft.cover" alt=""></el-avatar>
        </template>
      </el-table-column>
      <el-table-column
        label="订单状态"
        prop="status"
        sortable="custom"
        show-overflow-tooltip
        width="120"
      >
        <template v-slot:default="{ row }: HandicraftOrderScope">
          <dict-column
            :dict-id="DictConstants.BOOKING_ORDER_STATUS"
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
            }: HandicraftOrderScope"
          >
            {{ payment.vipAmount }}
          </template>
        </el-table-column>
        <el-table-column label="预约金额" prop="payAmount" sortable="custom">
          <template
            v-slot:default="{
              row: {
                baseOrder: { payment }
              }
            }: HandicraftOrderScope"
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
            }: HandicraftOrderScope"
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
            }: HandicraftOrderScope"
          >
            {{ payment.payTime }}
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column
        label="备注"
        prop="remark"
        sortable="custom"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot:default="{ row: { baseOrder } }: HandicraftOrderScope">
          {{ baseOrder.remark }}
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        prop="createdTime"
        sortable="custom"
        show-overflow-tooltip
        width="150"
      >
        <template v-slot:default="{ row }: HandicraftOrderScope">
          {{ row.createdTime }}
        </template>
      </el-table-column>
      <el-table-column
        label="更新时间"
        prop="editedTime"
        sortable="custom"
        show-overflow-tooltip
        width="150"
      >
        <template v-slot:default="{ row }: HandicraftOrderScope">
          {{ row.editedTime }}
        </template>
      </el-table-column>
      <el-table-column
        label="创建人"
        prop="creator.phone"
        sortable="custom"
        show-overflow-tooltip
        width="150"
      >
        <template v-slot:default="{ row }: HandicraftOrderScope">
          {{ row.creator.nickname }}({{ row.creator.phone }})
        </template>
      </el-table-column>
      <el-table-column
        label="更新人"
        prop="editor.phone"
        sortable="custom"
        show-overflow-tooltip
        width="150"
      >
        <template v-slot:default="{ row }: HandicraftOrderScope">
          {{ row.editor.nickname }}({{ row.editor.phone }})
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template v-slot:default="{ row }: HandicraftOrderScope">
          <div>
            <el-button
              class="edit-btn"
              link
              size="small"
              type="primary"
              @click="handleClose(row)"
              v-if="row.status == 'TO_BE_USED'"
            >
              <el-icon>
                <close />
              </el-icon>
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div class="page">
      <el-pagination
        style="margin-top: 30px"
        :current-page="queryRequest.pageNum"
        :page-size="queryRequest.pageSize"
        :page-sizes="[10, 20, 30, 40, 50]"
        :total="pageData.totalElements"
        background
        small
        layout="prev, pager, next, jumper, total, sizes"
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
