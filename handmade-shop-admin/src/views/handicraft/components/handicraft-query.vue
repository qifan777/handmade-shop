<script lang="ts" setup>
import RemoteSelect from '@/components/base/form/remote-select.vue'
import type { HandicraftSpec } from '@/apis/__generated/model/static'
import { handicraftCategoryQueryOptions } from '@/views/handicraft-category/handicraft-category'
import { DictConstants } from '@/apis/__generated/model/enums/DictConstants'
import DictSelect from '@/components/dict/dict-select.vue'

const emit = defineEmits<{ search: [value: HandicraftSpec]; reset: [] }>()
const query = defineModel<HandicraftSpec>('query', { required: true })
</script>
<template>
  <div class="search">
    <el-form inline label-width="80" size="small">
      <el-form-item label="手工名称">
        <el-input v-model.trim="query.name"></el-input>
      </el-form-item>
      <el-form-item label="价格">
        <el-input-number v-model="query.price" controls-position="right"></el-input-number>
      </el-form-item>
      <el-form-item label="标签"> </el-form-item>
      <el-form-item label="描述">
        <el-input v-model.trim="query.description"></el-input>
      </el-form-item>
      <el-form-item label="优惠价格">
        <el-input-number v-model="query.discountPrice" controls-position="right"></el-input-number>
      </el-form-item>
      <el-form-item label="提示">
        <el-input v-model.trim="query.tip"></el-input>
      </el-form-item>
      <el-form-item label="类别">
        <remote-select
          label-prop="name"
          :query-options="handicraftCategoryQueryOptions"
          v-model="query.categoryId"
        ></remote-select>
      </el-form-item>
      <el-form-item label="计费方式">
        <dict-select
          :dict-id="DictConstants.CHARGING_TYPE"
          v-model="query.chargingType"
        ></dict-select>
      </el-form-item>
      <el-form-item label="是否锁定">
        <el-switch v-model="query.locked"></el-switch>
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
