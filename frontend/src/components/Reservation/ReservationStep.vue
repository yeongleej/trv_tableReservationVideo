<template>
    <div style="margin-left: 50px; margin-top: 30px">
        <v-stepper
            prev-text="다시선택하기"
            next-text="예약요청하기"
            :items="['날짜 및 시간 선택', '예약요청확인']">
            <template v-slot:item.1>
                <v-container style="display: flex">
                    <!-- 달력과 시간 왼쪽에 위치 -->
                    <v-col cols="4">
                        <Calendar v-model="selectedDate" :store-id="storeId"></Calendar>
                    </v-col>

                    <!-- 달력과 시간 오른쪽에 테이블 선택 -->
                    <v-col cols="8">
                        <SelectTime v-model="selectedTimes" :store-id="storeId"></SelectTime>
                        <!-- <SelectTable v-model="selectedTable" :store-id="storeId"></SelectTable> -->
                    </v-col>
                </v-container>
            </template>

            <template v-slot:item.2>
                <v-card title="예약 내용 확인" flat>
                    <div class="reservation">
                        <li>음식점 이름 : {{ storeName }}</li>
                        <li>예약 일 : {{ store.reqDate }}</li>
                        <li>예약 시간 : {{ store.selectedTime }}</li>
                        <li>
                            테이블 번호 :
                            {{ store.selectedTable.diningTableNumber }} , "{{
                                store.selectedTable.diningTableType
                            }}"
                        </li>
                        <li>예약 인원 : {{ store.reservationUserCount }}</li>
                        <v-btn @click="requestReservation">예약요청</v-btn>
                    </div>
                </v-card>
            </template>
        </v-stepper>
    </div>
</template>

<script setup>
import Calendar from '@/components/Reservation/Calendar.vue';
import SelectTime from '@/components/Reservation/SelectTime.vue';
import SelectTable from '@/components/Reservation/SelectTable.vue';
import { ref, computed } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import router from '@/router';
const props = defineProps({
    storeId: String,
    storeName: String,
});

const store = useCounterStore();
const selectedDate = ref(null);
const selectedTimes = ref([]);
const selectedTable = ref(null);
const accessToken = localStorage.getItem('accessToken');

const currentStep = ref(0); // 현재 스텝 추적
const reservationResultMessage = ref('');

const requestReservation = function () {
    if (currentStep.value == 1) {
        // '예약요청결과' 단계가 아니면 함수를 종료
        return;
    }
    const payload = {
        storeId: props.storeId,
        diningTableNumber: store.selectedTable.diningTableNumber,
        reservationDate: store.reqDate,
        reservationTime: store.selectedTime,
        reservationUserCount: store.reservationUserCount,
        zpassId: store.zPass,
    };
    axios({
        method: 'post',
        url: `${store.API_URL}/api/reservation/regist`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        data: payload,
    })
        .then((res) => {
            console.log(res.data);
            console.log('예약이 성공적으로 되었습니다.');
            // 성공 시에는 알림창 띄우기
            const confirmation = confirm(
                '예약이 성공적으로 되었습니다. 예약 내역을 확인하시겠습니까?'
            );
            if (confirmation) {
                router.push('/customerreservation');
                // 예를 들어, router.push('/reservation-history') 등을 사용할 수 있습니다.
            }
        })
        .catch((err) => {
            console.log('예약실패');
            console.log(payload);
            console.log(err);
            // 실패 시에는 알림창 띄우기
            alert('예약이 실패하였습니다.');
        });
};
const formattedDate = computed(() => {
    if (selectedDate.value) {
        const options = {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
        };
        return new Date(selectedDate.value).toLocaleDateString('ko-KR', options);
    }
    return '';
});
</script>

<style scoped>
/* Add your styling here */
.reservation {
    border: 1px solid gainsboro;
    border-radius: 10px;
    padding: 15px;
    margin-left: 20px;
    font-size: large;
    font-weight: 600;
}
li {
    margin: 5px;
}
</style>
