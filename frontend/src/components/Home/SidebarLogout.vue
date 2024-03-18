<template>
    <v-app>
        <v-app-bar :elevation="10" style="background-color: #fffce0">
            <v-container class="text-center">
                <RouterLink :to="{ name: 'StreamingView' }" class="margin">스트리밍</RouterLink>
                <RouterLink :to="{ name: 'StoreSearchView' }" class="margin">음식점</RouterLink>
                <RouterLink :to="{ name: 'BoardView' }" class="margin">게시판</RouterLink>
            </v-container>
        </v-app-bar>

        <!-- Your main content goes here -->
    </v-app>
</template>

<script setup>
// No changes in the script setup section
import { ref } from 'vue';

const sidebar = ref(true);

const toggleSidebar = () => {
    sidebar.value = !sidebar.value;
};
</script>

<style scoped>
.margin {
    padding: 10px;
    padding: 10px;
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
