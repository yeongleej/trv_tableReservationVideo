<template>
    <v-sheet tile height="55" class="d-flex">
        <v-select
            v-model="type"
            :items="types"
            dense
            variant="outlined"
            hide-details
            class="ma-2"
            label="보기 방식선택"></v-select>
    </v-sheet>
    <div style="display: flex; width: 100%">
        <v-sheet>
            <v-calendar
                ref="calendar"
                v-model="value"
                :weekdays="weekday"
                :view-mode="type"
                :events="events"
                style="width: 45rem"
                @change="onCalendarDateChange"></v-calendar>
        </v-sheet>
        <div style="flex-grow: 1; padding: 16px; margin-left: 10px">
            <h3>선택한 날짜의 예약 및 웨이팅 리스트</h3>
            <ul>
                <li v-for="reservation in ReservationList" :key="reservation.Id">
                    예약일 : {{ reservation.reservationDate }} 예약시간 :
                    {{ reservation.reservationTime }} 예약테이블 :
                    {{ reservation.diningTableNumber }} 예약인원{{
                        reservation.reservationUserCount
                    }}
                    예약자 : {{ reservation.nickname }} 예약상태: {{ reservation.status }}
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useDate } from 'vuetify';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();
const type = ref('month');
const types = ['month', 'week', 'day'];
const weekday = [0, 1, 2, 3, 4, 5, 6];
const value = ref([new Date()]);
const events = ref([]);
const selectedDateReservations = ref([]);
const accessToken = localStorage.getItem('accessToken');
const titles = [''];

const ReservationList = ref([]);

const onCalendarDateChange = (value) => {
    getReservationListByDate(value[0]);
};

const getReservationListByDate = function (selectedDate) {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/reservation/search/store/${
            store.storeId
        }?date=${selectedDate.toISOString()}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log(res.data);
            selectedDateReservations.value = res.data;
        })
        .catch((err) => {
            console.log(err);
        });
};

const getReservationList = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/reservation/search/store/${store.storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log(res.data);
            ReservationList.value = res.data;
        })
        .catch((err) => {
            console.log(err);
        });
};

onMounted(() => {
    getReservationList();
});
</script>
