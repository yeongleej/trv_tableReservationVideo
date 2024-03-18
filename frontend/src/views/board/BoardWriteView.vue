<template>
    <form @submit.prevent="submitForm" class="form-container">
        <div class="form-group">
            <label for="domain">Domain:</label>
            <!-- <input type="text" id="domain" v-model="board.domain" class="form-control" /> -->
            <!-- <select class="select-box" v-model="board.domain">
                <option disabled value="">다음 중 하나를 선택하세요</option>
                <option>GENERAL</option>
                <option>RECIPE</option>
                <option>NOTIFICATION</option>
            </select> -->
            <v-select
                v-model="board.domain"
                :items="['GENERAL', 'RECIPE', 'NOTIFICATION']"
                variant="underlined"></v-select>
        </div>

        <div class="form-group">
            <label for="useStoreId">Use Store ID:</label>
            <input type="checkbox" id="useStoreId" v-model="board.useStoreId" class="checkbox" />
        </div>

        <div class="form-group">
            <label for="title">Title:</label>
            <input type="text" id="title" v-model="board.title" class="form-control" />
        </div>

        <div class="form-group">
            <label for="content">Content:</label>
            <textarea id="content" v-model="board.content" class="form-control"></textarea>
        </div>

        <div class="form-group">
            <label for="files">Files:</label>
            <input
                type="file"
                id="files"
                ref="files"
                multiple
                @change="handleFileUpload($event)"
                class="file-input" />
        </div>

        <button type="submit" class="submit-btn">Submit</button>
    </form>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import router from '@/router';
const accessToken = localStorage.getItem('accessToken');
const boardDomainList = ['GENERAL', 'RECIPE', 'NOTIFICATION'];
const board = ref({
    domain: 'GENERAL',
    useStoreId: true,
    title: '새로운 식당 리뷰',
    content: '여기 식당 정말 좋았어요! 추천합니다.',
});

const files = ref([]);

const handleFileUpload = (event) => {
    files.value = event.target.files;
};

const submitForm = () => {
    const formData = new FormData();
    formData.append('domain', board.value.domain);
    formData.append('useStoreId', board.value.useStoreId);
    formData.append('title', board.value.title);
    formData.append('content', board.value.content);

    if (files.value.length > 0) {
        for (let i = 0; i < files.value.length; i++) {
            formData.append('upfile', files.value[i]);
        }
    }

    axios
        .post(`${import.meta.env.VITE_API_URL}/api/board`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: `Bearer ${accessToken}`,
            },
        })
        .then((response) => {
            console.log(response.data);
        })
        .catch((error) => {
            console.error(error);
        });

    console.log('Form submitted');
    alert('글 작성이 완료되었습니다!');
    router.push({ name: 'BoardView' });
};
</script>

<style scoped>
.form-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    background: #fff;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
}

.form-control {
    width: 100%;
    padding: 10px;
    border-radius: 4px;
    border: 1px solid #ccc;
    font-size: 16px;
}

.checkbox {
    margin-top: 5px;
}

.file-input {
    font-size: 16px;
}

.submit-btn {
    background-color: #007bff;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
}

.submit-btn:hover {
    background-color: #0056b3;
}
</style>
