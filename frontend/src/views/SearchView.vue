<template>
    <h1>검색 페이지입니다.</h1>

    <div style="min-height: 50vh">
        <div v-for="data in dataGet" :key="data.storeId" class="store-card">
            <div class="store-image">
                <img :src="data.storeImage" alt="Store Image" />
            </div>
            <div class="store-info">
                <h2>{{ data.storeName }}</h2>
                <p>{{ data.storeAddress }}</p>
                <p v-if="data.storeIsWaiting">운영 중</p>
                <p v-else>운영 중지</p>
                <p>팔로워 수: {{ data.storeFollowCount }}</p>
                <button @click="goToStore(data.storeId)">상세 보기</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();
const route = useRoute();
const router = useRouter();
const dataGet = ref([]);

// `searchText`를 라우트 파라미터에서 직접 추출
const searchText = ref(route.params.search || '');

const search = () => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/search?keyword=${searchText.value}`,
    })
        .then((res) => {
            dataGet.value = res.data;
        })
        .catch((err) => {
            console.error('검색 데이터 가져오기 실패:', err);
        });
};

// 라우트 파라미터 변경시 검색 함수 재호출
watch(
    () => route.params.search,
    (newValue) => {
        searchText.value = newValue || '';
        search();
    }
);

// 컴포넌트 마운트 시 검색 실행
onMounted(search);

function goToStore(storeId) {
    router.push({ name: 'StoreDetail', params: { storeId } });
}
</script>

<style scoped>
/* 전체 페이지에 적용되는 기본 스타일 조정 */
body {
    font-family: 'Arial', sans-serif;
    background-color: #f5f5f5;
    margin: 0;
    padding: 10px; /* 패딩을 20px에서 10px로 줄임 */
    color: #333;
}

/* 각 상점 정보를 감싸는 컨테이너 스타일 조정 */
div {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 15px; /* 마진을 20px에서 15px로 줄임 */
    padding: 15px; /* 패딩을 20px에서 15px로 줄임 */
    transition: transform 0.3s ease;
}

/* 상점 이름 */
h2 {
    color: #007bff;
    margin-top: 0;
    font-size: 18px; /* 텍스트 크기 줄임 */
}

/* 상세 정보 및 운영 상태 스타일 조정 */
p {
    line-height: 1.4; /* 라인 높이 조정 */
    margin: 8px 0; /* 마진을 줄임 */
    font-size: 14px; /* 텍스트 크기 줄임 */
}

/* 상세 보기 버튼 스타일 조정 */
button {
    background-color: #007bff;
    border: none;
    border-radius: 4px;
    color: white;
    cursor: pointer;
    padding: 8px 16px; /* 패딩 조정 */
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 14px; /* 텍스트 크기 줄임 */
    margin: 4px 2px;
    transition: background-color 0.2s ease;
}

button:hover {
    background-color: #0056b3;
}

.store-card {
    display: flex;
    align-items: flex-start; /* 상위 요소가 다른 높이를 가질 때, 정렬을 위해 사용 */
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    overflow: hidden; /* 이미지가 border-radius를 따르도록 */
}

.store-image img {
    width: 150px; /* 이미지 크기 조정, 필요에 따라 조정 */
    height: 100%; /* 이미지 높이를 카드 높이에 맞춤 */
    object-fit: cover; /* 이미지 비율을 유지하면서 요소에 맞춤 */
}

.store-info {
    padding: 20px;
    flex: 1; /* 나머지 공간을 모두 차지하도록 설정 */
}
</style>
