<template>
    <v-card class="mx-auto card-position" max-width="400" height="500">
        <v-card-text>
            <p class="text-h4 text--primary">자리요에 방문해주셔서 감사합니다.</p>
            <div class="text--primary text-position">카카오로 로그인을 진행해주세요.</div>
        </v-card-text>
        <v-card-actions style="justify-content: center">
            <button><img src="../img/kakao.png" alt="login" /></button>
        </v-card-actions>
    </v-card>
</template>

<script setup></script>

<style scoped>
.card-position {
    margin-top: 50px;
}
.text-position {
    margin-top: 50px;
    margin-bottom: 50px;
}
button:hover {
    background-color: white;
    transition: 0.7s;
}
</style>
