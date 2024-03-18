<template>
    <v-card-text v-show="showReservationCard">
        <h4>인원수를 선택해주세요</h4>
        <h2>
            <v-btn v-bind="reservationUserCount" @click="decreaseWait" width="60" height="30"
                >-</v-btn
            >
            {{ reservationUserCount }}
            <v-btn @click="increaseWait">+</v-btn>
        </h2>
    </v-card-text>
</template>

<script setup>
import { ref, onMounted, watch, watchEffect, computed } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import router from '@/router';
const props = defineProps({
    storeId: String,
});
const store = useCounterStore();
const tables = ref([]);
const accessToken = localStorage.getItem('accessToken'); // Replace with your actual access token
const reservationUserCount = ref(0);
const selectedTable = ref(null);
const showReservationCard = ref(false);

onMounted(() => {
    console.log(props.storeId);
    getTableData();
    store.setSelectedTable();
    store.setSelectedTable();
    console.log('>>>>>>>>> selectedTable mounted <<<<<<<<<<<');
    console.log(store.aList);
});

watch(
    async () => store.aList,
    (newValue, oldValue) => {
        console.log('>>>>>>>>> selectedTable watch <<<<<<<<<<<');
        console.log(newValue);
    }
);
// // 데이터 변경 감지를 위한 computed 속성 생성
// const selectedTableWatcher = computed(() => {
//     console.log('>>>>>>>>> selectedTable computed <<<<<<<<<<<');
//     return store.aList;
// });

// //computed 속성 사용
// console.log(selectedTableWatcher.value); // 초기 값 출력

// // 데이터가 변경될 때마다 감지
// selectedTableWatcher.watch((newValue, oldValue) => {
//     console.log('>>>>>>>>> selectedTable watch <<<<<<<<<<<');
//     console.log(newValue);
// });

const getTableData = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${props.storeId}/dining-tables`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log(res.data);
            tables.value = res.data;
            // Assuming 'router' is defined in your component
        })
        .catch((err) => {
            console.log(err);
        });
};

const handleTableClick = (table) => {
    selectedTable.value = table;
    reservationUserCount.value = 0; // Reset reservationUserCount when a new table is clicked
    showReservationCard.value = true;
    store.setSelectedTable(selectedTable.value);
};

const decreaseWait = () => {
    // 인원 수 감소
    if (reservationUserCount.value > 0) {
        reservationUserCount.value--;
    }
    store.setSelectedCapacity(reservationUserCount.value);
};

const increaseWait = () => {
    // 인원 수 증가
    if (reservationUserCount.value < selectedTable.value.diningTableCapacity) {
        reservationUserCount.value++;
    }
    store.setSelectedCapacity(reservationUserCount.value);
};
</script>

<style scoped>
/* 추가적인 스타일링이 필요한 경우 여기에 추가하세요. */
.table-reservation {
    position: relative;
    border: 2px solid rgb(216, 216, 216);
    width: 100%;
    height: 768px;
    h3 {
        text-align: center;
        margin-bottom: 10px;
    }

    .table {
        position: absolute;
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        user-select: none;
        font-size: small;
    }
}
table:hover {
    transform: scale(1.1);
}
.table-container {
    position: relative;
    width: 100%;
    height: 768px;
}
</style>
