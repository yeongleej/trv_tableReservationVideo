<template>
    <v-card class="mx-auto" max-width="500">
        <v-card-title> 웨이팅 리스트 </v-card-title>

        <v-divider></v-divider>

        <v-virtual-scroll :items="customerWaitingList" height="320" item-height="48">
            <template v-slot:default="{ item }">
                <v-list-item
                    :title="`예약자명 : ${item.userName}`"
                    :subtitle="`웨이팅 총 인원수 : ${item.waitingUserCount}`">
                    <template v-slot:prepend>
                        <v-container>{{ item.waitingUserOrder }}.</v-container>
                        <v-icon class="bg-primary">mdi-account</v-icon>
                    </template>

                    <template v-slot:append>
                        <v-btn
                            @click="acceptWait(item.userId)"
                            icon="mdi-account-arrow-left"
                            size="x-small"
                            variant="tonal"></v-btn>
                    </template>
                </v-list-item>
            </template>
        </v-virtual-scroll>
    </v-card>
</template>

<script setup>
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import { ref } from 'vue';
import { onMounted } from 'vue';
const store = useCounterStore();
const customerWaitingList = ref([]);
const accessToken = localStorage.getItem('accessToken');

// storeId기준으로 방송하고 있는 가게에 웨이팅 리스트를 모두 불러옴
const getWaitList = () => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${store.storeId}/waiting/list`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            customerWaitingList.value = res.data;
            console.log(customerWaitingList.value);

            console.log('고객님들의 웨이팅 정보를 가져왔습니다.');
        })
        .catch((err) => {
            console.log('고객님들의 웨이팅 정보 가져오기 실패', err);
        });
};
onMounted(() => {
    getWaitList();
});

const acceptWait = (customerId) => {
    axios({
        method: 'delete',
        url: `${store.API_URL}/api/store/${store.storeId}/waiting/${customerId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            getWaitList();
            console.log('고객님의 웨이팅 요청을 수락했습니다.');
        })
        .catch((err) => {
            console.log('고객님의 웨이팅 요청 수락을 실패했습니다.', err);
        });
};
</script>

<style scoped></style>
