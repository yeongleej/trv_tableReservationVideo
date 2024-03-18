<template>
    <v-container>
        <v-row justify="center">
            <v-date-picker
                width="500"
                :elevation="5"
                :allowed-dates="allowedDates"
                v-model="selectedDate"
                @click="handleSelection"></v-date-picker>
        </v-row>
    </v-container>
    <!-- <v-btn @click="handleSelection" style="margin-left: 260px">선택하기</v-btn> -->
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
const props = defineProps({
    storeId: String,
});

const store = useCounterStore();
let disabledDay = null;
const selectedDate = ref(null);
const selectedTimes = ref([]);
const availableDates = ref([]); // 수정: 빈 배열로 초기화
const availableTimes = ref([]);
const accessToken = localStorage.getItem('accessToken');

//zpass 유무 플래그
const zpassFlag = ref(store.zPass);
const getReservationDates = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/reservation/search/available/store/${props.storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            const currentDate = new Date();
            // 오늘로부터 7일 후의 날짜 계산
            const futureDate = new Date(currentDate.setDate(currentDate.getDate() + 7));

            availableDates.value = res.data
                // 날짜 문자열을 Date 객체로 변환
                .map((date) => new Date(date))
                // 예약 가능한 날짜 중에서 7일 후의 날짜만 필터링
                .filter((date) => date <= futureDate);

            console.log('예약 가능한 날짜입니다.', availableDates.value);
        })
        .catch((error) => {
            console.error('불러오기에 실패했습니다.', error);
        });
};

const getReservationDatesIfpass = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/reservation/search/available/store/${props.storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            const currentDate = new Date();
            // 오늘로부터 7일 후의 날짜 계산
            const futureDate = new Date(currentDate.setDate(currentDate.getDate() + 14));

            availableDates.value = res.data
                // 날짜 문자열을 Date 객체로 변환
                .map((date) => new Date(date))
                // 예약 가능한 날짜 중에서 7일 후의 날짜만 필터링
                .filter((date) => date <= futureDate);

            console.log('예약 가능한 날짜입니다.', availableDates.value);
        })
        .catch((error) => {
            console.error('불러오기에 실패했습니다.', error);
        });
};

const allowedDates = (date) => {
    const thisDate = new Date(date);
    const currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0);

    if (thisDate < currentDate) {
        return false;
    }

    // 예약 가능한 날짜인 경우
    return availableDates.value.some((availableDate) => {
        return (
            thisDate.getFullYear() === availableDate.getFullYear() &&
            thisDate.getMonth() === availableDate.getMonth() &&
            thisDate.getDate() === availableDate.getDate()
        );
    });
};

let dateUpdateInterval = null;

onMounted(() => {
    dateUpdateInterval = setInterval(() => {
        // Add logic if needed for updating dates
    }, 24 * 60 * 60 * 1000);
    console.log(zpassFlag.value);
    if (zpassFlag.value == null) {
        getReservationDates();
    } else {
        getReservationDatesIfpass();
    }
    store.saveSelectedDate();
});

onUnmounted(() => {
    clearInterval(dateUpdateInterval);
});
// 선택한 date 설정확인, axios로 보내기
const handleSelection = () => {
    if (selectedDate.value) {
        const adjustedDate = new Date(selectedDate.value.getTime() + 9 * 60 * 60 * 1000);
        const selectDate = adjustedDate.toISOString().split('T')[0];
        // console.log('선택한 날짜:', reqDate);
        // Pinia 스토어에 선택한 날짜 저장
        store.saveSelectedDate(selectDate);
    }
};
</script>
<style scoped></style>
