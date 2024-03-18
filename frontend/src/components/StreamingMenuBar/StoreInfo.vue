<template>
    <hr />
    <div class="container">
        <!-- <div class="image-container">
            <img :src="storeImage" alt="storeImg" />
        </div> -->
        <div class="card">
            <img class="card-image" :src="storeImage" alt="storeImg" />
            <!-- <div class="card-image">
            </div> -->
            <!-- <div class="category">Illustration</div>
            <div class="heading">
                A heading that must span over two lines
                <div class="author">By <span class="name">Abi</span> 4 days ago</div>
            </div> -->
        </div>
        <div class="v-card-container">
            <v-card>
                <v-card-title class="v-card-title">
                    {{ storeName }}
                </v-card-title>
                <v-card-subtitle class="v-card-subtitle">
                    {{ storeDetail }}
                </v-card-subtitle>
                <div class="text-align">
                    <li>영업일 : {{ storeOperationDates }}</li>
                </div>
                <div class="text-align">
                    <li>운영 시간 : {{ storeOperationHours }}</li>
                </div>
                <div class="text-align">
                    <li>주소 : {{ storeAddress }}</li>
                </div>
                <div class="text-align">
                    <li>가게번호 : {{ storePhone }}</li>
                </div>
            </v-card>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue';
import { useCounterStore } from '@/stores/counter';
import axios from 'axios';

onMounted(async () => {
    console.log('여기');
    await getStoreInfo();
});

const store = useCounterStore();

const { storeId } = defineProps({
    storeId: String,
});
const accessToken = localStorage.getItem('accessToken');

const storeName = ref('');
const storeDetail = ref('');
const storeImage = ref('');
const storeOperationDates = ref('');
const storeOperationHours = ref('');
const storeAddress = ref('');
const storePhone = ref('');

const getStoreInfo = async () => {
    await axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    }).then((res) => {
        console.log(res.data);
        storeName.value = res.data.storeName;
        storeDetail.value = res.data.storeDetail;
        storeImage.value = res.data.images[0];
        storeOperationDates.value = res.data.storeOperationDates;
        storeOperationHours.value = res.data.storeOperationHours;
        storeAddress.value = res.data.storeAddress;
        storePhone.value = res.data.storePhone;
    });
};
</script>

<style scoped>
.container {
    display: flex;
    width: calc(100% + 10px);
    margin-right: -10px;
    justify-content: space-evenly;
}

.image-container {
    flex: 5;
    margin-right: 10px;
    width: 100%;
}

.image-container img {
    width: 20rem;
    /* width: 100%; */
}

.v-card-container {
    flex: 5;
    margin-left: 10px;
    max-width: 100%;
    max-height: 100%;
}

.v-card-title {
    margin-top: 25px;
    margin-bottom: 25px;
    font-size: 31px;
}

.v-card-subtitle {
    font-size: 13px;
    margin-bottom: 25px;
}

.text-align {
    width: 100%;
    margin: 10px;
    padding-left: 12px;
}

/* Image styling */
img {
    max-width: 100%;
    border-radius: 12px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* Card styling */
.v-card {
    border-radius: 12px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    height: 100%;
}

.card {
    width: 250px;
    background: white;
    padding: 0.4em;
    border-radius: 6px;
    display: flex;
    justify-content: center;
}

.card-image {
    background-color: rgb(236, 236, 236);
    width: 100%;
    height: 100%;
    border-radius: 6px 6px 0 0;
}

.card-image:hover {
    transform: scale(0.98);
}

/* .chip {
    text-align: center;
    margin-left: auto;
    margin-right: auto;
    margin-top: 3px;
    padding: 10px;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.chip:hover {
    background-color: #f0f0f0;
}

.chip.active {
    background-color: #4caf50;
    color: white;
} */

/* Card content styling */
/* .v-card-content {
    display: grid;
    align-items: center;
    justify-items: center;
    gap: 15px;
} */

/* Button styling */
/* .v-btn {
    background-color: #4caf50;
    color: white;
    border-radius: 8px;
    padding: 8px 16px;
    cursor: pointer;
    transition: background-color 0.3s;
} */

/* Hover effect for buttons */
/* .v-btn:hover {
    background-color: #45a049;
} */

/* Container styling */
/* .container {
    display: grid;
    height: 100%;
} */
</style>
