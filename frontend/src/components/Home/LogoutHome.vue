<template>
    <div style="text-align: center; width: 90%; margin: auto">
        <v-carousel
            :continuous="true"
            :cycle="true"
            :show-arrows="false"
            :interval="5000"
            :hide-delimiters="true"
            style="width: 100%; height: 700px">
            <v-carousel-item>
                <img src="@/img/mainback1.png" alt="Page 1" style="width: 100%" />
            </v-carousel-item>

            <v-carousel-item>
                <img
                    src="@/img/mainback2.png"
                    alt="Page 2"
                    style="width: 100%; margin-bottom: 60px" />
            </v-carousel-item>

            <v-carousel-item>
                <img
                    src="@/img/mainback3.png"
                    alt="Page 3"
                    style="width: 100%; margin-bottom: 60px" />
            </v-carousel-item>
        </v-carousel>
    </div>
</template>

<script setup>
import { ref } from 'vue';

const animationClass = ref('');
const isHovering = ref(false);

const startAnimation = () => {
    animationClass.value = 'animate-card';
    isHovering.value = true; // Hover 상태 감지
};

const stopAnimation = () => {
    animationClass.value = '';
    isHovering.value = false; // Hover 해제 상태 감지
};
</script>

<style scoped>
.grid-container-card {
    display: grid;
    width: 90%;
    grid-template-columns: repeat(3, 1fr); /* Use repeat for shorthand */
    gap: 30px 30px;
    margin: 20px;
    margin-left: auto;
    margin-right: auto;
}

.custom-card {
    position: relative;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.3s ease-out;
}

.animate-card {
    transform: scale(1.05);
}

.card-title,
.card-subtitle,
.card-text {
    text-align: center;
    position: absolute;
    top: 82%;
    left: 50%;
    transform: translate(-50%, -50%);
    margin: 0;
    color: rgb(255, 255, 255);
}

.card-title {
    font-size: 1.5em;
}

.card-subtitle {
    top: 70%;
}

.card-text {
    top: 90%;
}

.custom-card:hover {
    transform: scale(1.05);
}

.custom-card:hover .card-title {
    font-size: 1.8em;
}

.card-image {
    width: 100%;
    height: auto;
    transition: opacity 0.3s ease-out;
}

.custom-card:hover .card-image {
    opacity: 0.8;
}
</style>
