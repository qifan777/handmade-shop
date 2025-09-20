<script setup lang="ts">
import productCategory from "@/components/product/product-category.vue";
import { ref } from "vue";
import { ProductCategoryDto } from "@/apis/__generated/model/dto";
import Taro from "@tarojs/taro";
import { api } from "@/utils/api-instance";
import { useHomeStore } from "@/stores/home-store";
import { useCartStore } from "@/pages/cart/cart-store";
const homeStore = useHomeStore();
const cartStore = useCartStore();
homeStore.getUserInfo();
type ProductCategory =
  ProductCategoryDto["ProductCategoryRepository/COMPLEX_FETCHER_FOR_FRONT"];
const categories = ref<ProductCategory[]>([]);
Taro.useLoad(() => {
  Taro.showShareMenu({
    showShareItems: ["shareAppMessage", "shareTimeline"],
  });
  api.productCategoryForFrontController
    .query({
      body: {
        pageNum: 1,
        pageSize: 100000,
        query: {},
      },
    })
    .then((res) => {
      categories.value = res.content;
    });
});
const handleAddToCart = (product: ProductCategory["products"][0]) => {
  cartStore.pushItem({ product, count: 1, checked: true });
  Taro.showToast({ title: "已添加至购物车" });
};
const handleClick = (product: ProductCategory["products"][0]) => {
  Taro.navigateTo({
    url: "./product-details?id=" + product.id,
  });
};
</script>

<template>
  <div>
    <product-category :categories="categories">
      <template #default="{ product }">
        <product-cover
          :product="product"
          @click="handleClick(product)"
          @add="handleAddToCart(product)"
        ></product-cover>
      </template>
    </product-category>
    <register-popup></register-popup>
  </div>
</template>

<style lang="scss"></style>
