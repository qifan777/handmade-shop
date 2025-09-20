<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { usePageHelper } from "@/utils/page";
import { api } from "@/utils/api-instance";
import { HandicraftDto } from "@/apis/__generated/model/dto";
import Taro from "@tarojs/taro";
import HandicraftRow from "@/pages/handicraft/components/handicraft-row.vue";

type Category = { id: string; name: string };
type Handicraft =
  HandicraftDto["HandicraftRepository/COMPLEX_FETCHER_FOR_FRONT"];

onMounted(() => {});
const { pageData: categoryPageData } = usePageHelper(
  api.handicraftCategoryForFrontController.query,
  api.handicraftCategoryForFrontController,
  {
    pageNum: 1,
    pageSize: 1000,
    query: {},
    sorts: [{ property: "sortOrder", direction: "ASC" }],
  },
);
const activeCategory = ref<{ id: string; name: string }>({
  id: "",
  name: "全部",
});
const categories = computed(() => {
  const rows: Category[] = [{ name: "全部", id: "" }];
  rows.push(
    ...categoryPageData.value.content.map((row) => {
      return { ...row } satisfies Category;
    }),
  );
  return rows;
});
const handleChangeCategory = (category: Category) => {
  activeCategory.value = category;
  reloadPageData({
    pageNum: 1,
    pageSize: 10,
    query: { categoryId: category.id },
  });
};
const { pageData, reloadPageData } = usePageHelper(
  api.handicraftForFrontController.query,
  api.handicraftForFrontController,
  { pageSize: 10, pageNum: 1, query: {} },
);
const handleBooking = (row: Handicraft) => {
  if (row.locked) return;
  Taro.navigateTo({
    url: "./handicraft-details?id=" + row.id,
  });
};
</script>

<template>
  <div class="handcraft-page">
    <scroll-view class="category-scroll" :scroll-x="true" v-if="activeCategory">
      <div class="category-list">
        <div
          :class="[
            'category',
            activeCategory.id == category.id ? 'active' : '',
          ]"
          v-for="category in categories"
          :key="category.id"
          @click="handleChangeCategory(category)"
        >
          {{ category.name }}
        </div>
      </div>
    </scroll-view>
    <div class="handcraft-list">
      <div
        class="handicraft-wrapper"
        v-for="handicraft in pageData.content"
        :key="handicraft.id"
      >
        <handicraft-row :handicraft="handicraft">
          <div class="tip-button">
            <div class="tip">
              <text>{{ handicraft.tip }}</text>
            </div>
            <nut-button
              type="primary"
              class="booking"
              @click="handleBooking(handicraft)"
              >预约
            </nut-button>
          </div>
        </handicraft-row>
        <div class="locked" v-if="handicraft.locked">锁定</div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@import "../../app.scss";

page {
  background-color: rgba(black, 0.05);
}

.handcraft-page {
  .category-scroll {
    padding: 30px 0;
    background-color: white;
    border-top: 1px solid rgba(black, 0.05);
  }

  .category-list {
    white-space: nowrap;

    .category {
      font-size: 28px;
      display: inline-block;
      margin-left: 20px;
      border-radius: 999px;
      padding: 15px 30px;
      border: 1px solid rgba($primary-color, 0.3);
      color: $primary-color;

      &.active {
        background-color: $primary-color;
        color: white;
        font-weight: bold;
      }
    }
  }

  .handcraft-list {
    .handicraft-wrapper {
      margin-top: 20px;

      background-color: white;
      padding: 20px 30px;

      .tip-button {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 40px;

        .tip {
          font-size: 28px;
          color: $primary-color;
          font-weight: bold;
        }

        .booking {
          width: 180px;
        }
      }

      .locked {
        text-align: end;
        padding: 10px 30px 0;
        font-size: 26px;
        color: rgba(black, 0.5);
      }
    }
  }
}
</style>
