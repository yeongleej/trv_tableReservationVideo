<template>
    <div id="app">
        <div class="sections-container">
            <div
                v-for="(section, index) in sections"
                :key="index"
                class="section"
                :class="{ active: activeSection === section }"
                @click="toggleSection(section)">
                <h4>{{ section }}</h4>
            </div>
        </div>
        <div v-if="activeSection" class="section-details">
            <component :is="getComponent(activeSection)" />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import StoreInfo from '@/components/Owner/StoreInfo.vue';
import ReservationInfo from '@/components/Owner/ReservationInfo.vue';
import ReservationHistory from '@/components/Owner/ReservationHistory.vue';

const sections = ref(['음식점 정보관리', '예약시스템 관리', '예약내역관리']);
const activeSection = ref('예약시스템 관리');

const toggleSection = (section) => {
    if (activeSection.value !== section) {
        activeSection.value = section;
    }
};

const getComponent = (section) => {
    switch (section) {
        case '음식점 정보관리':
            return StoreInfo;
        case '예약시스템 관리':
            return ReservationInfo;
        case '예약내역관리':
            return ReservationHistory;
        default:
            return null;
    }
};
</script>

<style scoped>
* {
    font-family: 'SejonghospitalBold' !important;
}
.sections-container {
    width: 90%;
    display: flex;
}

.section {
    width: 20%;
    height: 30px;
    border: 1px solid #ddd;
    margin-left: auto;
    margin-top: auto;
    padding: 10px;
    cursor: pointer;
    transition: all 0.3s;
    display: flex;
    justify-content: center;
    align-items: center;
}

.section:hover {
    background-color: bisque;
    transform: scale(1.02);
}

.active {
    z-index: 1;
}

.section-details {
    margin-top: 10px;
    margin-left: 30px;
    padding: 10px;
    font-size: large;
    font-weight: bold;
}
.margin {
    margin-left: auto;
    margin-right: auto;
}
</style>
