<template>
    <div class="container">
        <div class="category-buttons">
            <b class="category-title">CATEGORY</b>
            <button class="category-button" @click="filterByDomain('')">전체</button>
            <button class="category-button" @click="filterByDomain('GLOBAL')">자유게시판</button>
            <button class="category-button" @click="filterByDomain('RECIPE')">레시피</button>
            <button class="category-button" @click="filterByDomain('NOTIFICATION')">
                공지사항
            </button>
            <button class="register-button" @click="fnWrite">글쓰기</button>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th id="th-no" width="10%">NO</th>
                        <th id="th-title" width="50%">제목</th>
                        <th id="th-category" width="10%">카테고리</th>
                        <th id="th-writer" width="10%">작성자</th>
                        <th id="th-regist" width="20%">등록일시</th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="row in list"
                        :key="row.boardId"
                        @click="fnView(row.boardId)"
                        class="table-row">
                        <td>{{ row.boardId }}</td>
                        <td>{{ row.title }}</td>
                        <td>{{ row.domain }}</td>
                        <td>{{ row.userName }}</td>
                        <td>{{ row.regDate.substring(0, 19).replace('T', '  ') }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="paging-container">
            <button @click="fnPageFront()" :disabled="paging.page === 1">이전</button>
            <button
                v-for="pageNum in paging.total_page_cnt"
                :key="pageNum"
                @click="fnPage(pageNum)"
                :class="{ active: paging.page === pageNum }">
                {{ pageNum }}
            </button>
            <button @click="fnPageBack()" :disabled="paging.page === paging.total_page_cnt">
                다음
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import router from '@/router';

const list = ref([]);
const paging = ref({
    page: 1,
    total_page_cnt: 1,
    size: 10,
    sort: 'boardId,desc',
});

let domain = '';

onMounted(() => {
    fnGetList();
});

const accessToken = ref(localStorage.getItem('accessToken'));

async function fnGetList() {
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/board/search`, {
            headers: {
                Authorization: `Bearer ${accessToken.value}`,
            },
            params: {
                domain,
                page: paging.value.page - 1,
                size: paging.value.size,
                sort: paging.value.sort,
            },
        });

        paging.value = {
            ...paging.value,
            total_page_cnt: response.data.totalPages,
        };

        list.value = response.data.list;
    } catch (error) {
        console.error('Error fetching list:', error);
        // 여기에 사용자에게 오류 메시지를 표시하는 로직을 추가할 수 있습니다.
    }
}

function fnPage(newPage) {
    if (newPage < 1 || newPage > paging.value.total_page_cnt) return;
    paging.value.page = newPage;
    fnGetList();
    console.log(paging.value.page);
}

function fnPageBack() {
    if (paging.value.page + 1 > paging.value.total_page_cnt) return;
    paging.value.page = paging.value.page + 1;
    fnGetList();
    console.log(paging.value.page);
}

function fnPageFront() {
    if (paging.value.page - 1 < 1) return;
    paging.value.page = paging.value.page - 1;
    fnGetList();
    console.log(paging.value.page);
}

function filterByDomain(value) {
    domain = value;
    paging.value.page = 0;
    fnGetList();
}

function fnWrite() {
    router.push('/board/write');
}

function fnView(boardId) {
    router.push(`/board/${boardId}`);
}
</script>

<style scoped>
.container {
    max-width: 70rem;
    margin: auto;
    padding: 20px;
}

.category-buttons {
    margin-bottom: 20px;
}

.category-title {
    font-size: 1.3rem;
    font-weight: bold;
    margin-right: 2rem;
    margin-left: 2rem;
    color: #6da9e4;
}

.category-button {
    margin: 0 7px;
    padding: 5px 10px;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    transition: color 0.3s ease;
    color: #6da9e4;
    font-weight: bold;
}
.register-button {
    margin: 0 10px;
    padding: 5px 10px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    background-color: #6da9e4;
    transition: background-color 0.3s ease;
}

.category-button:hover {
    color: rgb(32, 135, 175);
}

.register-button:hover {
    background-color: #ddd;
}

.register-button {
    float: right;
    background-color: #6da9e4;
    color: white;
}

.table-container {
    overflow-x: auto;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1rem;
}

th,
td {
    padding: 0.5rem;
    border: 1px solid #ddd;
    text-align: center;
}

th {
    background-color: rgb(238, 245, 248);
}

/* #th-no {
    width: 5rem;
}
#th-title {
    width: 10rem;
}
#th-category {
    width: 5rem;
}
#th-writer {
    width: 5rem;
}
#th-regist {
    width: 5rem;
} */

.table-row:hover {
    background-color: rgb(238, 245, 248);
    cursor: pointer;
}

.paging-container {
    text-align: center;
    margin-top: 20px;
}

.paging-container button {
    margin: 0 0.2rem;
    padding: 5px 10px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    background-color: #6da9e4;
    color: white;
    transition: background-color 0.3s ease;
}

.paging-container button:hover {
    /* background-color: #0056b3; */
    background-color: rgb(86, 163, 194);
}

.paging-container button.active {
    font-weight: bold;
    border: 2px solid #6da9e4;
    background-color: white;
    color: #0056b3;
}
</style>
