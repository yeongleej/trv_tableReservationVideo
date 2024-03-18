<template>
    <v-app>
        <v-app-bar :height="60" :elevation="0" style="background-color: #fffce0">
            <v-container class="text-center">
                <RouterLink :to="{ name: 'StreamingView' }" class="margin">스트리밍</RouterLink>
                <RouterLink :to="{ name: 'StoreSearchView' }" class="margin">음식점</RouterLink>
                <RouterLink :to="{ name: 'BoardView' }" class="margin">게시판</RouterLink>
                <RouterLink :to="{ name: 'CustomerReservationView' }" class="margin"
                    >예약관리</RouterLink
                >
                <!-- <RouterLink :to="{ name: 'OwnerReservationView' }" class="margin"
                    >가게 관리</RouterLink
                > -->
                <RouterLink
                    v-if="userRole == 'OWNER'"
                    :to="{ name: 'OwnerReservationView' }"
                    class="margin"
                    >가게 관리</RouterLink
                >
                <RouterLink
                    v-if="userRole == 'OWNER'"
                    :to="{ name: 'OwnerStreamingView' }"
                    class="margin"
                    >가게 스트리밍</RouterLink
                >
                <RouterLink :to="{ name: 'MypageView' }" class="margin">마이페이지</RouterLink>
            </v-container>
        </v-app-bar>

        <!-- Your main content goes here -->
    </v-app>
</template>

<script setup>
// No changes in the script setup section
import { ref } from 'vue';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();
const userRole = ref(store.userRole);

const sidebar = ref(true);

const toggleSidebar = () => {
    sidebar.value = !sidebar.value;
};
</script>

<style scoped>
.margin {
    padding: 15px;
    padding: 15px;
    text-decoration: none;
    transition: 0.1s;
}
.margin:hover {
    background-color: antiquewhite;
    transition: 0.5s;
    transform: scale(1.02); /* 10% 확대 */
    transition: transform 0.3s ease-in-out;
}
.v-app-bar {
    text-align: center;
}
</style>
