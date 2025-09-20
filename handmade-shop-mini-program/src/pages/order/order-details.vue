<template>
  <div class="order-details" v-if="order">
    <div class="remark" v-if="order.baseOrder.remark">
      <nut-cell is-link center>
        <template #icon>
          <image class="icon" src="@/assets/icons/editor.png"></image>
        </template>
        <template #desc>
          <textarea
            v-model="order.baseOrder.remark"
            style="height: 100rpx"
            placeholder="请输入备注"
            readonly
          ></textarea>
        </template>
        <template #link>
          <div></div>
        </template>
      </nut-cell>
    </div>
    <div class="product-list">
      <product-row
        v-for="item in order.items"
        :key="item.id"
        :product="item.product"
      >
        <template #operation>
          <div class="sku-count">x{{ item.productCount }}</div>
        </template>
      </product-row>
    </div>
    <nut-cell-group class="summary">
      <nut-cell title="商品总价">
        <template #desc>
          <div class="value">￥{{ order.baseOrder.payment.productAmount }}</div>
        </template>
      </nut-cell>
      <nut-cell title="运费">
        <template #desc>
          <div class="value">￥{{ order.baseOrder.payment.deliveryFee }}</div>
        </template>
      </nut-cell>
      <nut-cell title="会员优惠">
        <template #desc>
          <div class="value">-￥{{ order.baseOrder.payment.vipAmount }}</div>
        </template>
      </nut-cell>
      <nut-cell title="实际支付">
        <template #desc>
          <div class="value">￥{{ order.baseOrder.payment.payAmount }}</div>
        </template>
      </nut-cell>
    </nut-cell-group>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ProductOrderDto } from "@/apis/__generated/model/dto";
import { api } from "@/utils/api-instance";
import Taro from "@tarojs/taro";

const order =
  ref<ProductOrderDto["ProductOrderRepository/COMPLEX_FETCHER_FOR_FRONT"]>();
const init = (id: string) => {
  api.productOrderForFrontController.findById({ id }).then((res) => {
    order.value = res;
  });
};
Taro.useLoad(({ id }) => {
  init(id);
});
</script>

<style lang="scss">
@import "../../app.scss";

page {
  background-color: rgba(black, 0.05);
}

.order-details {
  .icon {
    width: 40px;
    height: 40px;
  }

  .address {
    background-color: white;
    border-bottom-left-radius: 12px;
    border-bottom-right-radius: 12px;

    .nut-cell {
      margin-top: 0;
    }

    .address-row {
      padding: 0;
    }
  }

  .sku-count {
    font-size: 28px;
  }

  .product-list {
    background-color: white;
    padding: 32px;
    border-radius: 12px;
    margin-bottom: 30px;
  }

  .summary {
    border-top-left-radius: 30px;
    border-top-right-radius: 30px;

    .value {
      color: rgba(black, 0.9);
      font-weight: bold;
    }
  }

  .deliver {
    background-color: white;
    padding: 25px;

    .phone {
      width: 50px;
    }
  }
}
</style>
