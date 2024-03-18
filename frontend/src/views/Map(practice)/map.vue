<template>
    <div id="app">
      <div id="map"></div>
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
    },
  };
  </script>
  
  <style>
  #map {
    width: 800px;
    height: 500px;
  }
  </style>
  