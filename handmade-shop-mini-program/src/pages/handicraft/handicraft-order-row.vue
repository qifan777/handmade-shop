<script setup lang="ts">
import { HandicraftOrderDto } from "@/apis/__generated/model/dto";
import DictColumn from "@/components/dict/dict-column.vue";
import { DictConstants } from "@/apis/__generated/model/enums/DictConstants";
import dayjs from "dayjs";
import { computed, onMounted } from "vue";
import HandicraftRow from "@/pages/handicraft/components/handicraft-row.vue";
import { HandicraftBookingView } from "@/apis/__generated/model/static";

const props = defineProps<{
  order: HandicraftOrderDto["HandicraftOrderRepository/COMPLEX_FETCHER_FOR_FRONT"];
}>();
onMounted(() => {});
const dateBookings = computed(() => {
  const map: Record<string, HandicraftBookingView[]> = {};
  const items = props.order.items.map((row) => row.handicraftBookingView);
  items.sort((a, b) => {
    return dayjs(a.date + " " + a.startTime).isAfter(b.date + " " + b.startTime)
      ? 1
      : -1;
  });
  items.forEach((item) => {
    if (map[item.date]) {
      map[item.date].push(item);
    } else {
      map[item.date] = [item];
    }
  });
  return map;
});
</script>

<template>
  <div class="handicraft-order-row">
    <div class="time-status">
      <div class="time">
        {{ dayjs(order.createdTime).format("YYYY-MM-DD HH:mm") }}
      </div>
      <dict-column
        :dict-id="DictConstants.PRODUCT_ORDER_STATUS"
        :value="order.status"
      ></dict-column>
    </div>
    <handicraft-row :handicraft="order.handicraft"></handicraft-row>
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
    <div class="total">
      合计：
      <product-price :price="order.baseOrder.payment.payAmount"></product-price>
    </div>
    <slot name="bottom"></slot>
    <div class="buttons">
      <slot name="buttons"></slot>
    </div>
  </div>
</template>

<style lang="scss">
.handicraft-order-row {
  padding: 30px;
  background-color: white;
  border-radius: 20px;

  .time-status {
    display: flex;
    font-size: 26px;
    justify-content: space-between;
    color: rgba(black, 0.5);
    padding: 10px 0;
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

  .total {
    font-size: 30px;
    display: flex;
    justify-content: flex-end;
    padding: 15px 10px;
  }

  .buttons {
    display: flex;
    justify-content: flex-end;

    .nut-button {
      margin-left: 20px;
    }
  }
}
</style>
