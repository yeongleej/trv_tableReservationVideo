<template>
    <div class="container">
        <div v-for="item in userFollowData">
            <div class="flip-card">
                <div class="flip-card-inner">
                    <div class="flip-card-front">
                        <div class="storeItems">
                            <div class="storeImg">
                                <img
                                    style="height: 120px; width: 120px"
                                    class="storeImg"
                                    :src="item.images[0]"
                                    alt="storeimg" />
                                <div style="margin-top: 10px">가게명 : {{ item.storeName }}</div>
                                <div>위치 : {{ item.storeAddress }}</div>
                            </div>
                        </div>
                    </div>
                    <div class="flip-card-back">
                        <div class="buttonGroup">
                            <v-btn
                                style="
                                    height: 20px;
                                    width: 90px;
                                    margin-left: 3px;
                                    margin-right: 3px;
                                "
                                variant="outlined"
                                @click="unfollow(item.storeId)">
                                <v-icon icon="mdi-heart" color="red"></v-icon>구독취소</v-btn
                            >
                            <v-dialog style="width: 98%">
                                <template v-slot:activator="{ props }">
                                    <v-btn
                                        v-bind="props"
                                        style="
                                            height: 20px;
                                            width: 90px;
                                            margin-left: 3px;
                                            margin-right: 3px;
                                        "
                                        variant="outlined"
                                        color="success"
                                        >예약하기</v-btn
                                    >
                                </template>
                                <template v-slot:default="{ isActive }">
                                    <v-card title="Dialog">
                                        <v-card-text>
                                            <ReservationStep
                                                :store-id="item.storeId"
                                                :store-name="item.storeName" />
                                        </v-card-text>
                                        <v-card-actions>
                                            <v-spacer></v-spacer>
                                            <v-btn
                                                text="Close Dialog"
                                                @click="isActive.value = false"></v-btn>
                                        </v-card-actions>
                                    </v-card>
                                </template>
                            </v-dialog>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import { ref, onMounted } from 'vue';
import ReservationStep from './Reservation/ReservationStep.vue';

const accessToken = localStorage.getItem('accessToken');
const store = useCounterStore();
const userFollowData = ref([]);

onMounted(() => {
    getUserData();
});

const getUserData = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/follow/store/search`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            userFollowData.value = res.data;
            console.log(userFollowData.value);
            console.log('구독 정보를 가져왔습니다.');
        })
        .catch((err) => {
            console.log('구독 정보 로딩 실패', err);
        });
};

// follow 취소
const unfollow = (storeId) => {
    axios({
        method: 'delete',
        url: `${store.API_URL}/api/follow/store/${storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log('팔로우 취소 성공');
            getUserData();
        })
        .catch((err) => {
            console.log('팔로우 취소 실패', err);
        });
};
</script>

<style scoped>
.container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    height: 100vh;
}
.storeItems {
    display: flex;
    flex-direction: column;
    /* align-items: center; */
    /* justify-content: center; */
    height: 100%;
    padding: 8px;
}
.storeImg {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 85%;
    flex-wrap: wrap;
    flex-direction: row;
    justify-content: center;
}
.storeInfo {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
}
.buttonGroup {
    display: flex;
    margin-left: auto;
    margin-right: auto;
    justify-content: center;
    align-items: center;
}
.storeCard {
    margin: 8px;
    padding: 16px;
    width: 300px;
}

/* ------------------------------------------------------------------------- */

.flip-card {
    background-color: transparent;
    width: 190px;
    height: 254px;
    perspective: 1000px;
    font-family: sans-serif;
    margin: 15px 60px;
}

.title {
    font-size: 1.5em;
    font-weight: 900;
    text-align: center;
    margin: 0;
}

.flip-card-inner {
    position: relative;
    width: 100%;
    height: 100%;
    text-align: center;
    transition: transform 0.8s;
    transform-style: preserve-3d;
}

.flip-card:hover .flip-card-inner {
    transform: rotateY(180deg);
}

.flip-card-front,
.flip-card-back {
    box-shadow: 0 8px 14px 0 rgba(0, 0, 0, 0.2);
    position: absolute;
    display: flex;
    flex-direction: column;
    justify-content: center;
    width: 100%;
    height: 100%;
    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
    border: 1px solid coral;
    border-radius: 1rem;
}

.flip-card-front {
    background: linear-gradient(
        120deg,
        bisque 60%,
        rgb(255, 231, 222) 88%,
        rgb(255, 211, 195) 40%,
        rgba(255, 127, 80, 0.603) 48%
    );
    color: coral;
}

.flip-card-back {
    background: linear-gradient(
        120deg,
        rgb(255, 174, 145) 30%,
        coral 88%,
        bisque 40%,
        rgb(255, 185, 160) 78%
    );
    color: white;
    transform: rotateY(180deg);
}
</style>
