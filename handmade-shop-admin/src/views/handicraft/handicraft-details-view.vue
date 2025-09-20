<script setup lang="ts">
import { onActivated, reactive, ref, watchEffect } from 'vue'

import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { HandicraftInput } from '@/apis/__generated/model/static'
import RemoteSelect from '@/components/base/form/remote-select.vue'
import ImageUpload from '@/components/image/image-upload.vue'
import ValueInput from '@/components/key-value/value-input.vue'
import { assertFormValidate, handleUploadSuccess } from '@/utils/common'
import { api } from '@/utils/api-instance'
import { useFormHelper } from '@/components/base/form/form-helper'
import DictSelect from '@/components/dict/dict-select.vue'
import { DictConstants } from '@/apis/__generated/model/enums/DictConstants'
import { handicraftCategoryQueryOptions } from '@/views/handicraft-category/handicraft-category'
import HandicraftBookingConfigTable from '@/views/handicraft/components/handicraft-booking-config-table.vue'
import dayjs from 'dayjs'
import { Plus } from '@element-plus/icons-vue'

const API_PREFIX = import.meta.env.VITE_API_PREFIX

const props = defineProps<{ id?: string }>()
const formRef = ref<FormInstance>()
const handlePeriodChange = (value: string) => {
  const keyMap: Record<string, number> = {
    HALF: 0.5,
    ONE: 1,
    TWO: 2,
    FOUR: 4,
    EIGHT: 8,
    TWELVE: 12,
    TWENTY_FOUR: 24
  }
  form.value.periods = []
  for (let i = 0; i < 24 / keyMap[value]; i++) {
    form.value.periods.push({
      startTime: dayjs('00:00:00', 'HH:mm:ss')
        .add(i * keyMap[value], 'hour')
        .format('HH:mm:ss'),
      endTime: dayjs('00:00:00', 'HH:mm:ss')
        .add((i + 1) * keyMap[value], 'hour')
        .format('HH:mm:ss'),
      locked: form.value.locked,
      peopleLimit: form.value.peopleLimit,
      price: form.value.price
    })
  }
}
const initForm: HandicraftInput & { period: string; peopleLimit: number } = {
  chargingType: 'PERIOD',
  cover: '',
  locked: false,
  name: '',
  periods: [],
  price: 0,
  period: '',
  peopleLimit: 0,
  tags: [],
  categoryId: '',
  primaryPictures: [],
  video: []
}
const { formData: form, restForm } = useFormHelper<
  HandicraftInput & { period: string; peopleLimit: number }
>(initForm)
const rules = reactive<FormRules<HandicraftInput>>({
  name: [{ required: true, message: '请输入手工名称', trigger: 'blur' }],
  cover: [{ required: true, message: '请输入封面', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  primaryPictures: [{ required: true, message: '请输入主图', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请输入类别', trigger: 'blur' }],
  chargingType: [{ required: true, message: '请输入计费方式', trigger: 'change' }],
  locked: [{ required: true, message: '请输入是否锁定', trigger: 'blur' }]
})
const handleConfirm = () => {
  form.value.primaryPictures = form.value.primaryPictures.filter((item) => item.status == 'success')
  if (form.value.video) {
    console.log(form.value.video)
    form.value.video = form.value.video.filter((item) => item.status == 'success')
  }
  formRef.value?.validate(
    assertFormValidate(() =>
      api.handicraftForAdminController.save({ body: form.value }).then(async (res) => {
        form.value.id = res
        ElMessage.success('操作成功')
      })
    )
  )
}
onActivated(() => {
  if (props.id) {
    api.handicraftForAdminController.findById({ id: props.id }).then((res) => {
      form.value = { ...res, period: 'ONE', peopleLimit: 0 }
    })
  } else {
    restForm()
  }
})
watchEffect(() => {
  form.value.periods.forEach((period) => {
    period.peopleLimit = form.value.peopleLimit
    period.locked = form.value.locked
    period.price = form.value.price
  })
})
</script>

<template>
  <div class="form">
    <div>
      修改手工预约的"价格"、"预约人数限制"、"是否锁定"会批量修改时间段中的相应字段。创建手工后无法再删除时间段，只能锁定时间段
    </div>
    <el-form labelWidth="120" class="form" ref="formRef" :model="form" :rules="rules">
      <el-form-item label="手工名称" prop="name">
        <el-input v-model.trim="form.name"></el-input>
      </el-form-item>
      <el-form-item label="封面" prop="cover">
        <image-upload v-model="form.cover"></image-upload>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="form.price"></el-input-number>
      </el-form-item>
      <el-form-item label="标签" prop="tags">
        <value-input v-model="form.tags"></value-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="主图" prop="primaryPictures">
        <el-upload
          v-model:file-list="form.primaryPictures"
          :action="`${API_PREFIX}/oss/upload`"
          list-type="picture-card"
          :on-success="handleUploadSuccess"
        >
          <el-icon>
            <plus></plus>
          </el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="视频">
        <el-upload
          v-model:file-list="form.video"
          :limit="1"
          :on-success="handleUploadSuccess"
          :action="`${API_PREFIX}/oss/upload`"
        >
          <el-icon>
            <plus></plus>
          </el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="提示" prop="tip">
        <el-input v-model.trim="form.tip"></el-input>
      </el-form-item>
      <el-form-item label="类别" prop="categoryId">
        <remote-select
          label-prop="name"
          :query-options="handicraftCategoryQueryOptions"
          v-model="form.categoryId"
        ></remote-select>
      </el-form-item>
      <el-form-item label="计费方式" prop="chargingType">
        <dict-select
          :dict-id="DictConstants.CHARGING_TYPE"
          v-model="form.chargingType"
        ></dict-select>
      </el-form-item>
      <el-form-item label="预约人数限制" prop="peopleLimit">
        <el-input-number v-model="form.peopleLimit"></el-input-number>
      </el-form-item>
      <el-form-item label="是否锁定" prop="locked">
        <el-switch v-model="form.locked"></el-switch>
      </el-form-item>
      <el-form-item label="时间段间隔" prop="period" v-if="!form.id">
        <dict-select
          :dict-id="DictConstants.BOOKING_PERIOD"
          v-model="form.period"
          @change="handlePeriodChange"
        ></dict-select>
      </el-form-item>
      <handicraft-booking-config-table
        :bookings="form.periods"
        :update="!!form.id"
      ></handicraft-booking-config-table>
    </el-form>
    <el-row justify="center">
      <el-button type="primary" @click="handleConfirm">提交</el-button>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.form {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
