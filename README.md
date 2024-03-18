<img src="https://capsule-render.vercel.app/api?type=venom&color=F89E6B&section=header&text=TRV&fontSize=120" width="1100" height="350">

## 1. 서비스 소개

- 서비스 명 : TRV(Table Reservation with Video) <br>
  <img src="https://github.com/yeongleej/Triplog/assets/81922339/2f493e4c-d39e-4703-98e0-a0d59860b8d0" width="600">
  > 실시간 화면 공유 및 직접 테이블 예약이 가능한  `음식점 예약 및 웨이팅` 서비스

### 타겟 🎯

- 유명가게의 현재 웨이팅 상황을 알고 싶은 방문자
- 예약시 직접 원하는 테이블을 선택하고 싶은 사람
- 본인 가게의 장점을 직접 홍보하며 손님을 늘리고 싶은 사장님
- 예약 시스템을 통해 손님을 효율적으로 관리하고 싶은 사장님

👉 J적 성향으로 미리미리를 중요시 여기는 모든 분들

### 📌 기술 스택

#### ✔️ BE <br>

<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"><img src="https://img.shields.io/badge/java-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"><img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"><img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"><img src="https://img.shields.io/badge/Jwt-DD344C?style=for-the-badge&logo=jsonwebtokens&logoColor=white">

#### ✔️ FE <br>

<img src="https://img.shields.io/badge/vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white"><img src="https://img.shields.io/badge/vite-646CFF?style=for-the-badge&logo=vite&logoColor=white"><img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"><img src="https://img.shields.io/badge/vuetify-2E3084?style=for-the-badge&logo=vuetify&logoColor=white">

#### ✔️ DB <br>

<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"><img src="https://img.shields.io/badge/postgres-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">

#### ✔️INFRA <br>

<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><img src="https://img.shields.io/badge/Oracle Cloud Infrastructure-B8DBE4?style=for-the-badge&logo=oracle&logoColor=white"><img src="https://img.shields.io/badge/amazons3-1572B6?style=for-the-badge&logo=amazons3&logoColor=white"><img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/jira-0052CC?style=for-the-badge&logo=jirasoftware&logoColor=white"><img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"><img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white">
<img src="https://img.shields.io/badge/sonarqube-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white"><img src="https://img.shields.io/badge/portainer-13BEF9?style=for-the-badge&logo=portainer&logoColor=white"><img src="https://img.shields.io/badge/gerrit-00E47C?style=for-the-badge&logo=gerrit&logoColor=white">

#### ✔️WEBRTC <br>

<img src="https://img.shields.io/badge/webrtc-FBB040?style=for-the-badge&logo=webrtc&logoColor=white"><img src="https://img.shields.io/badge/openvidu-5CAE58?style=for-the-badge&logo=&logoColor=white"><img src="https://img.shields.io/badge/kurento-F78C40?style=for-the-badge&logo=&logoColor=white">

## 2. 기획

### 배경 📃

> - 다양한 환경에서 예약 시스템을 이용해야하는 상황에 직면
> - 여행, 데이트, 회식, 중요한 모임 등 음식점을 예약하는 것의 중요성이 더욱 증대
> - 항공권, 숙박 예약 사이트를 검색했을 때와 달리, **음식점 예약을 전문으로 하는 웹사이트가 존재하지 않음**

### 목적 🥅

<div class="notice--success">
 캐치테이블, 테이블링이라는 모바일 예약서비스가 제공하지 않는, 실시간 화면공유를 통해 웨이팅 현황을 파악할 수 있고, 사업자가 직접 가게구조를 만들어 소비자는 원하는 테이블을 직접 예약할 수 있는 웹 서비스
</div>

## 3. 설계

### 💡 시스템 구조도

| ![시스템구조도1](https://github.com/yeongleej/Triplog/assets/81922339/3357e9ea-0c42-4c0f-a527-b5c0c199b1e0) | ![시스템구조도2](https://github.com/yeongleej/Triplog/assets/81922339/9c6a9529-eeac-491f-9dd3-6453cdc2e476) |
| :---------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------: |

### 💡 명세서

<a href="https://devryeong.notion.site/6e2a67b8515040139a2d9d47eb4e1405?pvs=4">wireframe</a> |
<a href="https://devryeong.notion.site/6e2a67b8515040139a2d9d47eb4e1405?pvs=4">기능 명세</a> |
<a href="https://devryeong.notion.site/6e2a67b8515040139a2d9d47eb4e1405?pvs=4">API 연동 규격서</a> |
<a href="https://devryeong.notion.site/6e2a67b8515040139a2d9d47eb4e1405?pvs=4">ERD</a> |
<a href="https://devryeong.notion.site/6e2a67b8515040139a2d9d47eb4e1405?pvs=4">Sequence Diagram</a> |
<a href="https://devryeong.notion.site/6e2a67b8515040139a2d9d47eb4e1405?pvs=4">GanttChart</a><br>

## 4. 서비스 화면

![메인화면](https://github.com/yeongleej/Triplog/assets/81922339/93d868b2-f25c-42ae-a5d9-62aa25992d26)<br>

|                                               소셜 로그인                                                |                                                게시판                                                |
| :------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------: |
| ![소셜로그인](https://github.com/yeongleej/Triplog/assets/81922339/7a1e14aa-5be6-4376-9b27-3182825d7e3d) | ![게시판](https://github.com/yeongleej/Triplog/assets/81922339/2171fbc0-a5dc-4de3-8014-276eaafeb1c6) |

|                                               상점 구조 설정                                               |                                               예약 일시 설정                                               |
| :--------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------: |
| ![가게구조설정](https://github.com/yeongleej/Triplog/assets/81922339/4da2da66-a4dc-464f-a5d8-0bdd94f46d3e) | ![예약일시설정](https://github.com/yeongleej/Triplog/assets/81922339/e7c3cc2c-a56e-4e7a-9ff5-0027492f3d0c) |

|                                              예약하기                                              |                                                구독하기                                                |
| :------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------: |
| ![예약](https://github.com/yeongleej/Triplog/assets/81922339/d2195e50-dd69-4131-b395-fc2d99d22cf4) | ![구독하기](https://github.com/yeongleej/Triplog/assets/81922339/16e1a4e9-613d-4387-997e-871990137901) |

|              스트리밍 페이지               |           웨이팅           |
| :----------------------------------------: | :------------------------: |
| ![방송및부가기능](/gif/방송및부가기능.gif) | ![웨이팅](/gif/웨이팅.gif) |

|            전화문의            |            채팅            |
| :----------------------------: | :------------------------: |
| ![전화문의](/gif/전화기능.gif) | ![채팅](/gif/채팅기능.gif) |

## 5. 프로젝트 진행

### Git 🌟

- git 컨벤션 규칙
  ![깃컨벤션](https://github.com/yeongleej/Triplog/assets/81922339/c5d4f3c1-9483-433e-ae15-bf1f06243d77)
- git flow
  ![gitflow](https://github.com/yeongleej/Triplog/assets/81922339/d1406656-9d9c-4660-9de2-ee872d046150)

### JIRA 📈

```
- 매주 월요일 오전 회의에서 금주에 진행되어야 할 이슈를 백로그에 등록
- 에픽은 가장 큰 단위인 기획, 페이지화면 구성, Infra, API 등으로 구성
- 스토리 포인트를 할당할 때는 모든 팀원이 참여, 작업 복잡성과 리스크를 고려하여 스토리 포인트를 할당 (스토리 포인트를 통해 우선순위 설정과 프로젝트 진행 상황을 추적하는데 용이했습니다.)
- 이슈는 스토리를 완료하기 위한 작은 업무 단위로 생성 예를 들어 소셜 로그인 기능 구현, 카카오 로그인 API 테스트, Oauth2 + JWT 구현 등으로 생성
- 에픽링크 태그를 사용하여 이슈를 구별
```

❗️ 무엇보다 담당자와 스토리 포인트 설정, 현재 작업중인 내용 지라에 실시간으로 반영하는 것을 가장 중요하게 생각했습니다.

| <img src="https://github.com/yeongleej/Triplog/assets/81922339/0103f4fd-05a4-44a0-a9d4-27c1279e64cd"> | <img src ="https://github.com/yeongleej/Triplog/assets/81922339/3f6265ac-63db-4bc1-b2ba-c758ff5e1882" width="2500"> |
| :---------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------: |

## 6. 버전관리 및 배포

### 버전 정보

```
FE: Vue3(3.3.11), Vite(5.0.12), Vuetify(3.5.1)

BE: Spring Boot(3.2.1), Spring Security, Gradle(8.5), Java(17.0.9), JPA, JWT

DB: MySQL(8.0.35), Redis, Postgres

Cloud: EC2(Ubuntu 20.04 LTS), OCI(Ubuntu 20.04 LTS), S3 Bucket

WebRTC: OpenVidu(2.29.1), kurento, coturn, Nginx

IDE: VSCode, IntelliJ

Infra: Git, Jira, Docker, Nginx(1.21), Jenkins, SonarQube, Gerrit, Portainer
```

<aside>
💡배포관련 자세한 사항은 exec/TRV 서비스 포팅 매뉴얼_B201.pdf 참고

</aside>

## 7. 팀원 소개

<div>

|                                              **강준혁**                                              |                                              **김태희**                                              |                                              **남찬현**                                              |                                              **임혜령**                                              |                                              **이지영**                                              |                                              **최호연**                                              |
| :--------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------: |
| ![강준혁](https://github.com/yeongleej/Triplog/assets/81922339/a77eca15-90d8-41f0-bc81-3c8d21eb120e) | ![김태희](https://github.com/yeongleej/Triplog/assets/81922339/4843792a-3929-4e46-a472-22c377f54921) | ![남찬현](https://github.com/yeongleej/Triplog/assets/81922339/99dcd0f4-057b-4e7a-a3fb-51f9826da3f6) | ![임혜령](https://github.com/yeongleej/Triplog/assets/81922339/8876276e-ed41-49c6-a77f-8763df41d158) | ![이지영](https://github.com/yeongleej/Triplog/assets/81922339/a5f8cf2d-f4cb-42d5-bec3-ad64fe3b7fcf) | ![최호연](https://github.com/yeongleej/Triplog/assets/81922339/62b6ad58-f30b-4194-9948-889cb018c945) |
|                                &nbsp;&nbsp;&nbsp;FE&nbsp;&nbsp;&nbsp;                                |                                &nbsp;&nbsp;&nbsp;BE&nbsp;&nbsp;&nbsp;                                |                                &nbsp;&nbsp;&nbsp;BE&nbsp;&nbsp;&nbsp;                                |                                                INFRA                                                 |                                &nbsp;&nbsp;&nbsp;BE&nbsp;&nbsp;&nbsp;                                |                                &nbsp;&nbsp;&nbsp;FE&nbsp;&nbsp;&nbsp;                                |

</div>
