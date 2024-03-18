<template>
    <v-container>
        <div class="flex-container">
            <v-select
                v-model="multiple"
                :items="items"
                label="원하는 수의 예약가능한 일자를 선택하세요"
                style="width: 400px"
                @change="handleMultipleChange"></v-select>
            <button @click="openModal" style="margin-left: 20px">저장하기</button>
        </div>
        <v-row justify="center">
            <v-date-picker
                v-model="selectedDate"
                width="500"
                :elevation="5"
                :allowed-dates="allowedDates"
                :multiple="multiple"
                :range="true"
                @day-content="dayContent"
                @click:date="handleDateClick"
                @input="resetSelectedDates"></v-date-picker>
        </v-row>
        <v-dialog v-model="modal" max-width="400">
            <v-card>
                <v-card-title>예약 시간 설정 저장</v-card-title>
                <v-card-text> 예약 시간 설정을 저장하시겠습니까? </v-card-text>
                <v-card-actions>
                    <v-btn @click="closeModal">취소</v-btn>
                    <v-btn @click="saveSelectTimeAndCloseModal">확인</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
    <div class="time-selection">
        <h3>예약시간 설정</h3>
        <div class="time-row" v-for="row in timeRows" :key="row[0]">
            <div v-for="time in row" :key="time" @click="selectTime(time)">
                <label class="checkbox-label">
                    <input type="checkbox" :value="time" v-model="selectedTimes" />
                    {{ time }}
                </label>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import router from '@/router';

const accessToken = localStorage.getItem('accessToken');
const store = useCounterStore();
const multiple = ref('');
const items = ref([1, 3, 5, 7, 10, 14, 30]);
let disabledDay = null;
const selectedDate = ref([]);
const selectedTimes = ref([]);
const availableTimes = ref([
    '00:00',
    '01:00',
    '02:00',
    '03:00',
    '04:00',
    '05:00',
    '06:00',
    '07:00',
    '08:00',
    '09:00',
    '10:00',
    '11:00',
    '12:00',
    '13:00',
    '14:00',
    '15:00',
    '16:00',
    '17:00',
    '18:00',
    '19:00',
    '20:00',
    '21:00',
    '22:00',
    '23:00',
]);

const selectTime = (time) => {
    // You can add logic here to handle the selection if needed
};

const resetSelectedDates = function () {
    // 새로운 날짜를 선택할 때마다 selectedDate 초기화
    selectedDate.value = [];
};

const timeRows = ref(
    Array.from({ length: availableTimes.value.length / 2 }, (_, i) =>
        availableTimes.value.slice(i * 6, i * 6 + 6)
    )
);
const getSelectTime = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${store.storeId}/reservation/info`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log(res.data);
            const currentDate = new Date();

            // 받아온 날짜를 바로 할당
            selectedDate.value = res.data.storeReservationAvailableDates.map(
                (date) => new Date(date)
            );

            // 필요하다면 현재 날짜 이전의 날짜는 필터링할 수 있습니다.
            // selectedDate.value = selectedDate.value.filter((date) => date >= currentDate);

            console.log(selectedDate.value);
            selectedTimes.value = res.data.storeReservationAvailableHours;
        })
        .catch((err) => {
            console.log('정보불러오기에 실패했습니다', err);
        });
};
const saveSelectTime = function () {
    const payload = {
        storeReservationAvailableDates: selectedDate.value,
        storeReservationAvailableHours: selectedTimes.value,
    };
    axios({
        method: 'put',
        url: `${store.API_URL}/api/store/${store.storeId}/reservation/info`,
        data: payload,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log(selectedDate.value);
            console.log(selectedTimes.value);
            console.log('성공적으로 저장되었습니다.');
            alert('저장이 완료되었습니다.');
        })
        .catch((error) => {
            console.error('저장에 실패했습니다.', error);
        });
};
const allowedDates = (date) => {
    const thisDate = new Date(date);
    const clickedDay = thisDate.getDay();
    const currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0);

    if (disabledDay !== null && clickedDay === disabledDay) {
        return false;
    }

    if (thisDate < currentDate) {
        return false;
    }

    if (selectedDate.value.length >= parseInt(multiple.value)) {
        return false;
    }

    return true;
};
const handleMultipleChange = function () {
    // multiple이 변경될 때 selectedDate 초기화
    resetSelectedDates();
};

let dateUpdateInterval = null;
const modal = ref(false);

const openModal = function () {
    modal.value = true;
};

const closeModal = function () {
    modal.value = false;
};

const saveSelectTimeAndCloseModal = function () {
    saveSelectTime();
    closeModal();
};
onMounted(() => {
    dateUpdateInterval = setInterval(() => {
        // Increment each selected date by one day
        selectedDate.value = selectedDate.value.map((date) => {
            const nextDay = new Date(date);
            nextDay.setDate(nextDay.getDate() + 1);
            return nextDay;
        });

        // Call the getSelectTime function to update the dates
        getSelectTime();
    }, 24 * 60 * 60 * 1000);
    getSelectTime();
});

onUnmounted(() => {
    clearInterval(dateUpdateInterval);
});

watch(multiple, () => {
    resetSelectedDates();
});
</script>

<style scoped>
.flex-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 500px;
    margin-left: auto;
    margin-right: auto;
    margin-bottom: 16px; /* Add margin as needed */
}

.v-date-picker-header button {
    color: black;
}

.time-row {
    display: flex;
    gap: 5px;
    padding-top: 5px;
    padding: 5px;
}

.time-selection div {
    flex: 1;
}

.checkbox-label {
    display: flex;
    align-items: center;
}

.checkbox-label input {
    margin-right: 5px;
}
button:hover {
    padding: 4px;
    background-color: bisque;
    transform: scale(1.02);
}
</style>
