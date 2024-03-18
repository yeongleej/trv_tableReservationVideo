<template>
    <div class="time-selection">
        <h3>예약시간 설정</h3>
        <div class="time-row" v-for="row in timeRows" :key="row[0]">
            <div v-for="time in row" :key="time" @click="selectTime(time)">
                <label class="checkbox-label">
                    <input type="checkbox" :value="time" v-model="selectedTimes" />
                    {{ time }}
                </label>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();

const availableTimes = ref([
    '00:00',
    '01:00',
    '02:00',
    '03:00',
    '04:00',
    '05:00',
    '06:00',
    '07:00',
    '08:00',
    '09:00',
    '10:00',
    '11:00',
    '12:00',
    '13:00',
    '14:00',
    '15:00',
    '16:00',
    '17:00',
    '18:00',
    '19:00',
    '20:00',
    '21:00',
    '22:00',
    '23:00',
]);

const selectedTimes = ref([]);

const selectTime = (time) => {
    // You can add logic here to handle the selection if needed
};

const timeRows = ref(
    Array.from({ length: availableTimes.value.length / 2 }, (_, i) =>
        availableTimes.value.slice(i * 6, i * 6 + 6)
    )
);
</script>

<style scoped>
.time-row {
    display: flex;
    gap: 5px;
    padding-top: 5px;
    padding: 5px;
}

.time-selection div {
    flex: 1;
}

.checkbox-label {
    display: flex;
    align-items: center;
}

.checkbox-label input {
    margin-right: 5px;
}
</style>
