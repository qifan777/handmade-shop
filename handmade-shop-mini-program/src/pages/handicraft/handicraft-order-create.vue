<template>
  <div class="order-submit">
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
    <scroll-view class="product-list" :scroll-y="true">
      <div
        class="date-bookings"
        v-for="date in Object.keys(dateBookings)"
        :key="date"
      >
        <div class="date">{{ date.substring(5, 10) }}</div>
        <div class="bookings">
          <div
            class="booking"
            v-for="booking in dateBookings[date]"
            :key="booking.date + booking.startTime"
          >
            <div class="time">
              {{ booking.startTime.substring(0, 5) }} -
              {{ booking.endTime.substring(0, 5) }}
            </div>
            <div class="price">￥ {{ booking.price }}</div>
          </div>
        </div>
      </div>
    </scroll-view>
    <nut-cell-group class="summary">
      <nut-cell title="总时长">
        <template #desc>
          <div class="value">{{ hours }}</div>
        </template>
      </nut-cell>
    </nut-cell-group>
    <div class="submit-bar-wrapper" v-if="calculateView">
      <div class="submit-bar">
        <div class="price">
          ￥ {{ calculateView.paymentPriceView.payAmount }}
        </div>
        <nut-button type="primary" @click="saveOrder">提交订单</nut-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Taro from "@tarojs/taro";
import { computed, ref } from "vue";
import { HandicraftBookingDto } from "@/apis/__generated/model/dto";
import { api } from "@/utils/api-instance";
import type {
  HandicraftBookingView,
  HandicraftOrderInput,
  HandicraftOrderService_CalculateView,
} from "@/apis/__generated/model/static";
import { handleHandicraftWeChatPay } from "@/pages/order/order-store";
import dayjs from "dayjs";

type HandicraftBooking =
  HandicraftBookingDto["HandicraftBookingRepository/COMPLEX_FETCHER_FOR_FRONT"];
const bookings = ref<HandicraftBooking[]>([]);
const toDate = (time: string, date?: string) => {
  date = date ? date : dayjs().format("YYYY-MM-DD");
  return dayjs(`${date} ${time}`);
};
const hours = computed(() => {
  if (!calculateView.value) return "";
  const minutes = calculateView.value.bookingViews
    .map((item) => item.minutes)
    .reduce((previousValue, currentValue) => previousValue + currentValue, 0);
  return (minutes / 60).toFixed(1) + " 小时";
});
const dateBookings = computed(() => {
  const map: Record<string, HandicraftBookingView[]> = {};
  if (!calculateView.value) return map;
  const bookingViews = [...calculateView.value.bookingViews];
  bookingViews.sort((a, b) => {
    return toDate(a.date, a.startTime).isAfter(toDate(b.date, b.startTime))
      ? 1
      : -1;
  });
  bookingViews.forEach((item) => {
    if (map[item.date]) {
      map[item.date].push(item);
    } else {
      map[item.date] = [item];
    }
  });
  return map;
});

Taro.eventCenter.on("bookings", (values: HandicraftBooking[]) => {
  bookings.value = values;
  calculate();
});

const calculateView = ref<HandicraftOrderService_CalculateView>();
const order = ref<HandicraftOrderInput>({
  items: [],
  handicraftId: "",
});
const calculate = () => {
  if (bookings.value.length === 0) return;
  order.value.items = bookings.value.map((item) => ({
    handicraftBookingId: item.id,
  }));
  order.value.handicraftId = bookings.value[0].handicraftId;

  api.handicraftOrderForFrontController
    .calculate({ body: order.value })
    .then((res) => {
      calculateView.value = res;
    });
};
const saveOrder = async () => {
  Taro.showLoading();
  const res = await api.handicraftOrderForFrontController.create({
    body: order.value,
  });
  await handleHandicraftWeChatPay(res);
  Taro.hideLoading();
  Taro.navigateTo({ url: "./handicraft-order-list" });
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
    box-sizing: border-box;
    background-color: white;
    padding: 32px;
    border-radius: 12px;
    margin-bottom: 30px;
    height: 450px;

    .date-bookings {
      .date {
        margin: 20px 0;
        font-size: 32px;
        color: rgba(black, 0.8);
      }

      .bookings {
        background-color: rgba(black, 0.05);
        padding: 20px;
        border-radius: 10px;

        .booking {
          font-size: 28px;
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
      }
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
