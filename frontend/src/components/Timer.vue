<template>
    <div>
        <div>
            {{ minutesRemaining }} 분 {{ secondsRemaining }} 초
            <v-btn color="blue" style="border: 1px solid blue">연장</v-btn>
        </div>
        <!-- <p><div>{{ decodedJWT }}</div></p> -->
    </div>
</template>

<script setup>
import axios from 'axios';
import { ref, onMounted } from 'vue';
import router from '@/router';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();
const API_URL = import.meta.env.VITE_API_URL;

const accessToken = store.accessToken;
const refreshToken = store.refreshToken;

// // 로컬 스토리지에서 토큰 가져오기
// const storedToken = accessToken;

// 토큰이 존재하는지 확인하고 초기화
const token = ref(accessToken ? accessToken.replace('Bearer', '') : '');

// 디코딩된 JWT를 담을 ref
const decodedJWT = ref(null);
const currentTime = ref(store.currentTime);

const endTime = ref(store.endTime); // 토큰 만료 시간 (밀리초)
const minutesRemaining = ref(store.minutesRemaining); // 남은 시간 (분)
const secondsRemaining = ref(store.secondsRemaining); // 남은 시간 (초)
const timeRemaining = ref(store.timeRemaining);
// 토큰이 변경될 때마다 디코드 수행
onMounted(() => {
    decodeJWT();
});

// JWT 디코딩 함수
const decodeJWT = () => {
    try {
        const base64Payload = token.value.split('.')[1];
        const base64 = base64Payload.replace(/-/g, '+').replace(/_/g, '/');
        const decodedData = JSON.parse(
            decodeURIComponent(
                window
                    .atob(base64)
                    .split('')
                    .map(function (c) {
                        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                    })
                    .join('')
            )
        );

        // decodedData 객체를 decodedJWT.value에 할당
        decodedJWT.value = decodedData;

        // exp Time을 설정
        if (decodedData && decodedData.exp) {
            endTime.value = decodedData.exp * 1000;

            // 남은 시간 업데이트
            updateRemainingTime();
            console.log('decode시 남은 시간', timeRemaining);
        }
    } catch (error) {
        console.error('Error decoding JWT:', error);
    }
};

// ------------여기까지 디코딩 관련 코드-------------------//

// ------------여기부터 타이머 관련 코드-------------------//

// confirm함수에서 로그인 연장을 취소를 눌렀을 때 F5를 눌러도 해당 값을 기억하고 confirm 창이 뜨지 않게 하기 위한 변수임 But, 제대로 작동 안함 수정 필요!
const confirmationDone = ref(false);

// 남은 시간 계산 함수
const updateRemainingTime = function () {
    timeRemaining.value = endTime.value - currentTime.value;
    minutesRemaining.value = Math.floor(timeRemaining.value / (1000 * 60));
    secondsRemaining.value = Math.floor((timeRemaining.value % (1000 * 60)) / 1000);
};

// 연장 확인 창이 열려있는지 나타내는 플래그
const promptOpenFlag = ref(false);

// 로그인 남은 시간이 10분 전일 때 연장 여부 확인 (즉 만료시간 10분 전에 알림)
const refreshLogin = function () {
    if (
        minutesRemaining.value == 59 &&
        secondsRemaining.value == 55 &&
        minutesRemaining.value > 0 &&
        !promptOpenFlag.value &&
        !confirmationDone.value
    ) {
        if (confirm('로그인 시간이 10분 남았습니다. 연장하시겠습니까?')) {
            // 로그인 연장 로직
            console.log('연장 로그인 함수가 실행되었습니다.');
            // extendLogin();
            eLogin();
        } else {
            promptOpenFlag.value = true; // 연장 확인 창이 닫혔음을 표시
            confirmationDone.value = true;

            // 자동 로그아웃 로직
            autoLogout();
        }
    }
};

// 연장로직
const eLogin = function () {
    axios
        .get(`${API_URL}/api/user/reissue`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Authorization-refresh': `Bearer ${refreshToken}`,
            },
        })
        .then((res) => {
            console.log('로그인 연장을 진행합니다.');

            store.accessToken = res.headers.get('authorization');
            store.refreshToken = res.headers.get('authorization-refresh');

            localStorage.setItem('accessToken', store.accessToken);
            localStorage.setItem('refreshToken', store.refreshToken);
            decodeJWT();

            console.log('연장 눌렀을 때 남은시간 :', timeRemaining);
        })
        .catch((err) => {
            console.error('로그인 연장 실패:', err);
        });
};

// 로그아웃 로직
const logout = function () {
    axios({
        method: 'post',
        url: `${API_URL}/api/user/logout`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
            // "Authorization-refresh" refresh Token
        },
    })
        .then((res) => {
            // console.log(res);
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '/';
        })
        .catch((err) => {
            console.log(err);
        });
};

// 자동 로그아웃 로직
const autoLogout = function () {
    alert('10분후 자동로그아웃 됩니다.');
    console.log('자동 로그아웃 로직 시작');

    // 남은시간 후에 로그아웃 로직 추가
    setTimeout(() => {
        console.log('로그아웃 함수 시작');
        logout();
        console.log('로그아웃 함수 완료');
        router.push({ name: 'HomeView' });
        console.log('자동 로그아웃 로직 완료');
        // 추가적인 로그아웃 처리를 수행할 수 있습니다.
    }, timeRemaining.value - 2000); // 남은 시간 후에 setTimeout 안에 함수들 작동되도록 하는 딜레이 시간임
};

// 매 1초마다 남은 시간 확인
onMounted(() => {
    setInterval(() => {
        currentTime.value = new Date().getTime();
        updateRemainingTime();
        refreshLogin();
    }, 1000);
});

// 초기 로딩 시 남은 시간 업데이트
// 이 부분이 없으면 새로 고침 시 약 1~2초간 onMounted가 진행되는 동안 0분 00초로 표기됨
updateRemainingTime();
</script>

<style scoped></style>
