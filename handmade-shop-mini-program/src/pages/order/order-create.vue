<template>
  <div class="order-submit">
    <div class="address">
      <nut-cell
        is-link
        center
        @click="switchPage('/pages/address/address-list?from=order')"
      >
        <template #icon>
          <image class="icon" src="@/assets/icons/local.png"></image>
        </template>
        <template #title>
          <address-row :address="address" v-if="address"></address-row>
        </template>
        <template #desc v-if="!address">
          <div>请选择收货地址</div>
        </template>
        <template #link>
          <rect-right></rect-right>
        </template>
      </nut-cell>
    </div>
    <div class="remark">
      <nut-cell is-link center>
        <template #icon>
          <image class="icon" src="@/assets/icons/editor.png"></image>
        </template>
        <template #desc>
          <textarea
            v-model="order.remark"
            style="height: 100rpx"
            placeholder="请输入备注"
          ></textarea>
        </template>
        <template #link>
          <div></div>
        </template>
      </nut-cell>
    </div>
    <div class="product-list">
      <product-row
        v-for="item in checkedItems"
        :key="item.product.id"
        :product="item.product"
      >
        <template #operation>
          <div class="sku-count">x{{ item.count }}</div>
        </template>
      </product-row>
    </div>
    <nut-cell-group class="summary">
      <nut-cell title="商品总价">
        <template #desc>
          <div class="value">￥{{ paymentPrice.productAmount }}</div>
        </template>
      </nut-cell>
      <nut-cell title="运费">
        <template #desc>
          <div class="value">￥{{ paymentPrice.deliveryFee }}</div>
        </template>
      </nut-cell>
      <nut-cell title="会员优惠">
        <template #desc>
          <div class="value">-￥{{ paymentPrice.vipAmount }}</div>
        </template>
      </nut-cell>
    </nut-cell-group>
    <div class="submit-bar-wrapper">
      <div class="submit-bar">
        <div class="price">￥{{ paymentPrice.payAmount }}</div>
        <nut-button type="primary" @click="saveOrder">提交订单</nut-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Taro from "@tarojs/taro";
import { ref } from "vue";
import { CartItem } from "@/pages/cart/cart-store";
import { AddressDto } from "@/apis/__generated/model/dto";
import { api } from "@/utils/api-instance";
import type {
  PaymentPriceView,
  ProductOrderInput,
} from "@/apis/__generated/model/static";
import { handleProductWeChatPay } from "@/pages/order/order-store";
import { switchPage } from "@/utils/common";
import { RectRight } from "@nutui/icons-vue-taro";

const checkedItems = ref<CartItem[]>([]);
Taro.eventCenter.on("cart-submit", async (items: CartItem[]) => {
  console.log("商品提交", items);
  checkedItems.value = items;
  const res = await api.addressForFrontController.query({
    body: { query: { top: true } },
  });
  address.value = res.content[0];
  order.value.addressId = res.content[0].id;
  calculate();
});
const address =
  ref<AddressDto["AddressRepository/COMPLEX_FETCHER_FOR_FRONT"]>();
Taro.eventCenter.on(
  "address",
  (value: AddressDto["AddressRepository/COMPLEX_FETCHER_FOR_FRONT"]) => {
    address.value = value;
    order.value.addressId = value.id;
    calculate();
  },
);

const paymentPrice = ref<PaymentPriceView>({
  couponAmount: 0,
  deliveryFee: 0,
  payAmount: 0,
  productAmount: 0,
  vipAmount: 0,
});
const order = ref<ProductOrderInput>({
  items: [],
  remark: "",
  addressId: "",
});

const calculate = () => {
  if (checkedItems.value.length === 0) return;
  order.value.items = checkedItems.value.map((item) => ({
    productId: item.product.id,
    productCount: item.count,
  }));
  api.productOrderForFrontController
    .calculate({ body: order.value })
    .then((res) => {
      paymentPrice.value = res;
    });
};

const saveOrder = async () => {
  Taro.showLoading();
  const res = await api.productOrderForFrontController.create({
    body: order.value,
  });
  await handleProductWeChatPay(res);
  Taro.hideLoading();
  Taro.navigateTo({ url: "./order-list" });
};
</script>

<style lang="scss">
page {
  background-color: rgba(black, 0.05);
}

.order-submit {
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

  .product-list {
    background-color: white;
    padding: 32px;
    border-radius: 12px;
    margin-bottom: 30px;

    .sku-count {
      font-size: 28px;
    }
  }

  .summary {
    border-top-left-radius: 30px;
    border-top-right-radius: 30px;

    .value {
      color: rgba(black, 0.9);
      font-weight: bold;
    }
  }

  .submit-bar-wrapper {
    display: flex;
    justify-content: center;
    position: fixed;
    bottom: 0;
    width: 100%;
    background-color: white;

    .submit-bar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20px;
      width: 700px;

      .price {
        font-size: 34px;
        color: red;
      }
    }
  }
}
</style>
