<template>
  <div>
    <nut-swiper height="200">
      <nut-swiper-item
        v-for="(slide, index) in slides"
        :key="index"
        style="height: 150px"
      >
        <image
          :src="slide.picture"
          alt=""
          style="height: 100%; width: 100%"
          mode="aspectFill"
          draggable="false"
        />
        <nut-noticebar :text="slide.content" :scrollable="false" />
      </nut-swiper-item>
    </nut-swiper>
    <nut-button @click="switchPage('/pages/handicraft/handicraft-list')"
      >立即预约</nut-button
    >
    <register-popup></register-popup>
  </div>
</template>

<script setup lang="ts">
import { SlideshowDto } from "@/apis/__generated/model/dto";
import { ref } from "vue";
import { api } from "@/utils/api-instance";
import Taro from "@tarojs/taro";
import { switchPage } from "@/utils/common";
import { useHomeStore } from "@/stores/home-store";

const slides = ref<
  SlideshowDto["SlideshowRepository/COMPLEX_FETCHER_FOR_FRONT"][]
>([]);
Taro.useLoad(() => {
  api.slideshowForAdminController
    .query({
      body: {
        pageNum: 1,
        pageSize: 1000,
        query: { valid: true },
        sorts: [{ property: "sort", direction: "ASC" }],
      },
    })
    .then((res) => {
      slides.value = res.content;
    });
});
Taro.showShareMenu({
  withShareTicket: true,
  showShareItems: ["wechatFriends", "wechatMoment"],
});
</script>

<style scoped></style>
