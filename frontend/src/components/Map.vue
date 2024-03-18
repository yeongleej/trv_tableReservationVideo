<template>
    <div id="app" style="margin-left: 50px">
        <h2>주소 검색하기</h2>
        <v-container id="map">
            <div class="map-list">
                <input
                    v-model="searchKeyword"
                    placeholder="주소 검색시 시/군/구 지번을 적어주세요"
                    @keyup.enter="searchNearbyPlaces"
                    class="map-input" />
                <v-btn @click="searchNearbyPlaces">검색</v-btn>
            </div>
        </v-container>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useCounterStore } from '@/stores/counter';
const store = useCounterStore();
const searchKeyword = ref('');
const searchResults = ref([]);
const map = ref(null);
const placesService = ref(null);
const markers = ref([]);
const currentPage = ref(1);
const itemsPerPage = ref(15);
const totalResults = ref(0);
const totalPages = ref(0);
// 이전에 생성된 인포윈도우를 참조하기 위한 변수 추가
let infowindow = null;
const myPagination = ref(null);

const displayedResults = ref([]);

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

    // 드래그 이벤트 리스너 추가
    kakao.maps.event.addListener(map.value, 'dragend', updateMarkerPosition);
    // 지도 드래그 시작 시 정보창 닫기
    kakao.maps.event.addListener(map.value, 'dragstart', () => {
        closeInfoWindow();
    });
};

// 드래그 이벤트가 발생할 때마다 마커 위치 업데이트 및 주소 정보 갱신
const updateMarkerPosition = () => {
    // 현재 지도 중심 좌표 가져오기
    const center = map.value.getCenter();

    // 마커가 하나뿐이므로 첫 번째 마커의 위치 업데이트
    if (markers.value.length > 0) {
        markers.value[0].setPosition(center);

        // 좌표로부터 주소 정보 갱신
        updateAddress(center);

        // 정보창이 열려있는 경우 닫기
        closeInfoWindow();

        // 새로운 위치로 place 정보 업데이트
        updatePlaceInfo(center);
    } else {
        // 마커가 없는 경우 새로 생성
        const marker = new kakao.maps.Marker({
            position: center,
            map: map.value,
        });
        markers.value.push(marker);
    }

    // 좌표 값 저장
    saveMarkerPositions();
    // 정보창이 열려있는 경우 닫기
    closeInfoWindow();
};

// 새로운 위치로 place 정보 업데이트
const updatePlaceInfo = (position) => {
    // 좌표로부터 place 정보 갱신
    const placesService = new kakao.maps.services.Places();
    placesService.keywordSearch(
        searchKeyword.value,
        (data, status) => {
            if (status === kakao.maps.services.Status.OK) {
                searchResults.value = data;
                console.log(data);
            } else {
                console.error('검색에 실패했습니다. 상태 코드:', status);
            }
        },
        {
            location: position,
            size: 1,
        }
    );
};
const showMarkerInfo = (place) => {
    // 마커 정보를 표시할 HTML을 생성
    const infoWindowContent = `
        <div class="infowindow-content" >
            <p>${place.address_name}</p>
        </div>
    `;

    // 이미 인포윈도우가 열려 있는지 확인하고 업데이트 또는 새로 생성
    if (!infowindow) {
        // 인포윈도우가 없는 경우 새로 생성
        infowindow = new window.kakao.maps.InfoWindow({
            content: infoWindowContent,
        });
    } else {
        // 인포윈도우가 있는 경우 내용을 업데이트
        infowindow.setContent(infoWindowContent);
    }

    // 인포윈도우를 해당 마커 위에 표시
    infowindow.open(map.value, markers.value[0]);

    // 새로운 위치로 place 정보 업데이트
    updatePlaceInfo(markers.value[0].getPosition());
    return infowindow; // infowindow 반환
};

// 좌표로부터 주소 정보 갱신하는 함수
const updateAddress = (position) => {
    // 좌표로부터 주소 정보를 가져와서 표시
    const geocoder = new kakao.maps.services.Geocoder();
    geocoder.coord2Address(position.getLng(), position.getLat(), (result, status) => {
        if (status === kakao.maps.services.Status.OK) {
            // 결과가 있는 경우 첫 번째 결과의 주소 정보를 가져와서 표시
            const newAddress = result[0].address.address_name;

            console.log('현재 주소:', newAddress);
            // 업데이트된 주소 값을 바로 인포윈도우에 반영
            updateInfowindowContent(newAddress);

            // 여기에서 주소 정보를 사용하여 정보창 업데이트 등의 작업을 할 수 있습니다.
        } else {
            console.error('주소 정보를 가져오는 데 실패했습니다.');
        }
    });
};

// 인포윈도우 내용 업데이트 함수
const updateInfowindowContent = (newAddress) => {
    if (markers.value.length > 0 && searchResults.value.length > 0) {
        const place = searchResults.value[0]; // 첫 번째 검색 결과를 사용하도록 가정
        const infoWindowContent = `
            <div class="infowindow-content" >
                <p>${newAddress}</p>
            </div>
        `;

        // 인포윈도우 업데이트
        const infowindow = showMarkerInfo(place);
        infowindow.setContent(infoWindowContent);
    }
};

const searchNearbyPlaces = () => {
    const center = map.value.getCenter();

    // 전국 범위로 bounds 설정
    const bounds = new kakao.maps.LatLngBounds(
        new kakao.maps.LatLng(33.0041, 124.592),
        new kakao.maps.LatLng(38.634, 131.877)
    );

    var count = 1;
    const callback = (data, status, pagination) => {
        if (status === kakao.maps.services.Status.OK) {
            if (totalPages.value == 0) {
                totalPages.value = pagination.last;
                myPagination.value = pagination;
            }
            searchResults.value = data;
            console.log(data);
            store.storeX = data[0].x;
            store.storeY = data[0].y;
            store.storeAddress = data[0].address_name;
            console.log(store.storeAddress);
            displaySearchResults();
        } else {
            console.error('검색에 실패했습니다. 상태 코드:', status);
        }
    };

    // 이전에 생성된 마커들 제거
    removeMarkers();

    // 마커가 하나만 남도록 함
    placesService.value.keywordSearch(searchKeyword.value, callback, {
        location: center,
        bounds: bounds, // 전국 범위로 설정
        size: 1, // 마커를 한 개만 생성하도록 함
    });
};

// 마커 생성
const createMarker = (place) => {
    const position = new kakao.maps.LatLng(place.y, place.x);
    const marker = new kakao.maps.Marker({
        position: position,
        map: map.value,
    });

    markers.value.push(marker);
    // 마커의 좌표 값을 저장
    marker.setPosition(position);

    kakao.maps.event.addListener(marker, 'click', () => {
        showMarkerInfo(place);
    });
};

// 결과 마커에 반영 후 지도 중심으로 이동하고 마커의 좌표 값을 저장
const displaySearchResults = () => {
    removeMarkers();
    searchResults.value.forEach((place) => {
        createMarker(place);
    });
    adjustMap();
    adjustMapSize(); // 검색 결과 표시 후 지도 크기 조정

    // 마커가 있는 위치로 지도 이동 후 좌표 값을 저장
    const center = map.value.getCenter();
    map.value.setCenter(center);
    saveMarkerPositions();

    // 마커 클릭 이벤트 리스너 추가
    addMarkerClickListener();
};

// 마커의 좌표 값을 저장하는 함수
const saveMarkerPositions = () => {
    const markerPositions = markers.value.map((marker) => marker.getPosition());
    console.log(markerPositions);
};

// 마커 삭제
const removeMarkers = () => {
    markers.value.forEach((marker) => {
        marker.setMap(null);
    });
    markers.value = [];
};

// 마커 클릭 이벤트 리스너 추가
const addMarkerClickListener = () => {
    kakao.maps.event.addListener(markers.value[0], 'click', () => {
        // 이전에 열렸던 인포윈도우 닫기
        closeInfoWindow();

        // 새로운 인포윈도우 열기
        showMarkerInfo(searchResults.value[0]);
    });
};

// 인포윈도우 닫기 함수
const closeInfoWindow = () => {
    const infowindow = new kakao.maps.InfoWindow(); // infowindow 변수 정의

    infowindow.close();
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
#map {
    width: 1024px;
    height: 512px;
}
.map-list {
    position: absolute;
    top: 10px; /* 적절한 위치로 조정 */
    left: 35px; /* 적절한 위치로 조정 */
    z-index: 100; /* 다른 요소 위로 표시하기 위해 z-index 설정 */
    display: grid;
    grid-template-columns: 8fr 2fr;
    height: 36px;
    width: 400px;
}
.map-input {
    display: flex;
}

.card-container {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
}

.card {
    width: 300px;
    border: 1px solid #ccc;
    border-radius: 8px;
    overflow: hidden;
}

.card-body {
    padding: 16px;
    margin: 1px;
    border: 1px solid;
}

.card-title {
    font-size: 18px;
    margin-bottom: 8px;
}

.card-text {
    color: #666;
    margin-bottom: 16px;
}
/* 인포윈도우 내용의 크기 조정 */
.infowindow-content {
    width: 300px; /* 원하는 너비로 조정 */
    padding: 10px; /* 원하는 내부 여백으로 조정 */
    /* 기타 스타일링 */
}
</style>
