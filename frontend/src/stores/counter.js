import { ref, computed, onMounted } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';
import router from '@/router';

export const useCounterStore = defineStore(
    'counter',
    () => {
        const API_URL = import.meta.env.VITE_API_URL;
        const dataGet = ref([]);

        const remainingTime = ref(0);
        const accessToken = ref(localStorage.getItem('accessToken'));
        const token = ref(accessToken.value ? accessToken.value.replace('Bearer', '') : null);
        const LoginData = ref('');
        const storeX = ref(null);
        const storeY = ref(null);
        const storeAddress = ref('');
        const updateRemainingTime = function (time) {
            remainingTime.value = time;
        };
        const getstore = function () {
            axios({
                method: 'get',
                url: `/store/${storeId}`,
            })
                .then((res) => {
                    getstore.value = res.data;
                })
                .catch((err) => {
                    console.log(err);
                });
        };
        const getReservation = function () {
            axios({
                method: 'get',
                url: `${API_URL}/api/reservation/search/available/store/{storeId}/date/{date}/`,
            })
                .then((res) => {
                    getReservation.value = res.data;
                })
                .catch((err) => {
                    console.log(err);
                });
        };
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
                    LoginData.value = decodedData;
                } catch (error) {
                    console.error('Error decoding JWT:', error);
                }
        };
        const userData = computed(() => {
            decodeJWT();
            return LoginData.value;
        });
        const userId = userData.value.userId;
        const userNickname = userData.value.nickname;
        const userEmail = userData.value.email;
        const userRole = userData.value.role;
        const storeId = userData.value.storeId;

        const reqDate = ref(null);
        const selectedTime = ref(null);
        const aList = ref([]);

        const saveSelectedTime = (time) => {
            selectedTime.value = time;
        };
        const saveSelectedDate = (date) => {
            reqDate.value = date;
        };
        const selectedTable = ref(null);
        const reservationUserCount = ref('');

        const setSelectedTable = (table) => {
            selectedTable.value = table;
        };
        const setSelectedCapacity = (capacity) => {
            reservationUserCount.value = capacity;
        };
        const resetSelection = () => {
            selectedTable.value = null;
            reservationUserCount.value = 0;
        };
        const countTableNum = ref(0);
        const zPass = ref(null);
        const zpassStatus = ref(null);
        return {
            API_URL,
            dataGet,
            remainingTime,
            accessToken,
            userData,
            LoginData,
            token,
            userId,
            userNickname,
            userEmail,
            userRole,
            storeId,
            storeX,
            storeY,
            storeAddress,
            reqDate,
            selectedTime,
            selectedTable,
            reservationUserCount,
            countTableNum,
            zPass,
            zpassStatus,
            aList,
            setSelectedTable,
            setSelectedCapacity,
            resetSelection,
            saveSelectedTime,
            saveSelectedDate,
            decodeJWT,
            updateRemainingTime,
            getstore,
            getReservation,
        };
    },
    { persist: true }
);
