<template>
    <div style="margin: 50px">
        <h2 style="margin-top: 30px">주변 장소 검색하기</h2>
        <form @submit.prevent="searchLocation" style="margin-top: 20px">
            <p style="margin: 30px">
                <label for="place">장소 검색 : </label>
                <input v-model="place" id="place" style="margin-left: 10px" />
                <button type="submit">검색</button>
            </p>
        </form>
        <hr />
        <div style="margin: 30px; display: flex; flex-wrap: wrap">
            <div id="map" style="width: 600px; height: 450px; flex: 8" class="box"></div>

            <!-- Search results on the right side -->
            <div class="results-container" style="flex: 4; margin-left: 10px">
                <h4 style="margin: 10px">검색 결과 리스트</h4>
                <ul v-if="displayedResults.length > 0" class="results-list">
                    <li
                        v-for="result in displayedResults"
                        :key="result.place_name"
                        class="result-card">
                        <div>
                            <a @click="toggleInfoWindow(result)">{{ result.place_name }}</a>
                            <div
                                v-if="
                                    selectedPlace && selectedPlace.place_name === result.place_name
                                ">
                                <strong>
                                    주소: {{ result.address_name }}<br />
                                    전화번호: {{ result.phone || '정보 없음' }} </strong
                                ><br />
                            </div>
                        </div>
                    </li>
                </ul>
                <p v-else class="no-results-message" style="margin-left: 15px">
                    검색 결과가 없습니다.
                </p>
            </div>
        </div>
        <div class="pagination">
            <button @click="prevPage" :disabled="currentPage === 1">이전</button>
            <span>{{ currentPage }}</span>
            <button @click="nextPage" :disabled="currentPage === totalPages">다음</button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';

let container;
let map;
let placesService;
let markers = [];

const city = ref('');
const district = ref('');
const place = ref('');
const searchResults = ref([]);
const selectedPlace = ref(null);

let script = document.createElement('script');
script.type = 'text/javascript';
script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=771bcadc181290207b7566c124585021&libraries=services,clusterer,drawing&autoload=false`;
document.head.appendChild(script);

script.onload = function () {
    kakao.maps.load(function () {
        container = document.getElementById('map');
        initializeMap();
        placesService = new kakao.maps.services.Places();
    });
};

function initializeMap() {
    if (!container) {
        setTimeout(initializeMap, 100);
        return;
    }

    let options = {
        center: new kakao.maps.LatLng(36.3553675622378, 127.298408300646),
        level: 4,
    };

    map = new kakao.maps.Map(container, options);
}

function searchLocation() {
    removeAllMarkers();

    if (!place.value.trim()) {
        return;
    }

    const searchKeyword = [city.value, district.value, place.value].filter(Boolean).join(' ');
    placesService.keywordSearch(searchKeyword, (data, status, pagination) => {
        if (status === kakao.maps.services.Status.OK) {
            searchResults.value = data;

            let bounds = new kakao.maps.LatLngBounds();

            for (let i = 0; i < searchResults.value.length; i++) {
                createMarker(searchResults.value[i]);
                bounds.extend(
                    new kakao.maps.LatLng(searchResults.value[i].y, searchResults.value[i].x)
                );
            }

            map.setBounds(bounds);
            currentPage.value = 1;
        }
    });
}
const displayedResults = computed(() => {
    const startIndex = (currentPage.value - 1) * resultsPerPage;
    const endIndex = startIndex + resultsPerPage;

    // Add additional check for null or undefined
    return searchResults.value && searchResults.value.slice
        ? searchResults.value.slice(startIndex, endIndex)
        : [];
});
function createMarker(place) {
    let marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(place.y, place.x),
        map: map,
    });

    markers.push(marker);

    kakao.maps.event.addListener(marker, 'click', function () {
        toggleInfoWindow(place);
    });
}

function removeAllMarkers() {
    if (markers && markers.length > 0) {
        for (let i = 0; i < markers.length; i++) {
            markers[i].setMap(null);
        }
        markers = [];
    }
}

function toggleInfoWindow(place) {
    if (window.infoWindow) {
        closeInfoWindow();
    } else {
        showInfoWindow(place);
        selectedPlace.value = place;
    }
}

function showInfoWindow(place) {
    const content = `
      <div style="width:250px">
        <strong>${place.place_name}</strong><br>
        주소: ${place.address_name}<br>
        전화번호: ${place.phone || '정보 없음'}
      </div>
    `;

    const infoWindow = new kakao.maps.InfoWindow({
        content: content,
        position: new kakao.maps.LatLng(place.y, place.x),
    });

    window.infoWindow = infoWindow;

    kakao.maps.event.addListener(infoWindow, 'closeclick', function () {
        closeInfoWindow();
    });

    infoWindow.open(map);

    function closeInfoWindowOnMapClick() {
        if (window.infoWindow) {
            closeInfoWindow();
        }
    }

    kakao.maps.event.addListener(map, 'click', closeInfoWindowOnMapClick);
}

function closeInfoWindow() {
    if (window.infoWindow) {
        window.infoWindow.close();
        window.infoWindow = null;
    }
}

const resultsPerPage = 8;
const currentPage = ref(1);
const totalPages = computed(() => Math.ceil(searchResults.length / resultsPerPage));

function nextPage() {
    if (currentPage.value < totalPages.value) {
        currentPage.value++;
    }
}

function prevPage() {
    if (currentPage.value > 1) {
        currentPage.value--;
    }
}
</script>

<style scoped>
.results-container {
    margin-left: 10px;
    display: flex;
    flex-direction: column;
}

.results-list {
    list-style-type: none;
    padding: 0;
}

.result-card {
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 10px;
    background-color: #fff;
    transition: background-color 0.3s;
}

.result-card:hover {
    background-color: #f9f9f9;
}

.pagination {
    margin-top: 15px;
    display: flex;
    align-items: center;
}

.pagination button {
    margin: 0 5px;
}
</style>
