<template>
    <v-progress-circular v-if="loading" indeterminate color="amber"></v-progress-circular>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps(['loading']); // Use defineProps to access the props

const loading = ref(false);

// Watch the 'loading' prop
watch(
    () => props.loading,
    (newValue) => {
        loading.value = newValue;
    }
);
</script>

<style scoped>
/* 로딩 스피너의 스타일 */
</style>
