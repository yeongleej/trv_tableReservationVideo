<template>
    <v-dialog v-model="modal" max-width="400">
        <v-card>
            <v-card-title>인원 수 선택</v-card-title>
            <v-card-text>
                <v-row>
                    <v-col>
                        <v-text-field
                            v-model="selectedCapacity"
                            type="number"
                            label="인원 수"
                            :max="selectedTable.diningTableCapacity"></v-text-field>
                    </v-col>
                </v-row>
            </v-card-text>
            <v-card-actions>
                <v-btn @click="closeModal">취소</v-btn>
                <v-btn @click="saveReservation">확인</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore();
const modal = ref(false);

const selectedTable = ref(null);
const selectedCapacity = ref(0);

const openModal = function (table) {
    selectedTable.value = table;
    selectedCapacity.value = 0; // 초기화
    modal.value = true;
};

const closeModal = function () {
    modal.value = false;
};

const saveReservation = function () {
    // 예약 정보 저장 로직을 추가할 수 있습니다.
    store.setSelectedTable(selectedTable.value);
    store.setSelectedCapacity(selectedCapacity.value);
    closeModal();
};
</script>
