<template>
  <div id="app">
    <p style="margin-top: -12px">
      <em class="link">
        <a href="/web/documentation/#CategoryCode" target="_blank">카테고리 코드목록을 보시려면 여기를 클릭하세요!</a>
      </em>
    </p>
    <div class="map_wrap">
      <div id="map" style="width: 100%; height: 100%; position: relative; overflow: hidden;"></div>
      <ul id="category">
        <li id="BK9" data-order="0">
          <span class="category_bg bank"></span>
          은행
        </li>
        <li id="MT1" data-order="1">
          <span class="category_bg mart"></span>
          마트
        </li>
        <li id="PM9" data-order="2">
          <span class="category_bg pharmacy"></span>
          약국
        </li>
        <li id="OL7" data-order="3">
          <span class="category_bg oil"></span>
          주유소
        </li>
        <li id="CE7" data-order="4">
          <span class="category_bg cafe"></span>
          카페
        </li>
        <li id="CS2" data-order="5">
          <span class="category_bg store"></span>
          편의점
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  mounted() {
    if (window.kakao && window.kakao.maps) {
      this.initMap();
    } else {
      const script = document.createElement('script');
      script.onload = () => kakao.maps.load(this.initMap);
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=771bcadc181290207b7566c124585021&libraries=services,clusterer,drawing&autoload=false`;
      document.head.appendChild(script);
    }
  },
  methods: {
    initMap() {
      var container = document.getElementById('map');
      var options = {
        center: new kakao.maps.LatLng(36.3553675622378, 127.298408300646),
        level: 5,
      };
      this.map = new kakao.maps.Map(container, options);
      this.placesService = new kakao.maps.services.Places();

      var zoomControl = new kakao.maps.ZoomControl();
      this.map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

      var mapTypeControl = new kakao.maps.MapTypeControl();
      this.map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);
    },
    searchPlaces() {
      if (!this.currCategory) {
        return;
      }

      this.placeOverlay.setMap(null);
      this.removeMarker();

      this.ps.categorySearch(this.currCategory, this.placesSearchCB, { useMapBounds: true });
    },
    placesSearchCB(data, status, pagination) {
      if (status === kakao.maps.services.Status.OK) {
        this.displayPlaces(data);
      } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        // 검색결과가 없는 경우의 처리
      } else if (status === kakao.maps.services.Status.ERROR) {
        // 검색 중에 오류가 발생한 경우의 처리
      }
    },
    displayPlaces(places) {
      var order = document.getElementById(this.currCategory).getAttribute('data-order');

      for (var i = 0; i < places.length; i++) {
        var marker = this.addMarker(new kakao.maps.LatLng(places[i].y, places[i].x), order);

        (function (marker, place) {
          kakao.maps.event.addListener(marker, 'click', function () {
            this.displayPlaceInfo(place);
          }.bind(this));
        }.bind(this))(marker, places[i]);
      }
    },
    addMarker(position, order) {
      var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_category.png';
      var imageSize = new kakao.maps.Size(27, 28);
      var imgOptions = {
        spriteSize: new kakao.maps.Size(72, 208),
        spriteOrigin: new kakao.maps.Point(46, (order * 36)),
        offset: new kakao.maps.Point(11, 28),
      };
      var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions);
      var marker = new kakao.maps.Marker({
        position: position,
        image: markerImage,
      });

      marker.setMap(this.map);
      this.markers.push(marker);

      return marker;
    },
    removeMarker() {
      for (var i = 0; i < this.markers.length; i++) {
        this.markers[i].setMap(null);
      }
      this.markers = [];
    },
    displayPlaceInfo(place) {
      var content = '<div class="placeinfo">' +
        '   <a class="title" href="' + place.place_url + '" target="_blank" title="' + place.place_name + '">' + place.place_name + '</a>';

      if (place.road_address_name) {
        content += '    <span title="' + place.road_address_name + '">' + place.road_address_name + '</span>' +
          '  <span class="jibun" title="' + place.address_name + '">(지번 : ' + place.address_name + ')</span>';
      } else {
        content += '    <span title="' + place.address_name + '">' + place.address_name + '</span>';
      }

      content += '    <span class="tel">' + place.phone + '</span>' +
        '</div>' +
        '<div class="after"></div>';

      this.contentNode.innerHTML = content;
      this.placeOverlay.setPosition(new kakao.maps.LatLng(place.y, place.x));
      this.placeOverlay.setMap(this.map);
    },
    addEventHandle(target, type, callback) {
      if (target.addEventListener) {
        target.addEventListener(type, callback);
      } else {
        target.attachEvent('on' + type, callback);
      }
    },
    addCategoryClickEvent() {
      var category = document.getElementById('category');
      var children = category.children;

      for (var i = 0; i < children.length; i++) {
        children[i].onclick = this.onClickCategory;
      }
    },
    onClickCategory() {
      var id = this.id;
      var className = this.className;

      this.placeOverlay.setMap(null);

      if (className === 'on') {
        this.currCategory = '';
        this.changeCategoryClass();
        this.removeMarker();
      } else {
        this.currCategory = id;
        this.changeCategoryClass(this);
        this.searchPlaces();
      }
    },
    changeCategoryClass(el) {
      var category = document.getElementById('category');
      var children = category.children;

      for (var i = 0; i < children.length; i++) {
        children[i].className = '';
      }

      if (el) {
        el.className = 'on';
      }
    },
  },
  data() {
    return {
      map: null,
      placesService: null,
      markers: [],
      currCategory: '',
      placeOverlay: new kakao.maps.CustomOverlay({ zIndex: 1 }),
      contentNode: document.createElement('div'),
      ps: new kakao.maps.services.Places(),
    };
  },
  created() {
    this.contentNode.className = 'placeinfo_wrap';
    this.addEventHandle(this.contentNode, 'mousedown', kakao.maps.event.preventMap);
    this.addEventHandle(this.contentNode, 'touchstart', kakao.maps.event.preventMap);
    this.placeOverlay.setContent(this.contentNode);
    this.addCategoryClickEvent();
  },
};
</script>

<style>
#map {
  width: 800px;
  height: 500px;
}
</style>
