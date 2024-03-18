<template>
    <div>
        <v-card class="mx-auto card-position" max-width="400" height="500">
            <v-card-text>
                <RouterLink style="padding-left: 20px; display: flex" :to="{ name: 'HomeView' }">
                    <div style="display: flex">
                        <img
                            style="width: 60px; height: 60px; margin-top: 25px"
                            src="@/img/logo.png"
                            alt="logo" />
                        <div style="margin-top: 40px; margin-left: 10px; display: block">
                            TRV
                            <div style="font-size: small; text-decoration: none">
                                Table Reservation with Video
                            </div>
                        </div>
                    </div>
                </RouterLink>
                <h4 class="text--primary text-position">
                    <p>안녕하세요.</p>
                    <p>테레비 서비스입니다.</p>
                    소셜로그인으로 로그인을 진행해주세요.
                </h4>
            </v-card-text>
            <v-card-actions>
                <div>
                    <button @click="snsLogin('kakao')">
                        <img src="@/img/kakaoL.png" alt="#" class="img-radius" />
                        <h5>카카오</h5>
                    </button>
                </div>
                <div>
                    <button @click="snsLogin('naver')">
                        <img src="@/img/naver.png" alt="#" class="img-radius" />
                        <h5>네이버</h5>
                    </button>
                </div>
                <div>
                    <button @click="snsLogin('google')">
                        <img src="@/img/google.png" alt="#" class="img-radius" />
                        <h5>Google</h5>
                    </button>
                </div>
            </v-card-actions>
        </v-card>
    </div>
</template>
<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import router from '@/router';

const store = useCounterStore();
const connectData = ref('');
const accessToken = localStorage.getItem('accessToken');
const refreshToken = localStorage.getItem('refreshToken');

const snsLogin = (type) => {
    const redirect_uri = window.location.origin + '';
    window.location.href = `${store.API_URL}/api/user/login/${type}?redirect_uri=${redirect_uri}/`;
};

const jwttest = function () {
    const atk = `Bearer ${accessToken}`;
    axios({
        method: 'get',
        url: '/api/user/jwt-test',
        headers: {
            Authorization: atk,
        },
    })
        .then((res) => {
            connectData.value = res.data;
        })
        .catch((err) => console.log(err));
};

// const jwttest = async () => {
//     try {
//         const atk = `Bearer ${accessToken}`;
//         console.log(atk);
//         const response = await axios.get('/api/user/jwt-test', {
//             headers: {
//                 Authorization: atk,
//             },
//         });
//         connectData.value = response.data;
//         console.log(connectData.value);
//     } catch (error) {
//         console.error(error);
//     }
// };
</script>
<style scoped>
* {
    font-family: 'SejonghospitalBold' !important;
}
.card-position {
    margin-top: 50px;
    background-color: #fffce0;
    box-shadow: 1px 1px 10px rgba(0, 0, 0, 0.1); /* Subtle box shadow added for depth */
}

.text-position {
    margin-top: 50px;
    margin-bottom: 50px;
}

div:hover {
    transition: 0.5s;
    transform: scale(1.02); /* 10% 확대 */
    transition: transform 0.3s ease-in-out;
}
button:hover {
    background-color: rgb(184, 181, 181);
    transition: 0.7s;
    transform: scale(1.1); /* 10% 확대 */
    transition: transform 0.3s ease-in-out;
}

.img-radius {
    border-radius: 50px;
    display: flex;
    margin-top: 10px;
    margin-left: 35px;
    margin-right: 35px;
    width: 60px;
    height: 60px;
}

/* Additional styling for text below logos */
button div {
    text-align: center;
    margin-top: 5px;
    font-size: small;
}
</style>
