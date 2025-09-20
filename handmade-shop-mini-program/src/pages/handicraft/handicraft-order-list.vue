<template>
  <div class="handicraft-order-list-page">
    <nut-tabs class="tabs" v-model="activeStatus" @change="handleTabChange">
      <nut-tab-pane
        v-for="tab in tabList"
        :key="tab.keyId"
        :title="tab.keyName"
        :pane-key="tab.keyEnName"
      >
        <div class="order" v-for="order in pageData.content" :key="order.id">
          <handicraft-order-row :order="order">
            <template #buttons>
              <nut-button
                size="small"
                plain
                v-if="order.status === 'TO_BE_PAID'"
                @click.stop="handleCancel(order.id)"
                >取消订单
              </nut-button>
              <nut-button
                size="small"
                type="success"
                plain
                v-if="order.status === 'TO_BE_PAID'"
                @click.stop="handlePay(order.id)"
                >立即支付
              </nut-button>
            </template>
          </handicraft-order-row>
        </div>
      </nut-tab-pane>
    </nut-tabs>
  </div>
</template>
<script setup lang="ts">
import { usePageHelper } from "@/utils/page";
import { api } from "@/utils/api-instance";
import { BookingOrderStatus } from "@/apis/__generated/model/enums";
import { Dictionaries } from "@/apis/__generated/model/enums/DictConstants";
import { ref } from "vue";
import { handleHandicraftWeChatPay } from "@/pages/order/order-store";
import Taro from "@tarojs/taro";
import HandicraftOrderRow from "@/pages/handicraft/handicraft-order-row.vue";

const { TO_BE_PAID, TO_BE_CONFIRMED, FINISHED, REFUNDED } =
  Dictionaries.BookingOrderStatus;
const tabList = [TO_BE_PAID, TO_BE_CONFIRMED, FINISHED, REFUNDED];
const activeStatus = ref<BookingOrderStatus>("TO_BE_PAID");
const handleTabChange = () => {
  reloadPageData({ query: { status: activeStatus.value } });
};
const { pageData, reloadPageData } = usePageHelper(
  api.handicraftOrderForFrontController.query,
  api.handicraftOrderForFrontController,
  { query: { status: activeStatus.value } },
);
const handleCancel = async (id: string) => {
  Taro.showModal({
    title: "是否确认取消订单",
    success: (actionRes) => {
      if (actionRes.confirm) {
        Taro.showLoading();
        api.handicraftOrderForFrontController
          .unpaidCancelForUser({ id })
          .then(() => {
            Taro.showToast({
              title: "取消成功",
              icon: "success",
            });
            Taro.hideLoading();
            reloadPageData();
          });
      }
    },
  });
};
const handlePay = async (id: string) => {
  await handleHandicraftWeChatPay(id);
  reloadPageData();
};
</script>
<style lang="scss">
page {
  background-color: #f5f5f5;
}

.handicraft-order-list-page {
  .nut-tabs__list {
    background-color: white;
  }

  .nut-tab-pane {
    padding: 0;
    background-color: #f5f5f5;
  }

  .order {
    margin: 20px;
  }
}
</style>
