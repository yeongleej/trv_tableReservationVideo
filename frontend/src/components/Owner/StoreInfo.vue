<template>
    <div class="store-edit">
        <h2>
            {{ storeName }}
            <button @click="openModal">저장</button>
        </h2>
        <hr />
        <div class="form-group">
            <label for="storeDetail">가게상세</label>
            <input v-model="storeDetail" type="text" id="storeDetail" />
            <div class="form-group">
                <label for="storePhone">전화번호</label>
                <input v-model="storePhone" type="text" id="storePhone" />
            </div>
        </div>
        <hr />
        <div class="form-group">
            <label for="storeMenu">메뉴</label>
            <textarea v-model="storeMenu" id="storeMenu"></textarea>
        </div>
        <hr />
        <div class="form-group">
            <label for="storeOperationDates">영업일</label>
            <input v-model="storeOperationDates" type="text" id="storeOperationDates" />
            <div class="form-group">
                <label for="storeOperationHours">영업시간</label>
                <input v-model="storeOperationHours" type="text" id="storeOperationHours" />
            </div>
            <v-dialog v-model="modal" max-width="600">
                <v-card>
                    <v-card-title>설정 저장</v-card-title>
                    <v-card-text> 설정을 저장하시겠습니까? </v-card-text>
                    <v-card-actions>
                        <v-btn @click="closeModal">취소</v-btn>
                        <v-btn @click="saveChangesAndCloseModal">확인</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();

const accessToken = localStorage.getItem('accessToken');

const storeInfo = ref(null);
// const storeAddress = ref(storeInfo.value.storeAddress);
const storeName = ref('');
const storeDetail = ref('');
const storeFollowCount = ref('');
const storeId = ref('');
const storeIsWaiting = ref('');
const storeMenu = ref('');
const storeOperationHours = ref('');
const storeOperationDates = ref('');
const storePhone = ref('');
const storePositionX = ref('');
const storePositionY = ref('');
const storeReservationEndDate = ref('');
const storeReservationStartDate = ref('');
const storeReservationStatus = ref('');

// 스토어 정보 불러오는 함수
const loadStoreData = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${store.storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            storeInfo.value = res.data;
            storeName.value = storeInfo.value.storeName;
            storeDetail.value = storeInfo.value.storeDetail;
            storeOperationHours.value = storeInfo.value.storeOperationHours;
            storeMenu.value = storeInfo.value.storeMenu;
            storePhone.value = storeInfo.value.storePhone;
            storeOperationDates.value = storeInfo.value.storeOperationDates;
        })
        .catch((err) => {
            console.log('Data를 불러오지 못했습니다.', err);
            console.log('Response:', err.response);
        });
};

const saveChanges = function () {
    // 변경된 정보를 서버에 저장하는 로직 추가
    const payload = {
        storeDetail: storeDetail.value,
        storePhone: storePhone.value,
        storeMenu: storeMenu.value,
        storeOperationDates: storeOperationDates.value,
        storeOperationHours: storeOperationHours.value,
    };
    axios({
        method: 'put',
        url: `${store.API_URL}/api/store/${store.storeId}/info`,
        data: payload,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            // 성공적으로 업데이트된 경우 처리
            console.log('가게 정보가 성공적으로 업데이트되었습니다.');
        })
        .catch((err) => {
            // 업데이트에 실패한 경우 처리
            console.log('가게 정보 업데이트에 실패했습니다.', err);
        });
};
const modal = ref(false);

const openModal = function () {
    modal.value = true;
};

const closeModal = function () {
    modal.value = false;
};

const saveChangesAndCloseModal = function () {
    saveChanges(); // 변경된 정보를 서버에 저장
    closeModal(); // 모달 닫기
};
onMounted(() => {
    loadStoreData();
    // 이 부분에서 DB에서 초기 가게 정보를 받아오는 API 호출
    // API 호출 후 받아온 정보를 각 변수에 할당
});
</script>

<style scoped>
.store-edit {
    border: 1px solid grey;
    padding: 20px;
    font-size: medium;
    .form-group {
        margin-bottom: 20px;

        label {
            display: block;
            margin-bottom: 5px;
        }

        input,
        textarea {
            width: 50%;
            padding: 8px;
            box-sizing: border-box;
        }
    }
    h2 {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    button {
        margin-right: 20px;
        padding: 8px;
        background-color: #5b8f58;
        color: #fff;
        border: 3px solid darkgreen;
        cursor: pointer;
        font-size: 16px;
        border-radius: 30px;
    }
}
</style>
