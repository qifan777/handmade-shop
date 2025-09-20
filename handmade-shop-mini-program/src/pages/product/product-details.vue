<script setup lang="ts">
import { ref } from "vue";
import {
  ProductCarriageTemplateDto,
  ProductDto,
} from "@/apis/__generated/model/dto";
import Taro from "@tarojs/taro";
import { api } from "@/utils/api-instance";
import { CartItem, useCartStore } from "@/pages/cart/cart-store";

const product =
  ref<ProductDto["ProductRepository/COMPLEX_FETCHER_FOR_FRONT"]>();
const carriageVisible = ref(false);
const carriage =
  ref<
    ProductCarriageTemplateDto["ProductCarriageTemplateRepository/COMPLEX_FETCHER_FOR_FRONT"]
  >();
Taro.useLoad((ops) => {
  api.productCarriageTemplateForFrontController
    .query({ body: { query: { valid: true } } })
    .then((res) => {
      carriage.value = res.content[0];
    });
  api.productForFrontController.findById({ id: ops.id }).then((res) => {
    product.value = res;
  });
});
const cartStore = useCartStore();
const handleAddProduct = () => {
  if (product.value) {
    cartStore.pushItem({ count: 1, product: product.value, checked: true });
  }
};
const submit = (
  product: ProductDto["ProductRepository/COMPLEX_FETCHER_FOR_FRONT"],
) => {
  Taro.navigateTo({
    url: "/pages/order/order-create",
    success: () => {
      Taro.eventCenter.trigger("cart-submit", [
        { product, count: 1, checked: true },
      ] satisfies CartItem[]);
    },
  });
};
</script>

<template>
  <div v-if="product" class="product-details">
    <div class="top">
      <image
        class="product-cover"
        :src="product.cover"
        mode="aspectFill"
      ></image>
      <div class="price-sell">
        <product-price :font-size="40" :price="product.price"></product-price>
        <div class="sell">销量：{{ product.sell }}</div>
      </div>
      <div class="title">
        {{ product.name }}
      </div>
    </div>
    <div class="rows">
      <div class="row">
        <div class="label">参数:</div>
        <div class="value">
          <scroll-view class="spec-scroll" :scroll-x="true">
            <div class="cell-list">
              <div
                class="cell"
                v-for="spec in product.specifications"
                :key="spec.name"
              >
                <div class="top">{{ spec.name }}</div>
                <div class="bottom">{{ spec.values.join(",") }}</div>
              </div>
            </div>
          </scroll-view>
        </div>
      </div>
      <div class="row" @click="carriageVisible = true">
        <div class="label">运费：</div>
        <div class="value">{{ carriage?.name }}</div>
      </div>
    </div>
    <div class="details">
      <div class="sub-title">商品详情</div>
      <div class="description">{{ product.description }}</div>
      <div class="images" v-if="product.pictures">
        <image
          v-for="image in product.pictures"
          :src="image.url"
          :key="image.url"
          mode="widthFix"
        ></image>
      </div>
    </div>
    <div class="bottom-wrapper">
      <div class="bottom">
        <div class="icons">
          <cart-icon></cart-icon>
        </div>
        <div class="buttons">
          <nut-button
            class="cart-button"
            type="warning"
            @click="handleAddProduct"
            >加入购物车</nut-button
          >
          <nut-button class="button" type="primary" @click="submit(product)"
            >立即购买</nut-button
          >
        </div>
      </div>
    </div>
    <nut-popup v-model:visible="carriageVisible" position="bottom">
      <div class="carriage">
        <div class="carriage-title">{{ carriage?.name }}</div>
        <div class="description">{{ carriage?.description }}</div>
      </div>
    </nut-popup>
  </div>
  <nut-empty v-else></nut-empty>
</template>

<style lang="scss">
@import "../../app.scss";
page {
  background-color: #f5f5f5;
}
.product-details {
  .top {
    background-color: white;
  }
  .product-cover {
    width: 100%;
    height: 700px;
  }
  .price-sell {
    padding: 30px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .sell {
      color: rgba(black, 0.5);
      font-size: 28px;
    }
  }
  .title {
    padding: 30px;
    @include text-max-line(2);
    font-size: 34px;
    font-weight: bold;
  }
  .rows {
    margin-top: 20px;
    background-color: white;
    padding: 30px;
    .row {
      display: flex;
      align-items: center;
      padding: 10px 0;
      .label {
        color: #999;
        width: 110px;
      }
      .value {
        .spec-scroll {
          width: 100%;
          .cell-list {
            display: flex;
            align-items: center;
            .cell {
              flex-shrink: 0;
              display: flex;
              flex-direction: column;
              align-items: center;
              padding: 10px 20px;
              border-right: 1px solid rgba(white, 0.05);
              .top {
                margin-bottom: 8px;
              }
              .bottom {
                text-align: center;
                color: #333;
              }
            }
          }
        }
      }
    }
  }
  .details {
    margin-top: 20px;
    background-color: white;
    padding-bottom: 130px;
    .sub-title {
      padding: 30px;
      font-size: 36px;
    }
    .description {
      padding: 0 30px 30px 30px;
    }
    .images {
      display: flex;
      flex-direction: column;
      image {
        width: 100%;
      }
    }
  }
  .bottom-wrapper {
    border-top: 1px solid rgba(black, 0.1);
    padding: 0 30px 20px 30px;
    height: 130px;
    display: flex;
    align-items: center;
    background-color: white;
    position: fixed;
    bottom: 0;
    width: 100%;
    box-sizing: border-box;
    .bottom {
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex: 1;
      .icons {
        image {
          width: 60px;
          height: 60px;
        }
      }
      .buttons {
        .cart-button {
          margin-right: 20px;
        }
      }
    }
  }
  .carriage {
    background-color: white;
    padding: 50px 30px;
    .carriage-title {
      font-size: 36px;
    }
  }
}
</style>
