<template>
    <div style="display: flex; justify-content: center" v-bind="$attrs">
        <div style="display: flex">
            <div style="display: flex; align-items: center; margin-right: 10px">
                <v-icon icon="mdi-filter"></v-icon>정렬 :
            </div>
        </div>
        <div style="width: 150px; height: 50%; padding: 0px">
            <div style="display: flex; align-items: center">
                <select
                    class="selectBox"
                    :items="arrangeList"
                    v-model="selectedArrange"
                    @change="selectChange">
                    <option value="추천순">추천순</option>
                    <option value="참여인원순">참여인원순</option>
                    <option value="최신방송순">최신방송순</option>
                </select>
            </div>
        </div>
    </div>
    <v-container style="display: flex; flex-wrap: wrap; width: 1200px">
        <div
            v-for="user in dataList.list"
            :key="user.storeId"
            style="
                display: flex;
                flex-wrap: wrap;
                width: 270px;
                margin: 8px;
                justify-content: space-evenly;
            ">
            <v-card
                @click="goToStreaming(user.storeId)"
                style="height: 300px; margin-left: 20px; cursor: pointer">
                <img
                    :src="
                        user.image || 'https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/null.png'
                    "
                    alt="Thumbnail"
                    style="
                        height: 60%;
                        width: 100%;
                        object-fit: cover;
                        padding: 5px;
                        display: flex;
                    " />
                <div style="display: flex; flex-wrap: wrap">
                    <v-card-title style="font-size: medium"
                        ><img
                            :src="
                                user.userImage ||
                                'https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/null.png'
                            "
                            alt="userimage"
                            style="height: 50px; border-radius: 50px" />
                        {{ user.title }}
                        <div style="display: flex">
                            <p style="margin-bottom: 8px; font-weight: 600">
                                {{ user.userNickname }}
                            </p>
                            <p style="position: absolute; right: 10px">{{ user.category }}</p>
                        </div>
                        <div style="font-size: small; color: gray; margin-top: -10px">
                            <p>{{ user.userCount }}명 시청중</p>
                        </div>
                    </v-card-title>

                    <v-card-subtitle style="text-align: left; margin-left: 33px"> </v-card-subtitle>
                </div>
                <v-card-actions>
                    <v-spacer></v-spacer>
                </v-card-actions>
            </v-card>
        </div>
    </v-container>
</template>

<script setup>
import { onMounted } from 'vue';
import { ref } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import router from '@/router';

const store = useCounterStore();
const accessToken = ref('');
const dataList = ref([]);
const arrangeList = ref(['추천순', '참여인원순', '최신방송순']);
const selectedArrange = ref('참여인원순');

const selectChange = function () {
    console.log('selectChange 함수시작');
    console.log(selectedArrange.value);
    if (selectedArrange.value === '최신방송순') {
        console.log('함수시작');
        axios({
            method: 'get',
            url: `${store.API_URL}/api/playroom/search?sort=modDate,desc`,
            headers: {
                Authorization: `Bearer ${accessToken.value}`,
            },
        })
            .then((res) => {
                console.log('정렬 최신방송순', res.data);
                dataList.value = res.data;
            })
            .catch((err) => {
                console.log(err);
            });
    } else if (selectedArrange.value === '추천순') {
        console.log('함수시작');
        axios({
            method: 'get',
            url: `${store.API_URL}/api/playroom/search?sort=likes`,
            headers: {
                Authorization: `Bearer ${accessToken.value}`,
            },
        })
            .then((res) => {
                dataList.value = res.data;
                console.log('정렬 추천순', dataList.value);
            })
            .catch((err) => {
                console.log(err);
            });
    } else {
        console.log('함수시작');
        axios({
            method: 'get',
            url: `${store.API_URL}/api/playroom/search?sort=userCount,desc`,
            headers: {
                Authorization: `Bearer ${accessToken.value}`,
            },
        })
            .then((res) => {
                dataList.value = res.data;
                console.log('정렬 참여인원순', dataList.value);
            })
            .catch((err) => {
                console.log(err);
            });
    }
};
const getStreamData = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/playroom/search?sort=userCount`,
        headers: {
            Authorization: `Bearer ${accessToken.value}`,
        },
    })
        .then((res) => {
            dataList.value = res.data;
            console.log('디폴트 리스트', dataList.value);
            console.log(typeof dataList.value);
        })
        .catch((err) => {
            console.log(err);
        });
};

const goToStreaming = (PlayroomId) => {
    router.push({
        path: `/streaming/${PlayroomId}`,
        params: {
            storeId: PlayroomId,
        },
    });
};

onMounted(() => {
    console.log('onmounted 실행됨');
    accessToken.value = localStorage.getItem('accessToken');
    getStreamData();
});
</script>
<style scoped>
.selectBox {
    width: 150px;
    display: flex;
    border-radius: 4px;
    border: 2px solid black;
    text-align: center;
    align-items: center;
}
</style>
