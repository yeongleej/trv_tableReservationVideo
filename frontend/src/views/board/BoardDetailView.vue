<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" md="8">
                <v-card v-if="boardData" class="mb-4">
                    <v-card-title class="text-h5">{{ boardData.title }}</v-card-title>
                    <v-card-subtitle
                        >{{ boardData.userName }} |
                        {{ formatDate(boardData.regDate) }}</v-card-subtitle
                    >
                    <v-card-text>{{ boardData.content }}</v-card-text>
                    <v-container>
                        <v-row>
                            <v-col
                                v-for="(image, index) in boardData.images"
                                :key="index"
                                cols="12"
                                sm="6">
                                <v-img :src="image" aspect-ratio="1.5" contain></v-img>
                            </v-col>
                        </v-row>
                    </v-container>
                    <v-card-actions>
                        <v-btn color="primary" @click="backToList">목록으로 돌아가기</v-btn>
                    </v-card-actions>
                </v-card>
                <v-progress-circular v-else indeterminate></v-progress-circular>
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
const store = useCounterStore();
const route = useRoute();
const router = useRouter();
const boardData = ref(null);
const accessToken = localStorage.getItem('accessToken');
const boardId = route.params.boardId;

onMounted(() => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/board/${boardId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            boardData.value = res.data;
        })
        .catch((err) => {
            console.log('Error fetching board details: ', err);
        });
});

// onMounted(async () => {
//     try {
//         const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/board/${boardId}`);
//         boardData.value = response.data;
//     } catch (error) {
//         console.error('Error fetching board details:', error);
//     }
// });

const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
};

const backToList = () => {
    router.push('/board');
};
</script>
