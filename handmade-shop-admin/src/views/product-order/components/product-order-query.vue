<script lang="ts" setup>
import type { ProductOrderSpec } from '@/apis/__generated/model/static'
import { DictConstants } from '@/apis/__generated/model/enums/DictConstants'
import DictSelect from '@/components/dict/dict-select.vue'
import DatetimePicker from '@/components/datetime/datetime-picker.vue'

const emit = defineEmits<{ search: [value: ProductOrderSpec]; reset: [] }>()
const query = defineModel<ProductOrderSpec>('query', { required: true })
</script>
<template>
  <div class="search">
    <el-form inline label-width="80" size="small">
      <el-form-item label="订单状态">
        <dict-select
          v-model="query.status"
          :dict-id="DictConstants.PRODUCT_ORDER_STATUS"
        ></dict-select>
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="query.name"></el-input>
      </el-form-item>
      <el-form-item label="创建人手机号">
        <el-input v-model="query.phone"></el-input>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="query.remark"></el-input>
      </el-form-item>
      <el-form-item label="支付时间">
        <datetime-picker
          v-model:min-date-time="query.minPayTime"
          v-model:max-date-time="query.maxPayTime"
        >
        </datetime-picker>
      </el-form-item>
      <el-form-item label=" ">
        <div class="btn-wrapper">
          <el-button size="small" type="primary" @click="() => emit('search', query)">
            查询
          </el-button>
          <el-button size="small" type="warning" @click="() => emit('reset')"> 重置</el-button>
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
