import { createRouter, createWebHashHistory } from 'vue-router';
import { useCounterStore } from '@/stores/counter';
import HomeView from '../views/HomeView.vue';
import LoginView from '@/views/LoginView.vue';
import StreamingView from '@/views/StreamingView.vue';
import BoardView from '@/views/board/BoardView.vue';
import BoardDetailView from '@/views/board/BoardDetailView.vue';
import BoardWriteView from '@/views/board/BoardWriteView.vue';
import StoreSearchView from '@/views/Store/StoreSearchView.vue';
import SearchView from '@/views/SearchView.vue';
import CustomerReservationView from '@/views/Reservation/CustomerReservationView.vue';
import OwnerReservationView from '@/views/Reservation/OwnerReservationView.vue';
import CustomerStreamingView from '@/views/Store/CustomerStreamingView.vue';
import OwnerStreamingView from '@/views/Store/OwnerStreamingView.vue';
import ReservationView from '@/views/Reservation/ReservationView.vue';
import MypageView from '@/views/Mypage/MypageView.vue';
import FollowerListView from '@/views/Mypage/FollowerListView.vue';
import ZariyopassView from '@/views/Mypage/ZariyopassView.vue';
import BusinessRegisterView from '@/views/Mypage/BusinessRegisterView.vue';
import StoreManageView from '@/views/Mypage/StoreManageView.vue';
import MyInfoView from '@/views/Mypage/MyInfoView.vue';
import LoginSuccessView from '@/views/LoginSuccessView.vue';

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            name: 'HomeView',
            component: HomeView,
        },
        {
            path: '/login',
            name: 'LoginView',
            component: LoginView,
        },
        {
            path: '/streaming',
            name: 'StreamingView',
            component: StreamingView,
        },
        {
            path: '/streaming/:storeId',
            name: 'CustomerStreamingView',
            component: CustomerStreamingView,
            props: true,
        },
        {
            path: '/board',
            name: 'BoardView',
            component: BoardView,
        },
        {
            path: '/board/write',
            name: 'BoardWriteView',
            component: BoardWriteView,
        },
        {
            path: '/board/:boardId',
            name: 'BoardDetailView',
            component: BoardDetailView,
            props: true,
        },
        {
            path: '/storesearch',
            name: 'StoreSearchView',
            component: StoreSearchView,
        },
        {
            path: '/search/:search',
            name: 'SearchView',
            component: SearchView,
            props: true,
        },
        {
            path: '/customerreservation',
            name: 'CustomerReservationView',
            component: CustomerReservationView,
        },
        {
            path: '/ownerreservation',
            name: 'OwnerReservationView',
            component: OwnerReservationView,
        },
        {
            path: '/ownerstreaming',
            name: 'OwnerStreamingView',
            component: OwnerStreamingView,
        },
        {
            path: '/reservation',
            name: 'ReservationView',
            component: ReservationView,
        },
        {
            path: '/mypage',
            name: 'MypageView',
            component: MypageView,
        },
        {
            path: '/followerlist',
            name: 'FollowerListView',
            component: FollowerListView,
        },
        {
            path: '/zariyopass',
            name: 'ZariyopassView',
            component: ZariyopassView,
        },
        {
            path: '/businessregister',
            name: 'BusinessRegisterView',
            component: BusinessRegisterView,
        },
        {
            path: '/storemanage',
            name: 'StoreManageView',
            component: StoreManageView,
        },
        {
            path: '/myinfo',
            name: 'MyInfoView',
            component: MyInfoView,
        },
        {
            path: '/login-success/:atk/:rtk',
            name: 'loginSuccess',
            component: LoginSuccessView,
        },
    ],
});

// 비로그인 시 페이지 이동 제한 기능임 필요시 활용
// router.beforeEach((to, from) => {
//     const store = useCounterStore();
//     if (to.name === 'StreamingView' && !store.islogin) {
//         window.alert('로그인이 필요합니다.');
//         return { name: 'LoginView' };
//     }
// });

export default router;
