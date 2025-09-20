<script setup lang="ts">
import ProductPrice from "@/components/product/product-price.vue";
import { HandicraftOrderDto } from "@/apis/__generated/model/dto";
type Handicraft =
  HandicraftOrderDto["HandicraftOrderRepository/COMPLEX_FETCHER_FOR_FRONT"]["handicraft"];

defineProps<{ handicraft: Handicraft }>();
</script>

<template>
  <div class="handicraft">
    <image :src="handicraft.cover" mode="aspectFill"></image>
    <div class="info">
      <div class="name">{{ handicraft.name }}</div>
      <div class="tag-list" v-if="handicraft.tags">
        <nut-tag
          class="tag"
          type="primary"
          plain
          :key="tag"
          v-for="tag in handicraft.tags"
          >{{ tag }}
        </nut-tag>
      </div>
      <div class="description">
        <text>{{ handicraft.description }}</text>
      </div>
      <product-price :price="handicraft.price" :font-size="32"></product-price>
      <slot></slot>
    </div>
  </div>
</template>

<style lang="scss">
.handicraft {
  display: flex;

  image {
    width: 200px;
    height: 260px;
    border-radius: 15px;
  }

  .info {
    margin-left: 20px;
    flex: 1;

    .name {
      font-size: 32px;
    }

    .tag-list {
      align-items: center;
      display: flex;
      margin-top: 20px;

      .tag {
        margin-right: 10px;
      }
    }

    .description {
      margin: 20px 0;
      color: rgba(black, 0.7);
      font-size: 26px;
    }
  }
}
</style>
