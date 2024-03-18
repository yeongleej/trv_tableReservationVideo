<template>
    <v-row justify="center">
        <v-dialog v-model="wait" persistent width="auto">
            <template v-slot:activator="{ props }">
                <v-btn
                    style="height: 50px; width: 150px; font-size: medium"
                    color="default"
                    v-bind="props"
                    @click="joinWait">
                    웨이팅
                </v-btn>
            </template>
            <v-container v-if="waitStatus">
                <v-card v-if="waitFlag">
                    <!-- 완료된 예약이 없을 때의 내용 -->
                    <v-card-title v-bind="waitingUserOrder" class="text-h5">
                        남은 웨이팅 수 : {{ waitingUserOrder }}
                    </v-card-title>
                    <v-card-text>
                        인원수를 입력하세요
                        <v-btn v-bind="peoplenum" @click="decreaseWait" width="60" height="30"
                            >-</v-btn
                        >
                        {{ peoplenum }}
                        <v-btn @click="increaseWait">+</v-btn>
                    </v-card-text>
                    <v-card-actions>
                        <v-btn color="primary" @click="addWait">예약하기</v-btn>
                        <v-btn color="red" @click="wait = false">취소</v-btn>
                    </v-card-actions>
                </v-card>
                <v-card v-else>
                    <!-- 예약이 완료되지 있을 때의 내용 -->
                    <v-card-title v-bind="waitingUserOrder" class="text-h5">
                        <!-- 백이랑 연결 시 db에서 웨이팅 팀의 수를 들고와야함 -->
                        고객님의 웨이팅 번호 : {{ waitingUserOrder + 1 }}
                    </v-card-title>
                    <v-card-subtitle> 웨이팅이 완료되었습니다. </v-card-subtitle>
                    <v-card-text> 인원수: {{ peoplenum }}명 </v-card-text>
                    <v-card-actions>
                        <v-btn color="success" @click="wait = false">확인</v-btn>
                    </v-card-actions>
                </v-card>
            </v-container>
        </v-dialog>
    </v-row>
</template>

<script setup>
import { ref } from 'vue';
import router from '@/router';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import { onMounted } from 'vue';
const store = useCounterStore();
const accessToken = localStorage.getItem('accessToken');
const waitList = ref(null);
// 예약 인원 수
const peoplenum = ref(0);
// 남은 웨이팅 수
const waitingUserOrder = ref(0);

// wait flag임
const waitFlag = ref(true);

// storeId
const props = defineProps({
    storeId: String,
});

// waitState
const waitState = ref(null);

const storeId = ref(props.storeId);
console.log(storeId.value);

const getWaitState = () => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/user/waiting/${storeId.value}/waiting`, //숫자 부분을 추후 storeId로 바꿔야 함
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            waitState.value = res.data;
            console.log(waitState.value.length);

            if (waitState.value.length === 0) {
                waitFlag.value = true;
                peoplenum.value = 0;
            } else {
                peoplenum.value = res.data.waitingUserCount;
                waitFlag.value = false;
            }

            console.log(waitState.value);
            console.log('웨이팅 상태 정보를 가져왔습니다.');
        })
        .catch((err) => {
            console.log('웨이팅 상태 정보 가져오기 실패', err);
        });
};

// 웨이팅 인원 확인
const getWaitNumber = () => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${storeId.value}/waiting/list`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            waitingUserOrder.value = res.data.length;
            console.log(waitingUserOrder.value);

            console.log('남은 웨이팅 수 정보를 가져왔습니다.');
        })
        .catch((err) => {
            console.log('남은 웨이팅 수 정보 가져오기 실패', err);
        });
};

// waiting 등록
const addWait = () => {
    axios({
        method: 'post',
        url: `${store.API_URL}/api/store/${storeId.value}/waiting`, // 숫자 부분을 storeId로 추후에 수정 필요
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        data: {
            waitingUserCount: peoplenum.value,
        },
    })
        .then((res) => {
            console.log('웨이팅 등록 완료');
            wait.value = false;
            waitFlag.value = false;
            getWaitState();
        })
        .catch((err) => {
            console.log('웨이팅 등록 실패', err);
        });
};

onMounted(() => {
    if (store.userId != undefined) {
        getWaitState();
    }
    getWaitNumber();
});

// 웨이팅 팀 수
const remainingWait = ref(10);
// 전화문의 버튼 클릭 시 모달의 v-model 변수
const call = ref(false);
// 웨이팅 버튼 클릭 시  모달의 v-model 변수
const wait = ref(false);

// 만약 전화문의 ok 버튼을 눌렀을 때 다른 페이지로 넘어가려면 아래 함수 활용
const callQues = function () {
    call.value = false;
    router.push({ name: '' });
};

const decreaseWait = () => {
    // 인원 수 감소
    if (peoplenum.value > 0) {
        peoplenum.value--;
    }
};

const increaseWait = () => {
    // 인원 수 증가
    peoplenum.value++;
};

const reserveWait = () => {
    // 웨이팅 예약 처리
    // 여기에서 예약 로직을 추가하면 됩니다.
    // 모달창이 떠야 함.

    // wait.value = false; // 모달 닫기
    notReservation.value = false;
};

const waitStatus = ref(false);

const joinWait = async () => {
    wait.value = false;
    if (store.userId == undefined) {
        alert('로그인후 사용해 주세요!');
    } else {
        await checkStoreStates();

        if (!waitStatus.value) {
            alert('사장님께서 채팅 기능을 닫아두셨습니다!');
        } else {
            wait.value = true;
        }
        getWaitState();
    }
};

// 스트리밍, 통화, 채팅 상태 확인
const checkStoreStates = async () => {
    // 상태 전송
    await axios({
        method: 'get',
        url: `${store.API_URL}/api/playroom/${storeId.value}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log('가게 상태 정보 검색에 성공했습니다.');
            waitStatus.value = res.data.waiting;
        })
        .catch((err) => {
            // 업데이트에 실패한 경우 처리
            console.log('가게 상태 정보 검색에 실패했습니다.', err);
        });
};
</script>

<style scoped></style>
