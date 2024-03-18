<template>
    <div class="container">
        <div class="form-border">
            <form class="form-size">
                <div style="display: flex">
                    <div style="width: 100%">
                        <label for="files">사업자 등록증 업로드:</label>
                        <input
                            type="file"
                            ref="storeBusinessFile"
                            multiple
                            @change="handleFileUpload($event)" />
                    </div>
                </div>
                <div style="display: flex">
                    <div style="width: 100%">
                        <label for="images">가게 대표 이미지 업로드:</label>
                        <input
                            type="file"
                            ref="images"
                            multiple
                            @change="handleStoreImgFileUpload($event)" />
                    </div>
                </div>
                <div style="display: flex">
                    <div style="width: 100%">
                        <label for="menuImg">가게 메뉴 이미지 업로드:</label>
                        <input
                            type="file"
                            ref="menu"
                            multiple
                            @change="handleMenuImgFileUpload($event)" />
                    </div>
                </div>
                <label for="">대표자명</label>
                <v-text-field v-model="ownerName" :counter="10"></v-text-field>
                <label for="">사업자 등록번호</label>
                <v-text-field v-model="businessNum" :counter="10"></v-text-field>
                <label for="">상호명</label>
                <v-text-field v-model="storeName"></v-text-field>
                <label for="">가게 주소명</label>
                <v-text-field v-model="storeAddress"
                    ><div class="text-center">
                        <v-btn variant="outlined"
                            >주소 검색하기
                            <v-dialog v-model="dialog" activator="parent" width="auto">
                                <v-card>
                                    <v-card-text>
                                        <Map />
                                    </v-card-text>
                                    <v-card-actions>
                                        <v-btn color="primary" block @click="getStoreAddress"
                                            >닫기</v-btn
                                        >
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                        </v-btn>
                    </div></v-text-field
                >
                <v-btn
                    v-if="registFlag"
                    type="submit"
                    @click="submitForm"
                    @submit.prevent="submitForm">
                    등록하기
                </v-btn>
                <v-btn v-else type="submit" @click="submitFormPut" @submit.prevent="submitFormPut">
                    수정하기
                </v-btn>
                <!-- handleReset 함수 => 폼 초기화 -->
            </form>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import router from '@/router';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import Map from '@/components/Map.vue';

const store = useCounterStore();
const accessToken = localStorage.getItem('accessToken');

// 기존의 저장된 유저 정보를 가져오는 로직임. :value로 바인딩해서 textfield에 연동
const storeData = ref(null);
const ownerName = ref('');
const businessNum = ref('');
const storeName = ref('');
const dialog = ref(false);
const storeX = ref(Number(store.storeX));
// console.log(storeX.value);
// console.log(typeof storeX.value);
const storeY = ref(Number(store.storeY));
const storeAddress = ref('');
const files = ref([]);
const storeImgFile = ref([]);
const menuImg = ref([]);
const storeId = ref(store.storeId);
const registFlag = ref(true);

const handleFileUpload = (event) => {
    files.value = event.target.files;
};

const handleStoreImgFileUpload = (event) => {
    storeImgFile.value = event.target.files;
};

const handleMenuImgFileUpload = (event) => {
    menuImg.value = event.target.files;
};

const getStoreData = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/owner`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            storeData.value = res.data;
            ownerName.value = res.data.ownerName;
            storeName.value = res.data.storeName;
            storeAddress.value = res.data.storeAddress;
            businessNum.value = res.data.storeBusinessNumber;
            // console.log(storeId.value);
            console.log(res.data);
            // console.log(storeData.value);
        })
        .catch((err) => {
            console.log('userData를 불러오지 못했습니다.', err);
        });
};

const putFlag = () => {
    if (storeId.value != '') {
        registFlag.value = false;
    }
};

onMounted(() => {
    if (storeId.value != '') {
        getStoreData();
    }
    putFlag();
    // console.log(store.userId);
});

const submitForm = () => {
    const formData = new FormData();
    formData.append('ownerName', ownerName.value);
    formData.append('storeName', storeName.value);
    formData.append('storeBusinessNumber', businessNum.value);
    formData.append('storePositionX', storeX.value);
    formData.append('storePositionY', storeY.value);
    formData.append('storeAddress', storeAddress.value);

    formData.append('storeBusinessFile', files.value[0]);
    formData.append('images', storeImgFile.value[0]);
    formData.append('menu', menuImg.value[0]);

    // console.log(Object.fromEntries(formData));
    // console.log(Object.fromEntries(formData));
    // 폼 유효성 검사
    if (!validateForm()) {
        // 필수 입력 필드가 비어있는 경우
        alert('모든 필수 입력 항목을 채워주세요.');
        return; // 폼 제출 중단
    }

    axios
        .post(`${store.API_URL}/api/store/regist`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: `Bearer ${accessToken}`,
            },
        })
        .then((response) => {
            console.log(response.data);
            localStorage.setItem('accessToken', response.headers.get('Authorization'));
            localStorage.setItem('refreshToken', response.headers.get('Authorization-refresh'));
            router.push({ name: 'MypageView' });
        })
        .catch((error) => {
            console.error(error);
        });

    // console.log('초기등록완료');
    // router.push({ name: 'MypageView' });
};

const submitFormPut = () => {
    const formData = new FormData();
    formData.append('ownerName', ownerName.value);
    formData.append('storeName', storeName.value);
    formData.append('storeBusinessNumber', businessNum.value);
    formData.append('storePositionX', storeX.value);
    formData.append('storePositionY', storeY.value);
    formData.append('storeAddress', storeAddress.value);

    formData.append('storeBusinessFile', files.value[0]);
    formData.append('images', storeImgFile.value[0]);
    formData.append('menu', menuImg.value[0]);

    // console.log(Object.fromEntries(formData));
    // 폼 유효성 검사
    if (!validateForm()) {
        // 필수 입력 필드가 비어있는 경우
        alert('모든 필수 항목을 입력 해주세요.');
        return; // 폼 제출 중단
    }

    axios
        .put(`${store.API_URL}/api/store/${store.storeId}/update`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: `Bearer ${accessToken}`,
            },
            // data: {
            //     ownerName: ownerName.value,
            //     storeName: storeName.value,
            //     storeBusinessNumber: businessNum.value,
            //     storeAddress: storeAddress.value,
            //     storePositionX: storeX.value,
            //     storePositionY: storeY.value,
            //     storeBusinessFile: files.value[0],
            //     images: storeImgFile.value[0],
            //     menu: menuImg.value[0],
            // },
        })
        .then((response) => {
            console.log(response.data);
            router.push({ name: 'MypageView' });
        })
        .catch((error) => {
            console.error(error);
        });

    console.log('사업자 정보 수정 완료');
};

const getStoreAddress = () => {
    dialog.value = false;
    storeAddress.value = store.storeAddress;
};

const validateForm = () => {
    // 필수 입력 필드들을 배열로 정의
    const requiredFields = [
        ownerName,
        businessNum,
        storeName,
        storeAddress,
        storeX,
        storeY,
        files,
        storeImgFile,
    ];

    // 필수 입력 필드들이 모두 채워져 있는지 확인
    const isValid = requiredFields.every((field) => {
        // 파일 필드인 경우, 길이를 확인
        if (Array.isArray(field.value)) {
            return field.value.length > 0;
        } else if (typeof field.value === 'string') {
            // 문자열인 경우 trim을 사용하여 값이 비어있는지 확인
            return field.value.trim() !== '';
        } else {
            // 그 외의 경우는 비어있지 않다고 간주
            return true;
        }
    });

    // 유효성 검사 결과 반환
    return isValid;
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
    height: 100%;
    justify-content: center;
    display: grid;
    margin-top: 50px;
}
.form-size {
    display: grid;
    width: 500px;
    height: 87%;
    border: 2px solid whitesmoke;
    border-radius: 5px;
    padding: 8px;
    align-items: center;
    align-content: space-evenly;
    margin-top: 50px;
}
.text-center {
    display: flex;
    justify-content: flex-end; /* 주소 검색 버튼을 오른쪽으로 정렬 */
}

.text-center v-btn {
    margin-left: auto; /* 오른쪽 여백을 추가하여 오른쪽 정렬 */
}

input[type='file'] {
    /* 파일 선택(input) 요소의 스타일을 정의합니다. */
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    background-color: #f9f9f9;
    /* 다른 스타일들을 추가로 적용할 수 있습니다. */
}
</style>
