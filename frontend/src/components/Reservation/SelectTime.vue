<template>
    <div class="reservation">
        <div>
            <h3 v-if="store.reqDate != null">예약시간을 선택해주세요</h3>
            <v-row :no-gutters="true">
                <v-col
                    v-for="timeOption in availableTimesSorted"
                    :key="timeOption.time"
                    cols="12"
                    md="3">
                    <v-checkbox
                        v-model="selectedTime"
                        :label="timeOption.time"
                        :value="timeOption.time"
                        density="compact"
                        class="custom-checkbox"
                        @change="handleCheckboxChange"></v-checkbox>
                </v-col>
            </v-row>
        </div>
        <div class="table-reservation" @dragover.prevent="handleDragOver" ref="tableReservation">
            <h3>예약을 원하시는 테이블을 선택해주세요.</h3>
            <hr />
            <div
                v-for="table in tables"
                :key="table.diningTableNumber"
                class="table"
                @click="handleTableClick(table)"
                :style="{
                    top: table.diningTableY + 'px',
                    left: table.diningTableX + 'px',
                    width: table.width + 'px',
                    height: table.height + 'px',
                    borderRadius: table.diningTableType === '원형테이블' ? '50%' : '7%',
                    backgroundColor:
                        table.status == 'INACTIVE'
                            ? 'rgba(169, 169, 169, 0.2)'
                            : table.diningTableType === '창문'
                            ? 'skyblue'
                            : table.diningTableType === '카운터'
                            ? '#FFD28F'
                            : table.diningTableType === '입구'
                            ? '#A3C9AA'
                            : table.diningTableType === '룸' || table.diningTableType === '단체석'
                            ? '#ffcccb'
                            : '#C69774',
                    boxShadow:
                        table.status == 'INACTIVE'
                            ? '3px 3px 6px 0px #CCDBE8 inset, -3px -3px 6px 1px rgba(255,255,255,0.5) inset'
                            : '0px 5px 10px rgba(0, 0, 0, 0.2)',
                }">
                <span v-if="table.diningTableType === '창문'">
                    <!-- Condition for window type -->
                    <span v-if="table.width > table.height">창문</span>
                    <span v-else-if="table.width < table.height">창문</span>
                </span>
                <span v-else-if="table.diningTableType === '카운터'">
                    <!-- Condition for counter type -->
                    카운터
                </span>
                <span v-else-if="table.diningTableType === '입구'">
                    <!-- Condition for entrance type -->
                    입구
                </span>
                <span
                    v-else-if="
                        table.diningTableType === '룸' || table.diningTableType === '단체석'
                    ">
                    <!-- Condition for room and group table type -->
                    {{ table.diningTableType }}
                </span>
                <span v-else>
                    <!-- Default text for other table types -->
                    ~{{ table.diningTableCapacity }}인
                </span>
            </div>
        </div>
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
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
const props = defineProps({
    storeId: String,
});
const store = useCounterStore();
const selectedTime = ref('');
const accessToken = localStorage.getItem('accessToken');
const availableTimes = ref([]);
const ainfo = ref([]);

const tables = ref([]);
const reservationUserCount = ref(0);
const selectedTable = ref(null);
const showReservationCard = ref(false);

const getAvailableTimes = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/reservation/search/available/store/${props.storeId}/date/${store.reqDate}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            // 정렬 추가
            console.log(res.data);
            availableTimes.value = res.data.sort((a, b) => a.time.localeCompare(b.time));
            store.aList.value = availableTimes;
            ainfo.value = res.data;
            console.log('>>>>>>>>>>> selected Time aList 변경 <<<<<<<<<<<<<<');
            console.log(ainfo.value);
        })
        .catch((err) => {
            console.log(err);
        });
};

onMounted(() => {
    console.log(props.storeId);
    if (store.reqDate) {
        getAvailableTimes();
    }
    store.saveSelectedTime();

    getTableData();
    store.setSelectedTable();
    store.setSelectedTable();
    console.log('>>>>>>>>> selectedTable mounted <<<<<<<<<<<');
    console.log(store.aList);
});

const handleCheckboxChange = () => {
    // selectedTime is an array containing the selected times
    store.saveSelectedTime(selectedTime.value);
    console.log('선택된 날짜 : ' + selectedTime.value);
    console.log('>>' + ainfo.value);
    ainfo.value.forEach((element) => {
        console.log(element);
        if (selectedTime.value == element.time) {
            console.log(element.diningTableResponseDtoList);
            tables.value = element.diningTableResponseDtoList;
        }
    });
};

// reqDate가 변경될 때마다 getAvailableTimes 호출
watch(
    () => store.reqDate,
    () => {
        if (store.reqDate) {
            getAvailableTimes();
            console.log(store.aList);
        }
    }
);

// 정렬된 배열을 computed property로 생성
const availableTimesSorted = ref([]);
watch(
    () => availableTimes.value,
    () => {
        availableTimesSorted.value = [...availableTimes.value];
        console.log('>>>>>>>시간 변경<<<<<<<<<');
        console.log(availableTimesSorted);
    }
);

watch(
    () => store.aList,
    () => {
        console.log('>>>>>>>>> selectedTable watch <<<<<<<<<<<');
        console.log(store.aList);
    }
);

////////////////////////////////////////// selected table ////////////////////////////////////////////
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
/* .reservation {
    display: flex;
    flex-direction: row;
} */
.table-reservation {
    position: relative;
    border: 2px solid rgb(216, 216, 216);
    width: 100%;
    height: 568px;
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
/* box-shadow: 3px 3px 6px 0px #CCDBE8 inset, -3px -3px 6px 1px rgba(255,255,255,0.5) inset; */
</style>
