<template>
    <div class="reservation-system">
        <div class="flex-container">
            <div>
                <v-select
                    v-model="selectedCategory"
                    :items="categoryOptions"
                    style="margin-top: 1rem"
                    label="시스템 설정 선택" />
                <div class="selected-component-description">
                    <!-- Add description for the selected category -->
                    <div v-if="selectedCategory === '가게구조 설정'" style="margin: 10px">
                        1. 테이블 선택하기를 클릭하여 원하는 구조를 생성합니다.
                        <p>2. 테이블을 클릭하여 가게구조에 맞게 옮겨놓습니다.</p>
                        <p>3. 가게구조에 맞게 만드셨다면 저장하기를 클릭하여 설정을 저장하세요.</p>
                        <p>- 테이블 구조 삭제를 원하시면 더블클릭하시면 됩니다.</p>
                        <p>- 룸은 4인기준, 단체석은 8인기준입니다.</p>
                    </div>
                    <div v-if="selectedCategory === '예약일시 설정'" style="margin: 10px">
                        1. 원하시는 예약 일수 및 시간의 선택이 가능한 사업자 예약일시 설정
                        페이지입니다.
                        <p>2. 사용자에게는 사업자가 설정한 date만 예약이 가능합니다.</p>
                        <p>3. 예약이 가능한 원하시는 일과 시간을 선택하세요.</p>
                        <p>4. 예약일시 설정이 완료되면 저장하기를 클릭해 설정을 저장하세요.</p>
                        <p>- 자리패스 구매유저는 사업자의 예약일보다 늘어날 수 있습니다.</p>
                    </div>
                </div>
            </div>
            <div class="selected-component-container">
                <component :is="getComponent(selectedCategory)" />
            </div>
        </div>
    </div>
</template>

<script setup>
import Calendar from '@/components/Owner/Calendar.vue';
import TableReservation from '@/components/Owner/TableReservation.vue';
import { ref } from 'vue';

const categoryOptions = ['예약일시 설정', '가게구조 설정'];
const selectedCategory = ref(categoryOptions[0]);

const getComponent = (category) => {
    switch (category) {
        case '예약일시 설정':
            return Calendar;
        case '가게구조 설정':
            return TableReservation;
        default:
            return null;
    }
};
</script>

<style scoped>
.reservation-system {
    width: 1200px;
    margin: 0 auto;
}

.flex-container {
    display: flex;
    justify-content: space-between;
    margin-left: 7rem;
}

.selected-component-container {
    margin-left: 30px;
    width: 80rem; /* Adjust the width as needed */
}

.selected-component-description {
    margin-top: 20px;
}
</style>
