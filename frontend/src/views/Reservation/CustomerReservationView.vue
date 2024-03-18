<template>
    <div class="container">
        <div class="reservation-card">
            <v-btn-toggle v-model="selectedTab">
                <v-btn @click="toggleTab('waiting')" class="btn3">
                    <div style="color: white">당일 웨이팅 리스트</div>
                </v-btn>
                <v-btn @click="toggleTab('scheduled')" class="btn1">
                    <div style="color: white">현재 예약한 가게 목록</div>
                </v-btn>
                <v-btn @click="toggleTab('completed')" class="btn2">
                    <div style="color: white">다녀온 가게 목록</div>
                </v-btn>
            </v-btn-toggle>
            <v-list v-if="selectedTab === 'waiting'" class="list">
                <v-list-item v-for="(store, index) in waitList" :key="index">
                    <v-list-item-content
                        style="display: flex; align-items: center; flex-wrap: wrap">
                        <img
                            :src="store.storeImage"
                            style="width: 100px; height: 85px; margin-right: 20px"
                            alt="#" />
                        <v-list-item-title>{{ store.storeName }}</v-list-item-title>
                        <div style="margin-left: 30px">
                            웨이팅 번호: {{ store.waitingUserOrder }}
                            <div>인원 : {{ store.waitingUserCount }}명</div>
                            <div>웨이팅 신청 시간: {{ store.registeredTime }}</div>
                        </div>
                        <div v-if="!store.waitingIsPostpone">
                            <v-btn
                                v-if="postponFlag"
                                @click="delayNumOpen"
                                color="grey"
                                style="margin-left: 6rem">
                                웨이팅 미루기
                            </v-btn>
                            <div v-else>
                                <v-btn @click="decreaseWait">-</v-btn>
                                {{ delayNum }}
                                <v-btn @click="increaseWait">+</v-btn>
                                <v-btn @click="postWaiting(index)">확인</v-btn>
                            </div>
                        </div>
                        <v-btn @click="cancleWaiting(index)" color="red" style="margin-left: 1rem">
                            웨이팅 취소
                        </v-btn>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
            <v-list v-if="selectedTab === 'scheduled'" class="list">
                <v-list-item v-for="(store, index) in filteredScheduledList" :key="index">
                    <v-list-item-content style="display: flex; align-items: center">
                        <img
                            :src="store.storeImage"
                            style="width: 100px; height: 85px; margin-right: 20px"
                            alt="#" />
                        <v-list-item-title>음식점 : {{ store.storeName }}</v-list-item-title>
                        <div style="text-align: start; margin-left: 30px">
                            예약 일: {{ store.reservationDate }}
                            <div>예약 시간 : {{ store.reservationTime }}</div>
                            <div>예약 인원 : {{ store.reservationUserCount }}명</div>
                        </div>
                        <v-spacer></v-spacer>
                        <v-list-item-action>
                            <v-btn
                                @click="cancelReservation(index)"
                                color="red"
                                class="reservation-btn"
                                style="margin-left: auto">
                                예약취소
                            </v-btn>
                        </v-list-item-action>
                    </v-list-item-content>
                </v-list-item>
            </v-list>

            <v-list v-if="selectedTab === 'completed'" class="list">
                <v-list-item v-for="(store, index) in filteredCompletedList" :key="index">
                    <v-list-item-content style="display: flex; align-items: center">
                        <img
                            :src="store.storeImage"
                            style="width: 100px; height: 85px; margin-right: 20px"
                            alt="#" />
                        <v-list-item-title>음식점 : {{ store.storeName }}</v-list-item-title>
                        <div style="margin-left: 50px">
                            예약 일 : {{ store.reservationDate }}
                            <div>예약 인원 : {{ store.reservationUserCount }}명</div>
                            <div>예약 시간 : {{ store.reservationTime }}</div>
                        </div>
                        <v-spacer></v-spacer>
                        <div v-if="store.status === 'CANCEL'">예약 결과 : {{ store.status }}</div>
                        <v-list-item-action>
                            <v-spacer></v-spacer>
                            <v-btn
                                v-if="store.status === 'INACTIVE'"
                                @click="writeReview(index)"
                                color="green"
                                class="reservation-btn">
                                리뷰작성
                            </v-btn>
                        </v-list-item-action>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import router from '@/router';

const selectedTab = ref('scheduled');
const waitList = ref([]);
const store = useCounterStore();
const accessToken = localStorage.getItem('accessToken');
const reservationList = ref(null);
const reservationEnd = ref(false);
const delayNum = ref(0);
const postponFlag = ref(true);

const delayNumOpen = () => {
    postponFlag.value = false;
};
const filteredScheduledList = computed(() => {
    if (reservationList.value) {
        return reservationList.value.filter((store) => store.status === 'ACTIVE');
    } else {
        return [];
    }
});

const filteredCompletedList = computed(() => {
    if (reservationList.value) {
        return reservationList.value.filter(
            (store) => store.status === 'INACTIVE' || store.status === 'CANCEL'
        );
    } else {
        return [];
    }
});
const decreaseWait = () => {
    // 인원 수 감소
    if (delayNum.value > 0) {
        delayNum.value--;
    }
};

const increaseWait = () => {
    // 인원 수 증가
    delayNum.value++;
};

// 웨이팅리스트 가져오기
const getWaitList = () => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/user/waiting/list`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            waitList.value = res.data;
            console.log(waitList.value);
            console.log(waitList.value[0].storeName);
            console.log('웨이팅 정보를 가져왔습니다.');
        })
        .catch((err) => {
            console.log('웨이팅 정보 가져오기 실패', err);
        });
};

// 예약 정보 가져오기
const getReservationList = () => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/reservation/search`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            reservationList.value = res.data;
            console.log(reservationList.value);
            console.log(reservationList.value[0]);
            console.log('예약 정보를 가져왔습니다.');
        })
        .catch((err) => {
            console.log('예약 정보 가져오기 실패', err);
        });
};
const storeInfo = ref([]);
const loadStoreData = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${reservationList.storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log(res.data);
            storeInfo.value = res.data;
        })
        .catch((err) => {
            console.log('Data를 불러오지 못했습니다.', err);
            console.log('Response:', err.response);
        });
};

onMounted(() => {
    getWaitList();
    getReservationList();
    loadStoreData();
});

// 예약 몇칸 미룰지 정하는 변수
const postponeCount = ref(0);

const postWaiting = (index) => {
    // 웨이팅 미루기 로직 구현
    const storeId = waitList.value[index].storeId;
    postponeCount.value = delayNum.value;
    axios({
        method: 'put',
        url: `${store.API_URL}/api/store/${storeId}/waiting/${postponeCount.value}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        // data: {
        //     waitingUserCount: '',
        //     waitingSequence: '',
        //     waitingIsPostpone: '',
        // },
    })
        .then((res) => {
            delayNum.value = 0;
            postponFlag.value = true;
            getWaitList();
            console.log('웨이팅 미루기가 성공했습니다.');
        })
        .catch((err) => console.log('웨이팅 미루기가 실패했습니다.', err));
};

const cancleWaiting = (index) => {
    // 웨이팅 취소 로직 구현
    const storeId = waitList.value[index].storeId;
    console.log(storeId);
    axios({
        method: 'delete',
        url: `${store.API_URL}/api/store/${storeId}/waiting`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log('웨이팅 취소가 성공했습니다.');
            waitList.value.splice(index, 1);
        })
        .catch((err) => console.log('웨이팅 취소가 실패했습니다.', err));
};

const toggleTab = (tab) => {
    selectedTab.value = tab;
};

const cancelReservation = (index) => {
    // 예약 취소 로직 구현
    const reservationId = filteredScheduledList.value[index].reservationId;
    // console.log(storeId);
    console.log(reservationId);
    axios({
        method: 'delete',
        url: `${store.API_URL}/api/reservation/${reservationId}/cancle`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            alert('예약이 취소되었습니다.');
            reservationList.value.splice(index, 1);
        })
        .catch((err) => {
            console.log(reservationList.value);
            console.log(reservationList.value.splice(index, 1));
            console.log('예약 취소가 실패했습니다.', err);
        });
};

const writeReview = (index) => {
    // 리뷰 작성 로직 구현
    completedStores.value.splice(index, 1);
};
</script>

<style scoped>
.container {
    display: flex;
    justify-content: center;
    width: 100%;
    min-height: 80vh;
}

.reservation-card {
    max-width: 1024px;
    width: 100%;
    min-height: 30%;
    background-color: #ffffff;
    border-radius: 20px;
    box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.1);
    padding: 20px;
    margin: 20px;
}

.v-btn-toggle {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}

.btn1,
.btn3,
.btn2 {
    flex: 1;
    font-size: 16px;
    margin: 0 10px;
    border: none;
    color: #ffffff;
    border-radius: 20px;
    transition: background-color 0.3s;
}

.btn1 {
    background-color: #3498db;
}

.btn2 {
    background-color: #2ecc71;
}
.btn3 {
    background-color: burlywood;
}
.btn1:hover,
.btn2:hover {
    opacity: 0.9;
}

.list {
    margin-top: 20px;
    align-items: center;
    line-height: 20px;
}
.v-list-item {
    margin-bottom: 10px;
    border-bottom: 1px solid #f2f2f2;
    padding: 10px 0;
}

.v-btn {
    border-radius: 20px;
    font-size: 14px;
    padding: 10px 20px;
}
.custom-list-item-content {
    flex: 1;
    display: flex;
}
</style>
