<template>
  <div class="order-list-page">
    <nut-tabs class="tabs" v-model="activeStatus" @change="handleTabChange">
      <nut-tab-pane
        v-for="tab in tabList"
        :key="tab.keyId"
        :title="tab.keyName"
        :pane-key="tab.keyEnName"
      >
        <div class="order" v-for="order in pageData.content" :key="order.id">
          <order-row
            :order="order"
            @click="switchPage('./order-details?id=' + order.id)"
          >
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
              <nut-button
                size="small"
                type="danger"
                plain
                v-if="order.status === 'TO_BE_RECEIVED'"
                @click="trackingDetails"
                >查看物流</nut-button
              >
            </template>
          </order-row>
        </div>
      </nut-tab-pane>
    </nut-tabs>
  </div>
</template>
<script setup lang="ts">
import { usePageHelper } from "@/utils/page";
import { api } from "@/utils/api-instance";
import OrderRow from "@/components/order/order-row.vue";
import { ProductOrderStatus } from "@/apis/__generated/model/enums";
import { switchPage } from "@/utils/common";
import { Dictionaries } from "@/apis/__generated/model/enums/DictConstants";
import { ref } from "vue";
import { handleProductWeChatPay } from "@/pages/order/order-store";
import Taro from "@tarojs/taro";

const { TO_BE_PAID, TO_BE_DELIVERED, TO_BE_RECEIVED, FINISHED, REFUNDED } =
  Dictionaries.ProductOrderStatus;
const tabList = [
  TO_BE_PAID,
  TO_BE_DELIVERED,
  TO_BE_RECEIVED,
  FINISHED,
  REFUNDED,
];
const activeStatus = ref<ProductOrderStatus>("TO_BE_PAID");
const handleTabChange = () => {
  reloadPageData({ query: { status: activeStatus.value } });
};
const { pageData, reloadPageData } = usePageHelper(
  api.productOrderForFrontController.query,
  api.productOrderForFrontController,
  { query: { status: activeStatus.value } },
);
const handleCancel = async (id: string) => {
  Taro.showModal({
    title: "是否确认取消订单",
    success: (actionRes) => {
      if (actionRes.confirm) {
        Taro.showLoading();
        api.productOrderForFrontController
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
  await handleProductWeChatPay(id);
  await reloadPageData();
};
const trackingDetails = (trackingNumber: string) => {
  Taro.navigateToMiniProgram({
    appId: "wx6885acbedba59c14",
    path: `pages/result/result?nu=${trackingNumber}&com=&querysource=third_xcx`,
  });
};
</script>
<style lang="scss">
@import "../../app.scss";
page {
  background-color: #f5f5f5;
}

.order-list-page {
  .nut-tabs__titles-item__line {
    background: $primary-color !important;
  }
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
