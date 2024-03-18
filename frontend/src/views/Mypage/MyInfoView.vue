<template>
    <div class="container">
        <div class="form-border">
            <form @submit.prevent="submit" class="form-size">
                <label for="">전화번호</label>
                <v-text-field v-model="phoneNum" :counter="12"></v-text-field>
                <label for="">선호 주소</label>
                <v-select v-model="selectedCity" :items="storeAddress"></v-select>
                <v-btn type="submit"> 저장하기 </v-btn>
                <!-- handleReset 함수 => 폼 초기화 -->
            </form>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import router from '@/router';
const store = useCounterStore();
const accessToken = localStorage.getItem('accessToken');
const userData = ref(null);
const phoneNum = ref('');
const selectedCity = ref(null);
const storeAddress = ref([
    '서울',
    '부산',
    '인천',
    '대구',
    '대전',
    '광주',
    '울산',
    '세종',
    '경기도',
    '강원도',
    '충청북도',
    '충청남도',
    '전라북도',
    '전라남도',
    '경상북도',
    '경상남도',
    '제주특별자치도',
]);

onMounted(() => {
    getUserData();
});
// 기존의 저장된 유저 정보를 가져오는 로직임. :value로 바인딩해서 textfield에 연동
const getUserData = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/user/${store.userId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log(res.data);
            userData.value = res.data;
            phoneNum.value = res.data.phone;
            selectedCity.value = res.data.hometown;
            console.log(phoneNum.value);
        })
        .catch((err) => {
            console.log('userData를 불러오지 못했습니다.', err);
        });
};

const submit = function () {
    axios({
        method: 'put',
        url: `${store.API_URL}/api/user/${store.userId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        data: {
            // img: values.img,
            phone: phoneNum.value,
            hometown: selectedCity.value,
        },
    })
        .then((res) => {
            console.log('개인정보 수정이 완료되었습니다.');
            router.push({ name: 'MypageView' });
        })
        .catch((err) => {
            console.log('개인정보 수정에 실패했습니다.', err);
        });
};
</script>

<style scoped>
.container {
    display: grid;
    width: 100%;
    justify-content: center;
}
.form-border {
    border: 2px solid whitesmoke;
    border-radius: 8px;
    width: 700px;
    height: 600px;
    justify-content: center;
    display: grid;
    margin-top: 50px;
}
.form-size {
    display: grid;
    width: 500px;
    height: 480px;
    border: 2px solid whitesmoke;
    border-radius: 5px;
    padding: 8px;
    align-items: center;
    align-content: space-evenly;
    margin-top: 50px;
}
</style>
