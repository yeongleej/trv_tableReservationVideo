<template>
    <div class="title">
        <h1 style="margin-left: 100px; margin-top: 30px">마이 페이지</h1>
    </div>
    <div class="container">
        <RouterLink class="grid-item1" :to="{ name: 'MyInfoView' }">
            <v-card width="400" height="260">
                <v-card-text style="font-size: 30px; line-height: 190px">
                    <v-icon icon="mdi-account" size="80" color="blue"></v-icon>
                    내정보관리
                </v-card-text>
            </v-card>
        </RouterLink>
        <RouterLink class="grid-item2" :to="{ name: 'BusinessRegisterView' }">
            <v-card width="400" height="260">
                <v-card-text style="font-size: 30px; line-height: 190px">
                    <v-icon icon="mdi-file" size="80" color="brown"></v-icon>
                    <span>사업자 등록 및 수정</span>
                </v-card-text>
            </v-card>
        </RouterLink>
        <RouterLink class="grid-item1" :to="{ name: 'FollowerListView' }">
            <v-card width="400" height="260">
                <v-card-text style="font-size: 30px; line-height: 190px">
                    <v-icon icon="mdi-heart" size="80" color="red"></v-icon>
                    구독정보관리
                </v-card-text>
            </v-card>
        </RouterLink>
        <v-dialog width="500">
            <template v-slot:activator="{ props }">
                <v-btn v-bind="props" style="height: 260px; font-size: 30px">
                    <v-icon icon="mdi-credit-card" size="80" color="orange"></v-icon>
                    <span>자리패스 구매하기</span>
                </v-btn>
            </template>

            <template v-slot:default="{ isActive }">
                <v-card title="정기구독 구매하기">
                    <v-card-text>
                        <span>1개월 구독을 구매하시겠습니까?</span>
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn text="구매" @click="puchasePass"></v-btn>
                        <v-btn text="취소" @click="isActive.value = false"></v-btn>
                    </v-card-actions>
                </v-card>
            </template>
        </v-dialog>
    </div>
</template>

<script setup>
import { RouterLink } from 'vue-router';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import { ref } from 'vue';
const store = useCounterStore();
const accessToken = localStorage.getItem('accessToken');
const isActive = ref(false);

const puchasePass = () => {
    const redirect_uri = window.location.origin + '';
    // console.log(redirect_uri);
    axios({
        method: 'post',
        url: `${store.API_URL}/api/zpass/payment/ready`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        data: {
            price: 3990,
            itemName: store.userNickname,
            redirectUri: redirect_uri,
        },
    })
        .then((res) => {
            console.log('자리패스 구매 요청 성공');
            console.log(res.data);
            window.location.href = res.data.next_redirect_pc_url;
        })
        .catch((err) => {
            console.log('자리패스 구매 실패', err);
        });
    isActive.value = false;
};

const snsLogin = (type) => {
    const redirect_uri = window.location.origin + '';
    window.location.href = `${store.API_URL}/api/user/login/${type}?redirect_uri=${redirect_uri}/`;
};
</script>

<style scoped>
.title {
    display: flex;
    justify-content: center;
}
.container {
    margin-left: auto;
    margin-right: auto;
    display: grid;
    gap: 30px;
    grid-template-columns: 1fr 1fr;
    justify-content: center;
    align-items: center;
    width: 800px;
}
.grid-item1 {
    text-align: center;
    background-color: red;
    text-decoration: none;
    color: black;
    border-radius: 5px;
    display: flex;
}
.grid-item2 {
    text-align: center;
    background-color: blue;
    text-decoration: none;
    color: black;
    border-radius: 5px;
    display: flex;
}
</style>
