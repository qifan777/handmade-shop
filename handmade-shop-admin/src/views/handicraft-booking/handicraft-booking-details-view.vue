<script setup lang="ts">
import { onActivated, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { HandicraftBookingInput } from '@/apis/__generated/model/static'
import RemoteSelect from '@/components/base/form/remote-select.vue'
import { assertFormValidate } from '@/utils/common'
import { api } from '@/utils/api-instance'
import { useFormHelper } from '@/components/base/form/form-helper'
import { handicraftQueryOptions } from '@/views/handicraft/handicraft'

const props = defineProps<{ id?: string }>()
const formRef = ref<FormInstance>()
const initForm: HandicraftBookingInput = {
  date: '',
  startTime: '',
  endTime: '',
  handicraftId: '',
  locked: false,
  peopleLimit: 0,
  price: 0
}
const { formData: form, restForm } = useFormHelper<HandicraftBookingInput>(initForm)
const rules = reactive<FormRules<HandicraftBookingInput>>({
  startTime: [{ required: true, message: '请输入开始时间', trigger: 'blur' }],
  endTime: [{ required: true, message: '请输入结束时间', trigger: 'blur' }],
  date: [{ required: true, message: '请输入日期', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  peopleLimit: [{ required: true, message: '请输入人数限制', trigger: 'blur' }],
  locked: [{ required: true, message: '请输入是否锁定', trigger: 'blur' }],
  handicraftId: [{ required: true, message: '请输入手工', trigger: 'blur' }]
})
const handleConfirm = () => {
  formRef.value?.validate(
    assertFormValidate(() =>
      api.handicraftBookingForAdminController.save({ body: form.value }).then(async (res) => {
        form.value.id = res
        ElMessage.success('操作成功')
      })
    )
  )
}
onActivated(() => {
  if (props.id) {
    api.handicraftBookingForAdminController.findById({ id: props.id }).then((res) => {
      form.value = res
    })
  } else {
    restForm()
  }
})
</script>

<template>
  <div class="form">
    <el-form labelWidth="120" class="form" ref="formRef" :model="form" :rules="rules">
      <el-form-item label="开始时间" prop="startTime">
        <el-time-picker
          v-model="form.startTime"
          placeholder="请选择时间"
          value-format="HH:mm:ss"
        ></el-time-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-time-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="请选择时间"
          value-format="HH:mm:ss"
        ></el-time-picker>
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker
          v-model="form.date"
          type="date"
          placeholder="请选择日期"
          value-format="YYYY-MM-DD"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="form.price"></el-input-number>
      </el-form-item>
      <el-form-item label="人数限制" prop="peopleLimit">
        <el-input-number v-model="form.peopleLimit"></el-input-number>
      </el-form-item>
      <el-form-item label="是否锁定" prop="locked">
        <el-switch v-model="form.locked"></el-switch>
      </el-form-item>
      <el-form-item label="手工" prop="handicraftId">
        <remote-select
          label-prop="name"
          :query-options="handicraftQueryOptions"
          v-model="form.handicraftId"
        ></remote-select>
      </el-form-item>
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
