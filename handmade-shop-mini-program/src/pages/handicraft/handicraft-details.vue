<script setup lang="ts">
import { computed, ref } from "vue";
import dayjs, { Dayjs } from "dayjs";
import { usePageHelper } from "@/utils/page";
import { api } from "@/utils/api-instance";
import {
  HandicraftBookingDto,
  HandicraftDto,
} from "@/apis/__generated/model/dto";
import Taro from "@tarojs/taro";
type Handicraft =
  HandicraftDto["HandicraftRepository/COMPLEX_FETCHER_FOR_FRONT"];
type HandicraftBooking =
  HandicraftBookingDto["HandicraftBookingRepository/COMPLEX_FETCHER_FOR_FRONT"];
const craft = ref<Handicraft>();
const dates = ref<Dayjs[]>([dayjs(), dayjs().add(1, "d"), dayjs().add(2, "d")]);
const activeDate = ref<Dayjs>(dates.value[0]);
Taro.useLoad((ops) => {
  api.handicraftForFrontController.findById({ id: ops.id }).then((res) => {
    craft.value = res;
    loadData();
  });
});
const handleDateChange = (date: Dayjs) => {
  activeDate.value = date;
  loadData();
};
const { pageData, reloadPageData } = usePageHelper(
  api.handicraftBookingForFrontController.query,
  api.handicraftBookingForFrontController,
  { pageSize: 1000, query: {}, pageNum: 1 },
  { enableLoad: false },
);
const toDate = (time: string, date?: string) => {
  date = date ? date : dayjs().format("YYYY-MM-DD");
  return dayjs(`${date} ${time}`);
};
const bookings = computed(() => {
  const rows = pageData.value.content.map((row) => row);
  rows.sort((a, b) => {
    if (toDate(a.startTime).isAfter(toDate(b.startTime))) {
      return 1;
    }
    return -1;
  });
  return rows.filter((row) => {
    if (
      row.locked ||
      row.handicraft.locked ||
      row.peopleLimit <= (row.bookingCount || 0)
    ) {
      return false;
    }
    let endTime = toDate(row.endTime, row.date);
    if (row.endTime == "00:00:00") {
      endTime = endTime.add(1, "d");
    }
    return endTime.isAfter(dayjs());
  });
});
const activeBookings = ref<HandicraftBooking[]>([]);
const handleBookingClick = (booking: HandicraftBooking) => {
  const index = activeBookings.value.findIndex((row) => row.id == booking.id);
  if (index >= 0) {
    activeBookings.value.splice(index, 1);
  } else {
    activeBookings.value.push(booking);
  }
};
const loadData = () => {
  if (!craft.value) return;
  reloadPageData({
    pageNum: 1,
    pageSize: 1000,
    query: {
      minDate: activeDate.value.format("YYYY-MM-DD"),
      maxDate: activeDate.value.format("YYYY-MM-DD"),
      handicraftId: craft.value.id,
    },
  });
};

const handleSubmit = () => {
  Taro.navigateTo({
    url: "./handicraft-order-create",
    success() {
      Taro.eventCenter.trigger("bookings", activeBookings.value);
    },
  });
};
</script>

<template>
  <div class="handicraft-booking" v-if="craft">
    <div class="swiper-wrapper">
      <nut-swiper class="swiper">
        <nut-swiper-item v-if="craft.video && craft.video.length > 0">
          <video
            :src="craft.video[0].url"
            style="height: 100%; width: 100%"
          ></video>
        </nut-swiper-item>
        <nut-swiper-item
          v-for="(picture, index) in craft.primaryPictures"
          :key="index"
        >
          <image
            :src="picture.url"
            alt=""
            style="height: 100%; width: 100%"
            mode="aspectFill"
            draggable="false"
          />
        </nut-swiper-item>
      </nut-swiper>
    </div>
    <div class="dates">
      <div
        :class="['date-wrapper', activeDate == date ? 'active' : '']"
        v-for="date in dates"
        @click="handleDateChange(date)"
        :key="date.get('d')"
      >
        <div class="date">{{ date.format("MM.DD") }}</div>
        <div class="day">{{ date.format("ddd") }}</div>
      </div>
    </div>

    <div class="bookings-wrapper">
      <div class="bookings">
        <div
          :class="[
            'booking-wrapper',
            activeBookings.findIndex((row) => row.id == booking.id) >= 0
              ? 'active'
              : '',
          ]"
          v-for="booking in bookings"
          :key="booking.id"
          @click="handleBookingClick(booking)"
        >
          <div class="booking-price">￥{{ booking.price }}/场</div>
          <div class="booking-time">
            {{ booking.startTime.substring(0, 5) }}-{{
              booking.endTime.substring(0, 5)
            }}
          </div>
        </div>
      </div>
      <div class="empty"></div>
    </div>

    <!--    <scroll-view :scroll-y="true" class="bookings-scroll"></scroll-view>-->
    <div class="result">
      <div class="total">共 {{ activeBookings.length }} 场</div>
      <div class="submit">
        <nut-button type="primary" @click="handleSubmit">立即预约</nut-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@import "../../app.scss";
.handicraft-booking {
  background-color: rgba(black, 0.05);
  display: flex;
  flex-direction: column;
  height: 100vh;
  .swiper-wrapper {
    .swiper {
      height: 450px;
      width: 750px;
    }
  }

  .dates {
    background-color: white;
    display: flex;
    align-items: center;
    padding: 30px;
    .date-wrapper {
      font-size: 28px;
      padding: 30px;
      border-radius: 8px;
      margin-right: 20px;
      &.active {
        color: white;
        background: linear-gradient(
          315deg,
          $primary-color 0%,
          $primary-color-end 100%
        );
      }
    }
  }
  .bookings-wrapper {
    overflow: scroll;
    .empty {
      height: 180px;
    }
    .bookings {
      display: grid;
      grid: auto-flow/1fr 1fr 1fr;
      gap: 10px;
      padding: 30px;

      .booking-wrapper {
        background-color: white;
        display: flex;
        flex-direction: column;
        align-items: center;
        font-size: 26px;
        padding: 30px;
        border-radius: 10px;
        .booking-price {
          font-size: 28px;
          margin-bottom: 10px;
        }
        .booking-time {
          font-weight: bold;
        }
        &.active {
          .booking-price {
            color: white;
            font-weight: bold;
          }
          .booking-time {
            color: rgba(white, 0.7);
            font-weight: normal;
          }
          background: linear-gradient(
            315deg,
            $primary-color 0%,
            $primary-color-end 100%
          );
        }
      }
    }
  }

  .result {
    position: fixed;
    bottom: 0;
    width: 100%;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 30px;
    background-color: white;
    box-shadow: 10px 10px 10px rgba(black, 0.1);
  }
}
</style>
