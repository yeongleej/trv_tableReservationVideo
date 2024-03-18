<template>
    <div
        id="app"
        style="
            width: 100%;
            height: 100%;
            margin-left: auto;
            margin-right: auto;
            background-color: #fffce0;
            font-family: Verdana, Geneva, Tahoma, sans-serif;
        ">
        <!-- 로딩 스피너 -->
        <loading-spinner :loading="loading"></loading-spinner>
        <v-layout
            style="
                margin: auto;
                font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
            ">
            <Navbar />
            <div style="width: 100%; margin-left: auto; margin-right: auto">
                <v-main>
                    <RouterView style="margin-left: auto; margin-right: auto; width: 90%" />
                    <Footer style="margin-top: 100px" />
                </v-main>
            </div>
        </v-layout>
    </div>
</template>

<script setup>
import { RouterView } from 'vue-router';
import axios from 'axios';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import { ref } from 'vue';
import { useCounterStore } from './stores/counter';
import Navbar from './components/Home/Navbar.vue';
import SidebarLogout from './components/Home/SidebarLogout.vue';
import SidebarLogin from './components/Home/SidebarLogin.vue';
import Footer from './components/Home/Footer.vue';

const store = useCounterStore();
const API_URL = import.meta.env.VITE_API_URL;
const loading = ref(false);

const accessToken = localStorage.getItem('accessToken');
</script>

<style scoped>
@font-face {
    font-family: 'SejonghospitalBold';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2312-1@1.1/SejonghospitalBold.woff2')
        format('woff2');
    font-weight: 300;
    font-style: normal;
}
* {
    font-family: 'SejonghospitalBold' !important;
}
</style>
