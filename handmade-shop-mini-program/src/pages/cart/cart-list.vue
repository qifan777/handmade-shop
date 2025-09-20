<template>
  <div class="cart-list-page" v-if="cartList.length">
    <div class="cart-content">
      <div class="top-bar">
        <div class="left">
          <nut-checkbox
            :model-value="checkedItems.length > 0"
            :indeterminate="isIndeterminate"
            @click="toggleCart"
          >
            已选：{{ checkedItems.length }}
          </nut-checkbox>
        </div>
        <div class="right" @click="clearCart">
          <Del size="20"></Del>
          <div class="tip">清空购物车</div>
        </div>
      </div>
      <div
        v-for="(item, index) in cartStore.cartList"
        :key="item.product.id"
        class="product-row"
      >
        <nut-checkbox
          v-model="item.checked"
          :label="item.product.id"
        ></nut-checkbox>
        <product-row :product="item.product">
          <template #operation>
            <nut-input-number
              :model-value="item.count"
              :min="0"
              @reduce="minusItem(index)"
              @add="plusItem(index)"
            ></nut-input-number>
          </template>
        </product-row>
      </div>
    </div>
    <div class="cart-bar-wrapper">
      <div class="cart-bar">
        <div class="left">
          <div class="price">￥{{ totalPrice }}</div>
        </div>
        <div class="right" @click="submit">去结算</div>
      </div>
    </div>
  </div>
  <nut-empty v-else></nut-empty>
  <register-popup></register-popup>
</template>

<script lang="ts" setup>
import { storeToRefs } from "pinia";
import { useCartStore } from "./cart-store";
import { Del } from "@nutui/icons-vue-taro";
import ProductRow from "@/components/product/product-row.vue";
import { computed } from "vue";
import Taro from "@tarojs/taro";

const cartStore = useCartStore();
const { checkedItems, totalPrice, cartList } = storeToRefs(cartStore);
const { minusItem, plusItem, clearCart, toggleCart } = cartStore;

// 中间状态
const isIndeterminate = computed(() => {
  return (
    checkedItems.value.length > 0 &&
    checkedItems.value.length < cartStore.cartList.length
  );
});
const submit = () => {
  if (checkedItems.value.length == 0) {
    Taro.showToast({ title: "购物车为空" });
    return;
  }
  Taro.navigateTo({
    url: "/pages/order/order-create",
    success() {
      Taro.eventCenter.trigger("cart-submit", checkedItems.value);
    },
  });
};
</script>

<style lang="scss">
// 多行溢出scss函数
@import "../../app.scss";

.cart-content {
  border-top-left-radius: 20rpx;
  border-top-right-radius: 20rpx;
  padding: 0 30rpx 150rpx 30rpx;

  .top-bar {
    display: flex;
    justify-content: space-between;
    padding: 30rpx 0;
    border-bottom: 1px solid rgba(black, 0.1);

    .left {
      display: flex;
      align-items: center;
    }

    .right {
      display: flex;
      align-items: center;

      .tip {
        margin-left: 5px;
      }
    }
  }

  .product-row {
    display: flex;
    align-items: center;
    margin-top: 20rpx;
    border-bottom: 1px solid rgba(black, 0.05);

    .nut-checkbox {
      margin-right: 0;
    }
  }
}
// 横条父亲的宽度和屏幕宽度一样, 让子元素横条居中.并且将横条的位置固定在页面底部
.cart-bar-wrapper {
  position: fixed;
  z-index: 20;
  bottom: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;

  .cart-bar {
    width: 700rpx;
    display: flex;
    height: 100rpx;

    .left {
      background-color: black;
      width: 500rpx;
      height: 100%;
      border-bottom-left-radius: 60rpx;
      border-top-left-radius: 60rpx;
      display: flex;
      align-items: center;

      .price {
        color: white;
        margin-left: 40rpx;
        font-size: 40rpx;
      }
    }

    .right {
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: $primary-color;
      color: white;
      width: 200rpx;
      height: 100%;
      border-bottom-right-radius: 60rpx;
      border-top-right-radius: 60rpx;
      font-size: 35rpx;
      font-weight: bold;
    }
  }
}
</style>
