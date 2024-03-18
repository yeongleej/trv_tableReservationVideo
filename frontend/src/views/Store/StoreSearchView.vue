<template>
    <div id="app" style="text-align: center">
        <h2 style="text-align: center">음식점 검색 페이지</h2>

        <div class="custom-map" style="width: 100%">
            <div class="store-search" style="width: 25%; margin: 0.3rem">
                <input
                    v-model="searchKeyword"
                    placeholder="음식점 검색어"
                    @keyup.enter="searchNearbyPlaces"
                    class="map-input"
                    style="display: inline" />

                <button class="map-btn" style="display: inline" @click="searchNearbyPlaces">
                    검색
                </button>

                <div class="card-list">
                    <v-virtual-scroll class="scroll" :height="430" :items="searchResults">
                        <template v-slot:default="{ item }">
                            <button style="width: 90%" @click="showMarkerInfo(item)">
                                <div
                                    class="search-card-body"
                                    :class="{ 'is-expanded': selectedItem === item }">
                                    <h5 class="search-card-title">{{ item.place_name }}</h5>
                                    <p class="search-card-text">{{ item.address_name }}</p>
                                    <!-- 선택된 아이템의 상세 정보 표시 -->
                                    <div v-if="selectedItem === item" class="detail-info">
                                        <p>전화 번호: {{ item.phone }}</p>

                                        <button
                                            v-if="item.regist"
                                            @click="goToStorePage(item)"
                                            class="store-page-btn">
                                            가게 페이지로 이동
                                        </button>
                                    </div>
                                </div>
                            </button>
                        </template>
                    </v-virtual-scroll>
                </div>
            </div>
            <div style="width: 75%; padding: 0.3rem">
                <v-container id="map"></v-container>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const API_URL = import.meta.env.VITE_API_URL;

const router = useRouter();

const searchKeyword = ref('');
const searchResults = ref([]);
const map = ref(null);
const placesService = ref(null);
const markers = ref([]);
const itemsPerPage = ref(15);
const totalPages = ref(0);
const selectedItem = ref(null);

const initMap = () => {
    const container = document.getElementById('map');
    const options = {
        center: new kakao.maps.LatLng(36.3553675622378, 127.298408300646),
        level: 14,
    };
    map.value = new kakao.maps.Map(container, options);
    placesService.value = new kakao.maps.services.Places();

    const zoomControl = new kakao.maps.ZoomControl();
    map.value.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

    const mapTypeControl = new kakao.maps.MapTypeControl();
    map.value.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);
};

const searchNearbyPlaces = async () => {
    const center = map.value.getCenter();
    const bounds = map.value.getBounds();
    const ne = bounds.getNorthEast();
    const sw = bounds.getSouthWest();

    console.log(center, bounds);

    removeMarkers();
    searchResults.value = [];

    const addressSet = new Set();

    const { data: backendData } = await axios.get(`${API_URL}/api/store/search`, {
        params: { keyword: searchKeyword.value },
    });

    backendData.forEach((item) => {
        const transformedItem = transformStoreToPlace(item);

        if (!addressSet.has(transformedItem.address_name)) {
            addressSet.add(transformedItem.address_name);
            searchResults.value.push(transformedItem);
        }
    });

    const callback = (data, status, pagination) => {
        if (status === kakao.maps.services.Status.OK) {
            // 검색 결과 처리
            data.forEach((item) => {
                if (!addressSet.has(item.address_name)) {
                    addressSet.add(item.address_name);
                    searchResults.value.push(item);
                }
            });

            console.log(searchResults);

            // 전체 페이지 개수 반영
            totalPages.value = pagination.last;
            if (pagination.hasNextPage) {
                pagination.nextPage();
            } else {
                displaySearchResults();
            }
        } else {
            console.error('검색에 실패했습니다. 상태 코드:', status);
            displaySearchResults();
        }
    };

    placesService.value.keywordSearch(searchKeyword.value, callback, {
        location: center,
        bounds: new kakao.maps.LatLngBounds(sw, ne),
        size: itemsPerPage.value,
    });
};

const goToStorePage = (store) => {
    router.push(`/streaming/${store.id}`);
};

function transformStoreToPlace(store) {
    return {
        id: store.id,
        address_name: store.storeAddress,
        category_group_code: '', // 상점 카테고리 그룹 코드가 명시되지 않았으므로 빈 문자열로 설정
        category_group_name: '', // 상점 카테고리 그룹 이름이 명시되지 않았으므로 빈 문자열로 설정
        category_name: '부동산 > 빌딩', // 예제에서는 고정 값으로 설정, 실제 상황에 맞게 조정 필요
        distance: '', // 거리는 주어진 객체에 없으므로 빈 문자열로 설정
        id: store.storeId.toString(), // ID를 문자열로 변환
        phone: store.storePhone || '', // 전화번호가 없는 경우 빈 문자열로 처리
        place_name: store.storeName,
        place_url: '', // 장소 URL은 주어진 객체에 없으므로 빈 문자열로 설정
        road_address_name: store.storeAddress, // 도로명 주소, 상세한 주소가 없으므로 기본 주소 사용
        x: store.storePositionX.toString(),
        y: store.storePositionY.toString(),
        regist: true,
    };
}

// 결과 마커에 반영
const displaySearchResults = () => {
    removeMarkers();
    searchResults.value.forEach((place) => {
        createMarker(place);
    });
    adjustMap();
    adjustMapSize(); // 검색 결과 표시 후 지도 크기 조정
};

// 마커 생성
const createMarker = (place) => {
    const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(place.y, place.x),
        map: map.value,
    });

    markers.value.push(marker);
    kakao.maps.event.addListener(marker, 'click', () => {
        showMarkerInfo(place);
    });
};

// 마커 삭제
const removeMarkers = () => {
    markers.value.forEach((marker) => {
        marker.setMap(null);
    });
    markers.value = [];
};

// 마커 정보
const showMarkerInfo = async (place) => {
    // 해당 장소에 대한 마커 찾기
    const marker = markers.value.find((marker) =>
        marker.getPosition().equals(new kakao.maps.LatLng(place.y, place.x))
    );

    // 마커 위치로 지도 중심 이동
    if (marker) {
        map.value.setCenter(marker.getPosition());
        map.value.setLevel(1); // 줌인 레벨 설정, 필요에 따라 조절 가능
    }

    try {
        const response = await axios.get(
            `${API_URL}/api/store/search/address?address=${place.address_name}`
        );
        console.log(response.data);
        if (response.data) {
            place.regist = true;
        } else {
            place.regist = false;
        }
    } catch (error) {
        console.error('Error sending store address:', error);
        place.showDetailsButton = false;
    }

    selectedItem.value = selectedItem.value === place ? null : place;
};
// 페이지 전환 시 마커 초기화 및 재할당
const refreshMarkers = () => {
    removeMarkers(); // 기존 마커 제거
    searchNearbyPlaces(); // 새로운 검색 결과에 따라 마커 다시 할당
};

// 모든 마커 확인을 위한 뷰포트 설정
const adjustMap = () => {
    const bounds = new kakao.maps.LatLngBounds();
    markers.value.forEach((marker) => {
        bounds.extend(marker.getPosition());
    });

    map.value.setBounds(bounds);
};

// 지도 크기 조절 함수 수정
const adjustMapSize = () => {
    // 기존 지도 엘리먼트 가져오기
    const mapContainer = document.getElementById('map');

    // 카카오 맵 리사이즈 이벤트 발생
    kakao.maps.event.trigger(map.value, 'resize');

    // 지도 중심 위치 재설정
    const center = map.value.getCenter();
    map.value.setCenter(center);
};
const apikey = import.meta.env.VITE_API_KEY;
// 카카오 맵 API 받아오기
onMounted(() => {
    if (window.kakao && window.kakao.maps) {
        initMap();
    } else {
        const script = document.createElement('script');
        script.onload = () => kakao.maps.load(initMap);
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${apikey}&libraries=services,clusterer,drawing&autoload=false`;
        document.head.appendChild(script);
    }
});
</script>

<style>
* {
    font-family: 'SejonghospitalBold' !important;
}
#map {
    width: 100%;
    height: 512px;
}
.map-list {
    /* position: absolute; */
    /* top: 10px; /* 적절한 위치로 조정 */
    /* left: 10px; /* 적절한 위치로 조정 */
    z-index: 100; /* 다른 요소 위로 표시하기 위해 z-index 설정 */
    background-color: white;
    width: 300px;
    height: 90%;
}

.scroll::-webkit-scrollbar {
    width: 10px; /* 스크롤바의 너비 */
}

.scroll::-webkit-scrollbar-thumb {
    height: 30%; /* 스크롤바의 길이 */
    background: lightgray; /* 스크롤바의 색상 */

    border-radius: 10px;
}

.scroll::-webkit-scrollbar-track {
    background: rgba(211, 211, 211, 0.2); /*스크롤바 뒷 배경 색상*/
}

.custom-map {
    display: flex;
}

.store-search {
    text-align: center;
    border: 3px solid rgb(185, 185, 171);
    border-radius: 5px;
    padding: 0.3rem;
}

.map-input {
    background-color: white;
    width: 80%;
    border: 1px solid;
    border-radius: 10px;
    padding-left: 0.5rem;
    padding-top: 0.1rem;
    padding-bottom: 0.1rem;
    margin-top: 0.5rem;
    margin-bottom: 0.5rem;
}

.map-btn {
    margin: 0.3rem;
}

.card-container {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
}

.search-card {
    width: 300px;
    border: 1px solid #ccc;
    border-radius: 8px;
    overflow: hidden;
}

.search-card-body {
    padding: 0.5rem;
    margin: 0.2rem;
    border: 1px solid #ccc; /* 테두리 색상을 조금 더 연하게 조정 */
    border-radius: 15px; /* 테두리 둥글기 설정 */
    background-color: white; /* 배경색 하얀색으로 설정 */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 약간의 그림자 효과 추가 */
    transition: transform 0.3s ease; /* 부드러운 변형 효과 */
}

.search-card-title {
    font-size: 18px;
    margin: 0.1rem;
}

.search-card-text {
    color: #666;
    /* margin-bottom: 16px; */
}

/* 선택된 카드 스타일 */
.is-expanded {
    transition: all 0.3s ease;
    max-height: 200px; /* 확장된 상태의 최대 높이 조정 */
}

/* 상세 정보 표시 스타일 */
.detail-info {
    padding: 0.5rem;
    border-top: 1px solid #eee;
}

.store-page-btn {
    display: inline-block;
    background-color: #4caf50; /* 버튼 기본 배경색 */
    background-image: linear-gradient(45deg, #44a08d, #093637); /* 그라데이션 배경 */
    color: white; /* 글자 색상 */
    padding: 10px 20px; /* 상하, 좌우 패딩 */
    border: none; /* 테두리 없음 */
    border-radius: 20px; /* 둥근 모서리 */
    cursor: pointer; /* 마우스 오버 시 커서 변경 */
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
    transition: all 0.3s ease; /* 부드러운 변형 효과 */
    margin-top: 10px; /* 상단 여백 */
    text-decoration: none; /* 텍스트 밑줄 제거 */
}

.store-page-btn:hover {
    background-image: linear-gradient(45deg, #093637, #44a08d); /* 호버 시 배경 그라데이션 변경 */
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2); /* 호버 시 그림자 효과 강화 */
    transform: translateY(-2px); /* 호버 시 약간 위로 이동 */
}
</style>
