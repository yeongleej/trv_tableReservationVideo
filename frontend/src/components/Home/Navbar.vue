<template>
    <v-app-bar flat :height="80" style="background-color: #fffce0">
        <RouterLink style="padding-left: 2.5rem" :to="{ name: 'HomeView' }">
            <div style="display: flex">
                <img class="logo-img" src="@/img/mainlogo3.png" alt="logo" />
            </div>
        </RouterLink>
        <div style="width: 7rem"></div>
        <v-text-field
            v-model="searchText"
            append-icon="mdi-magnify"
            label="검색"
            solo-inverted
            hide-details
            @keydown.enter="search"></v-text-field>
        <v-sheet
            class="d-flex"
            style="padding-left: 7rem; background-color: #fffce0"
            v-if="accessToken != null">
            <button v-if="passFlag" class="buttonPass" style="margin-top: 0.3rem">
                <v-icon icon="mdi-credit-card"></v-icon>Tpass
            </button>
            <div style="width: 10rem; text-align: center">
                {{ minutesRemaining }} 분 {{ secondsRemaining }} 초
                <v-btn color="primary" @click="eLogin">연장</v-btn>
            </div>
            <v-dialog v-model="dialogOpen">
                <!-- 다이얼로그 내용 -->
                <v-card>
                    <v-card-title>로그인 연장 확인</v-card-title>
                    <v-card-text> 로그인 시간이 10분 남았습니다. 연장하시겠습니까? </v-card-text>
                    <v-card-actions>
                        <!-- 확인 버튼 -->
                        <v-btn color="primary" @click="eLogin">확인</v-btn>
                        <!-- 취소 버튼 -->
                        <v-btn @click="dialogOpen = false">취소</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog></v-sheet
        >
        <v-btn v-if="accessToken != null" @click="logout">Logout</v-btn>
        <RouterLink
            style="padding-left: 100px; padding-right: 3rem"
            v-else
            :to="{ name: 'LoginView' }">
            <v-btn> Login </v-btn>
        </RouterLink>
    </v-app-bar>
    <v-app-bar flat :height="60" style="background-color: #fff7f1">
        <SidebarLogout v-if="!accessToken" />
        <SidebarLogin v-else
    /></v-app-bar>
</template>
<script setup>
import { RouterLink } from 'vue-router';
import axios from 'axios';
import { ref } from 'vue';
import { onMounted } from 'vue';
import router from '@/router';
import SidebarLogout from './SidebarLogout.vue';
import SidebarLogin from './SidebarLogin.vue';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();
const API_URL = import.meta.env.VITE_API_URL;
const searchText = ref(''); // 검색어를 저장하는 데이터 변수
const dialogOpen = ref(false);
const passFlag = ref(store.zpassState);
const zpassState = () => {
    console.log('패스 유무 상태', passFlag.value);
    if (store.zPass == null) {
        passFlag.value = false;
    } else {
        passFlag.value = true;
    }
};

const search = function () {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/search?keyword=${searchText.value}`,
    })
        .then((res) => {
            router.push({
                name: 'SearchView',
                params: { search: searchText.value },
            });
        })
        .catch((err) => {
            console.error('검색 데이터 가져오기 실패:', err);
        });
};
const accessToken = ref(localStorage.getItem('accessToken'));

const refreshToken = ref(localStorage.getItem('refreshToken'));
const token = ref(accessToken.value ? accessToken.value.replace('Bearer', '') : null);
// 디코딩된 JWT를 담을 ref
const decodedJWT = ref(null);
const endTime = ref(9999999999999);
const currentTime = ref(new Date().getTime()); // 현재 시간의 밀리초
const timeRemaining = ref(0);
const minutesRemaining = ref(0);
const secondsRemaining = ref(0);
// 토큰이 변경될 때마다 디코드 수행
onMounted(() => {
    decodeJWT();
    zpassState();
});
// JWT 디코딩 함수
const decodeJWT = () => {
    if (token.value)
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
        minutesRemaining.value === 10 &&
        secondsRemaining.value === 0 &&
        minutesRemaining.value > 0 &&
        !promptOpenFlag.value &&
        !confirmationDone.value
    ) {
        dialogOpen.value = true;
    }
};
// 연장로직
const eLogin = function () {
    axios
        .get(`${API_URL}/api/user/reissue`, {
            headers: {
                Authorization: `Bearer ${accessToken.value}`,
                'Authorization-refresh': `Bearer ${refreshToken.value}`,
            },
        })
        .then((res) => {
            console.log('로그인 연장을 진행합니다.');
            accessToken.value = res.headers.get('authorization');
            refreshToken.value = res.headers.get('authorization-refresh');

            localStorage.setItem('accessToken', accessToken.value);
            localStorage.setItem('refreshToken', refreshToken.value);
            token.value = accessToken.value ? accessToken.value.replace('Bearer', '') : null;
            decodeJWT();
            dialogOpen.value = false;

            // window.location.reload(); 최후의 수단임( 강제 로그아웃 )
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
            Authorization: `Bearer ${accessToken.value}`,
            // "Authorization-refresh" refresh Token
        },
    })
        .then((res) => {
            dialogOpen.value = false;
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('counter');
            window.location.href = '/';
        })
        .catch((err) => {
            console.log(err);
        });
};

const expLogout = () => {
    dialogOpen.value = false;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('counter');
    endTime.value = 9999999999999;
    window.location.href = '/';
};

// 매 1초마다 남은 시간 확인
onMounted(() => {
    setInterval(() => {
        currentTime.value = new Date().getTime();
        updateRemainingTime();
        refreshLogin();
        // console.log('토큰 만료시간', endTime.value);
        // console.log('현재 시간', currentTime.value);
        if (endTime.value <= currentTime.value) {
            expLogout();
        }
    }, 1000);
});

// 초기 로딩 시 남은 시간 업데이트
// 이 부분이 없으면 새로 고침 시 약 1~2초간 onMounted가 진행되는 동안 0분 00초로 표기됨
updateRemainingTime();
</script>
<style scoped>
/* 추가된 스타일 */
.v-input--solo .v-input__control {
    border-radius: 4px;
    background-color: #f5f5f5; /* 또는 다른 적절한 색상 */
}
.v-input--solo .v-input__slot {
    padding-left: 10px;
    padding-right: 10px;
}
/* 검색 아이콘 스타일 */
.v-icon--prepend {
    color: #666; /* 또는 다른 적절한 색상 */
}
/* 검색 입력 텍스트 스타일 */
.v-input__input {
    color: #333; /* 또는 다른 적절한 색상 */
}

.logo-img {
    width: 210px;
    height: 150px;
    margin-top: 0.5rem;
    margin-right: 0.3rem;
    display: flex;
}

.search-bar {
    width: 300px;
}

/* ------------------------------------- */
.buttonPass {
    text-decoration: none;
    position: relative;
    border: none;
    font-size: 14px;
    font-family: inherit;
    cursor: pointer;
    color: #fff;
    width: 5em;
    height: 2em;
    line-height: 2em;
    text-align: center;
    background: linear-gradient(90deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
    background-size: 300%;
    border-radius: 30px;
    z-index: 1;
    margin-right: 5px;
}

.buttonPass:hover {
    animation: ani 8s linear infinite;
    border: none;
}

@keyframes ani {
    0% {
        background-position: 0%;
    }

    100% {
        background-position: 400%;
    }
}

.buttonPass:before {
    content: '';
    position: absolute;
    top: -5px;
    left: -5px;
    right: -5px;
    bottom: -5px;
    z-index: -1;
    background: linear-gradient(90deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
    background-size: 400%;
    border-radius: 35px;
    transition: 1s;
}

.buttonPass:hover::before {
    filter: blur(20px);
}

.buttonPass:active {
    background: linear-gradient(32deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
}
</style>
