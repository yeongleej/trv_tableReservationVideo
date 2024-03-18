<template>
    <div class="container">
        <div class="container2">
            <!-- 1. 스트리밍 -->
            <div class="container-item1" v-if="isStreamOn">
                <div class="container-stop" v-if="isStreamStop">
                    <p style="color: white; display: flex; font-size: 50px">Calling...Wait...</p>
                </div>
                <div class="container-start" v-else="isStreamStop">
                    <user-video :stream-manager="mainStreamManager" />
                </div>
            </div>
            <!-- 1. 스트리밍 -->
            <div class="container-item1" v-else>
                <button style="display: flex; font-size: 50px" @click="joinStreaming" class="btn">
                    Watch
                </button>
            </div>
            <div class="info">
                <v-card
                    width="100%"
                    height="100%"
                    style="justify-items: center; align-items: center; display: flex">
                    <img
                        style="
                            height: 50px;
                            width: 50px;
                            display: flex;
                            border: 1px solid black;
                            border-radius: 50px;
                            margin: 5px;
                        "
                        :src="image"
                        alt="userimg" />
                    <div>
                        <p>{{ title }}</p>
                        <P style="color: gray; font-size: small">{{ userCount }}명 시청중</P>
                    </div>
                </v-card>
            </div>
        </div>
        <div class="container-item2">
            <div class="v-container">
                <div class="v-container-item1">
                    <div class="container3">
                        <v-btn
                            class="menu-btn"
                            id="btn-1"
                            style="height: 50px"
                            @click="onClick('btn-1')"
                            >가게정보</v-btn
                        >
                        <v-dialog v-model="reservation" style="width: 98%">
                            <template v-slot:activator="{ props }">
                                <v-btn
                                    class="menu-btn"
                                    style="height: 50px"
                                    v-bind="props"
                                    @click="joinReservation">
                                    예약하기
                                </v-btn>
                            </template>
                            <template v-slot:default="{ isActive }">
                                <v-card title="예약진행">
                                    <v-card-text>
                                        <v-btn
                                            text="예약창 닫기"
                                            @click="isActive.value = false"></v-btn>
                                        <ReservationStep
                                            :storeId="storeId"
                                            :storeName="storeName" />
                                    </v-card-text>
                                    <v-card-actions>
                                        <v-spacer></v-spacer>
                                        <v-btn
                                            text="예약창 닫기"
                                            @click="isActive.value = false"></v-btn>
                                    </v-card-actions>
                                </v-card>
                            </template>
                        </v-dialog>
                        <v-btn
                            class="menu-btn"
                            style="height: 50px"
                            id="btn-3"
                            @click="onClick('btn-3')"
                            >실시간채팅</v-btn
                        >
                        <v-row justify="center">
                            <!-- 2. 통화 -->
                            <v-dialog v-model="call" persistent width="auto">
                                <template v-slot:activator="{ props }">
                                    <v-btn
                                        class="menu-btn"
                                        style="height: 50px"
                                        color="default"
                                        v-bind="props"
                                        @click="joinCall">
                                        전화문의
                                    </v-btn>
                                </template>
                                <!-- <template>
                                    <v-btn color="default" @click="joinCall"> 전화문의 </v-btn>
                                </template> -->
                                <v-container v-if="storeStatus['call']">
                                    <v-card v-if="callFlag" class="call-card">
                                        <v-card-title v-if="ownerCallFlag" class="text-h5">
                                            <p style="font-size: xx-large">사장님과 통화중</p>
                                            <!-- 오디오 세션 -->
                                            <user-video
                                                :stream-manager="callStreamManager"
                                                class="hidden-tag" />
                                            <user-video
                                                v-for="sub in callSubscribers"
                                                :key="sub.stream.connection.connectionId"
                                                :stream-manager="sub"
                                                class="hidden-tag" />
                                            <!-- 오디오 세션 -->
                                            <div class="call-card-center">
                                                <img
                                                    class="call-image"
                                                    src="https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/phone-305741_640.webp" />
                                            </div>
                                        </v-card-title>
                                        <v-card-title v-else class="text-h5">
                                            전화를 거는 중입니다.
                                        </v-card-title>
                                        <v-card-text v-else>
                                            연결되는 동안 잠시만 기다려주세요.
                                        </v-card-text>
                                        <v-card-actions>
                                            <v-spacer></v-spacer>
                                            <v-btn color="red" variant="text" @click="callOff">
                                                통화끊기
                                            </v-btn>
                                        </v-card-actions>
                                    </v-card>

                                    <v-card v-else>
                                        <v-card-title class="text-h5">
                                            전화를 연결할까요?
                                        </v-card-title>
                                        <v-card-text>가게 사장님과 직접 연결됩니다.</v-card-text>
                                        <v-card-actions>
                                            <v-spacer></v-spacer>
                                            <v-btn color="red" variant="text" @click="call = false">
                                                cancle
                                            </v-btn>
                                            <v-btn
                                                color="green-darken-1"
                                                variant="text"
                                                @click="callOk">
                                                Ok
                                            </v-btn>
                                        </v-card-actions>
                                    </v-card>
                                </v-container>
                            </v-dialog>
                            <!-- 2. 통화 -->
                        </v-row>

                        <div style="display: flex">
                            <WaitingDialog :store-id="storeId" />
                        </div>

                        <v-btn class="menu-btn" style="height: 50px" @click="followtoggle">
                            <v-icon :icon="followIcon" :color="followColor"></v-icon
                            >{{ followtext }}</v-btn
                        >
                    </div>
                </div>
                <div class="v-container-item2">
                    <!-- 선택된 버튼에 따라 보여질 내용 -->
                    <div v-if="clickChoice === 'btn-1'">
                        <StoreInfo :store-id="storeId" />
                    </div>
                    <!-- 3. 채팅 -->
                    <keep-alive>
                        <v-container v-if="clickChoice === 'btn-3'" class="chat-container">
                            <LiveChat
                                class="live-chat"
                                :session="session"
                                :myUserName="myUserName"
                                :owner="false" />
                            <!-- <div style="display: grid">
                            </div> -->
                        </v-container>
                    </keep-alive>
                    <!-- 3. 채팅 -->
                    <div v-if="clickChoice === 'btn-6'">구독 내용</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import axios from 'axios';
import { ref, onBeforeUnmount, onMounted, defineComponent } from 'vue';
import LiveChat from '@/components/StreamingMenuBar/LiveChat.vue';
import StoreInfo from '@/components/StreamingMenuBar/StoreInfo.vue';
import WaitingDialog from '@/components/StreamingMenuBar/WaitingDialog.vue';
import { OpenVidu } from 'openvidu-browser';
import UserVideo from '../../components/Openvidu/UserVideo.vue';
import { useRoute } from 'vue-router';
import { useCounterStore } from '@/stores/counter';
import { computed } from 'vue';
import ReservationStep from '@/components/Reservation/ReservationStep.vue';

const route = useRoute();
const storeId = ref(route.params.storeId || '');
const storeName = ref(null);
const userFollowData = ref([]);
const userCount = ref(0);

onMounted(async () => {
    getStoreName();
    if (store.userId != undefined) {
        await getUserFlowData();
        isFollowCompute();
    }

    checkStoreStates();
});

const getStoreName = () => {
    axios({
        method: 'get',
        url: `${store.API_URL}/api/store/${storeId.value}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            storeName.value = res.data.storeName;
            console.log(res.data);
            console.log('스토어 이름', storeName.value);
        })
        .catch((err) => {
            console.log(err);
        });
};

const getUserFlowData = async () => {
    await axios({
        method: 'get',
        url: `${store.API_URL}/api/follow/store/search`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            userFollowData.value = res.data;
            console.log('구독 정보를 가져왔습니다.');
            isFollowCompute();
        })
        .catch((err) => {
            console.log('구독 정보 로딩 실패', err);
        });
};

const isFollow = ref(null);
// 구독 플래그, 실제 사용시에는 userData.value.isFollow
const isFollowCompute = () => {
    for (let i = 0; i < userFollowData.value.length; i++) {
        if (userFollowData.value[i].storeId == storeId.value) {
            isFollow.value = true;
            return;
        }
    }
    isFollow.value = false;
};

// 구독 아이콘, 텍스트, 색을 결정
const followIcon = computed(() => (isFollow.value ? 'mdi-heart' : 'mdi-heart-outline'));
const followColor = computed(() => (isFollow.value ? 'red' : 'gray'));
const followtext = computed(() => (isFollow.value ? '구독취소' : '구독하기'));
// 구독 상태 토글 함수
const followtoggle = async () => {
    // isFollow.value = !isFollow.value;
    if (store.userId == undefined) {
        alert('로그인후 사용해 주세요!');
    } else {
        if (isFollow.value === false) {
            await axios({
                method: 'get',
                url: `${store.API_URL}/api/follow/store/${storeId.value}`,
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            })
                .then((res) => {
                    console.log('팔로우를 성공했습니다.');
                    isFollowCompute();
                })
                .catch((err) => {
                    console.log('팔로우 실패', err);
                });
        } else {
            await axios({
                method: 'delete',
                url: `${store.API_URL}/api/follow/store/${storeId.value}`,
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            })
                .then((res) => {
                    console.log('팔로우 취소 성공');
                    isFollowCompute();
                })
                .catch((err) => {
                    console.log('팔로우 취소 실패', err);
                });
        }
        // 여기서 갱신
        await getUserFlowData(); // getUserFlowData 함수가 완료될 때까지 기다립니다.
        isFollowCompute(); // 새로운 데이터를 기반으로 isFollow 값을 설정합니다.
    }
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                       variable                       //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

axios.defaults.headers.post['Content-Type'] = 'application/json';
const APPLICATION_SERVER_URL =
    import.meta.env.NODE_ENV === 'production' ? '' : 'https://teddysopenvidu.kro.kr:5000/';
const OV = ref(undefined);
const session = ref(undefined);
const callSession = ref(undefined);
const mainStreamManager = ref(undefined);
const callStreamManager = ref(undefined);
const callSubscribers = ref([]);

const store = useCounterStore();

const accessToken = localStorage.getItem('accessToken');

const title = ref(undefined);
const image = ref(undefined);

const reservation = ref(false);

// 사장님 세션
// const mySessionId = 'SessionA';
const mySessionId = storeId.value + '_streaming';
let myUserName = 'viewer' + Math.floor(Math.random() * 100000);
if (store.userId != undefined) {
    myUserName = store.userId + '';
}

// 전화, 채팅, 웨이팅 상태
const storeStatus = ref({
    streaming: false,
    call: true,
    wait: false,
    chat: true,
});

// 스트리밍 flag
const isStreamOn = ref(false);

// 스트리밍 중 전화 flag
const isStreamStop = ref(false);
const clickChoice = ref('btn-1');

const onClick = function (id) {
    // 채팅
    if (id == 'btn-3') {
        joinChat();
    } else if (id == 'btn-2') {
        // 로그인 확인 처리
        if (store.userId == undefined) {
            alert('로그인후 사용해 주세요!');
        } else {
            clickChoice.value = id;
        }
    } else {
        clickChoice.value = id;
    }
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                 join video session                   //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

const joinStreaming = async () => {
    await checkStoreStates();

    if (storeStatus.value['streaming']) {
        isStreamOn.value = true;
        settingMainSession();
    } else {
        alert('사장님께서 방송중이지 않습니다!');
    }
};

const joinCall = async () => {
    call.value = false;
    await checkStoreStates();

    if (storeStatus.value['call']) {
        call.value = true;
        settingMainSession();
    } else {
        alert('사장님께서 전화통화 기능을 닫아두셨습니다!');
    }
};

const joinChat = async () => {
    await checkStoreStates();

    if (storeStatus.value['chat']) {
        settingMainSession();
        clickChoice.value = 'btn-3';
    } else {
        alert('사장님께서 채팅 기능을 닫아두셨습니다!');
    }
};

const joinReservation = async () => {
    reservation.value = false;
    if (store.userId == undefined) {
        alert('로그인후 사용해 주세요!');
    } else {
        reservation.value = true;
    }
};

const settingMainSession = () => {
    if (storeStatus.value['streaming'] || storeStatus.value['call'] || storeStatus.value['chat']) {
        if (!session.value) {
            joinSession();
        }
    } else {
        leaveSession();
    }
};

// 스트리밍, 통화, 채팅 상태 확인
const checkStoreStates = async () => {
    // 상태 전송
    await axios({
        method: 'get',
        url: `${store.API_URL}/api/playroom/${storeId.value}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then(async (res) => {
            console.log('가게 상태 정보 검색에 성공했습니다.');
            // 스토어 상태 초기화
            storeStatus.value = {
                streaming: res.data.broadcasting,
                call: res.data.calling,
                wait: res.data.waiting,
                chat: res.data.chatting,
            };
            title.value = res.data.title;
            image.value = res.data.userImage;
            if (res.data.userCount != null) {
                userCount.value = res.data.userCount;
            }
        })
        .catch(async (err) => {
            // 업데이트에 실패한 경우 처리
            console.log('가게 상태 정보 검색에 실패했습니다.', err);
        });
};

// 1. 최초 스트리밍 세션 생성
const joinSession = async () => {
    OV.value = new OpenVidu();
    session.value = OV.value.initSession();

    // 예외처리
    session.value.on('exception', ({ exception }) => {
        console.warn(exception);
    });

    // 1-2-1. 스트리밍 정보 입력 : 퍼블리셔가 생성 될 때 마다 실행
    session.value.on('streamCreated', async ({ stream }) => {
        const subscriber = await session.value.subscribe(stream);
        mainStreamManager.value = subscriber;
    });

    // 1-2-2. 스트리밍 정보 삭제 : 퍼블리셔가 삭제 될 때 마다 실행
    session.value.on('streamDestroyed', ({ stream }) => {
        mainStreamManager.value = undefined;
    });

    // 1-4-2. 스트리밍 종료 알림
    session.value.on('signal:streamEnd', () => {
        if (storeStatus.value['streaming']) {
            alert('방송이 종료되었습니다!');
            isStreamOn.value = false;
        }
        // leaveSession();
    });

    session.value.on('signal:stopStreaming', () => {
        console.log('전화대기화면on signal');
        isStreamStop.value = true;
    });

    session.value.on('signal:restartStreaming', () => {
        console.log('전화대기화면off signal');
        isStreamStop.value = false;
    });

    session.value.on('signal:closeChat', () => {
        clickChoice.value = 'btn-1';
        alert('사장님이 채팅방을 닫으셨습니다!');
    });

    session.value.on('signal:userCount', (event) => {
        if (event.data != '') {
            userCount.value = event.data;
        } else {
            userCount.value = 0;
        }
    });

    // 1-1. 세션 생성
    await getToken(mySessionId).then(async (token) => {
        // 1-2. 세션 참가
        await session.value
            .connect(token, { clientData: myUserName })
            .then(() => {
                // 2-2. 전화 수신 Result (Main Session)
                session.value.on(
                    'signal:call_result_' + session.value.connection.connectionId,
                    (event) => {
                        // 2-2-1. 전화 수신 OK
                        if (event.data === 'callOk') {
                            ownerCallFlag.value = true;
                            joinCallSession();
                        }
                        // 2-2-2. 전화 수신 Cancel
                        else if (event.data === 'callCancel') {
                            alert('전화가 거절되었습니다!!!');
                            leaveCallSession();
                        } else {
                            console.log('call error');
                        }
                    }
                );
                // 2-4-1. 전화 End (Main Session)
                session.value.on(
                    'signal:call_end_' + session.value.connection.connectionId,
                    (event) => {
                        leaveCallSession();
                    }
                );
            })
            .catch((error) => {
                console.log('세션에 연결하는 중에 오류가 발생했습니다:', error.code, error.message);
            });
    });

    // 시청자 수 초기화
    await sendStoreStates(0);

    // 1-4. 종료시 세션 정리(창이 닫혔을 때)
    window.addEventListener('beforeunload', leaveSession);
};

// 1-4-1. 세션 떠나기
const leaveSession = async () => {
    // 스트리밍 OFF
    isStreamOn.value = false;

    // 커넥션 끊기
    if (session.value) {
        console.log('종료 세션 시작!!!!!!!!!!!!!!!!!!!!!!!!!');
        // 시청자 수 초기화
        await sendStoreStates(1);
        session.value.disconnect();
    }

    // 변수 초기화
    session.value = undefined;
    mainStreamManager.value = undefined;
    OV.value = undefined;

    window.removeEventListener('beforeunload', leaveSession);
};

const sendStoreStates = async (cnt) => {
    await getNumberOfPeople(cnt);

    let data = {
        userCount: userCount.value,
    };
    // 서버 에게 알림
    await axios({
        method: 'put',
        url: `${store.API_URL}/api/playroom/${storeId.value}`,
        data: data,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            // 성공적으로 업데이트된 경우 처리
            console.log('가게 상태 정보가 성공적으로 업데이트되었습니다.');
        })
        .catch((err) => {
            // 업데이트에 실패한 경우 처리
            console.log('가게 상태 정보 업데이트에 실패했습니다.', err);
        });

    // 스트리머 에게 알림

    await session.value
        .signal({
            data: userCount.value,
            to: [],
            type: 'userCount',
        })
        .then(() => {
            console.log('Message successfully send');
        })
        .catch((error) => {
            console.error(error);
        });
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                      call item                       //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 전화 flag
const call = ref(false);
// 전화 송신 flag
const callFlag = ref(false);
// 전화 송신 성공 flag
const ownerCallFlag = ref(false);

// 2-1. 전화 송신 OK
const callOk = function () {
    callFlag.value = true;
    ownerCallFlag.value = false;
    sendCallMessage();
};

// 2-4. 전화 End
const callOff = async function () {
    await leaveCallSession();
    await sendCallEndMessage();
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                  join call session                   //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 2-1-1. 전화 요청 송신 (Main Session)
const sendCallMessage = () => {
    session.value
        .signal({
            data: store.userNickname,
            to: [],
            type: 'call',
        })
        .then(() => {
            console.log('Message successfully send');
        })
        .catch((error) => {
            console.error(error);
        });
};

// 2-4-1. 전화 종료 Send Call End (Main Session)
const sendCallEndMessage = async () => {
    await session.value
        .signal({
            data: 'send_call_end_message',
            to: [],
            type: 'call_end',
        })
        .then(() => {
            console.log('Message successfully send');
        })
        .catch((error) => {
            console.error(error);
        });
};

// 2-3. 전화 세션 생성
const joinCallSession = () => {
    OV.value = new OpenVidu();
    callSession.value = OV.value.initSession();

    // 예외처리
    callSession.value.on('exception', ({ exception }) => {
        console.warn(exception);
    });

    // 2-3-5. 스트리밍 정보 입력 : 퍼블리셔가 생성 될 때 마다 실행
    callSession.value.on('streamCreated', ({ stream }) => {
        let subscriber = callSession.value.subscribe(stream);
        callSubscribers.value.push(subscriber);
    });

    // 2-3-6. 스트리밍 정보 삭제 : 퍼블리셔가 삭제 될 때 마다 실행
    callSession.value.on('streamDestroyed', ({ stream }) => {
        const index = callSubscribers.value.indexOf(stream.streamManager, 0);
        if (index >= 0) {
            callSubscribers.value.splice(index, 1);
        }
    });

    // 2-3-1. 세션 생성
    getToken(mySessionId + '_call').then((token) => {
        // 2-3-2. 세션 참가
        callSession.value
            .connect(token, { clientData: myUserName })
            .then(() => {
                let newPublisher = OV.value.initPublisher(undefined, {
                    audioSource: undefined,
                    videoSource: false,
                    publishAudio: true,
                    publishVideo: false,
                });

                // 2-3-3. 퍼블리셔 생성 (음성)
                callStreamManager.value = newPublisher;
                callSession.value.publish(callStreamManager.value);
            })
            .catch((error) => {
                console.log('세션에 연결하는 중에 오류가 발생했습니다:', error.code, error.message);
            });
    });

    // 2-3-4. 종료시 통화 세션 정리(창이 닫혔을 때)
    window.addEventListener('beforeunload', leaveCallSession);
};

// 2-3-4-1. 통화 세션 떠나기
const leaveCallSession = async () => {
    call.value = false;
    callFlag.value = false;
    ownerCallFlag.value = false;
    isStreamStop.value = false;

    if (callSession.value) {
        if (storeStatus.value['streaming']) {
            await callSession.value.unpublish(callStreamManager.value);
        }
        await callSession.value.disconnect();
    }

    callSession.value = undefined;
    callSubscribers.value = [];
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                 create token/ session                //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 토큰 가져오기
const getToken = async (mySessionId) => {
    const sessionId = await createSession(mySessionId);
    return await createToken(sessionId);
};

// 세션 생성
const createSession = async (sessionId) => {
    const response = await axios.post(
        APPLICATION_SERVER_URL + 'api/sessions',
        { customSessionId: sessionId },
        {
            headers: { 'Content-Type': 'application/json' },
        }
    );
    return response.data;
};

// 토큰 생성하기
const createToken = async (sessionId) => {
    const response = await axios.post(
        APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections',
        {},
        {
            headers: { 'Content-Type': 'application/json' },
        }
    );
    return response.data;
};

// 인원수 확인하기
const getNumberOfPeople = async (cnt) => {
    const response = await axios.get(
        APPLICATION_SERVER_URL + 'api/sessions/' + mySessionId + '/user-count',
        {},
        {
            headers: { 'Content-Type': 'application/json' },
        }
    );
    userCount.value = response.data - cnt;
    console.log(response.data);
    console.log(userCount.value);
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                         end                          //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

onBeforeUnmount(() => {
    leaveSession();
});
</script>

<style scoped>
.container {
    /* display: grid;
    grid-template-columns: 3fr 2fr;
    background-color: aqua;
    width: 100%;
    height: 90vh;
    margin: 5px; */
    display: grid;
    grid-template-columns: 3fr 3fr;

    width: 100%;
    height: 80%;
    margin: 1rem;
    padding-left: 3rem;
    padding-right: 3rem;
}
.container2 {
    /* display: grid;
    grid-template-rows: 8fr 2fr; */
    display: grid;
    grid-template-rows: 17fr 2fr;
    padding-right: 2rem;
}
.container-item1 {
    /* display: flex;
    background-color: black;
    justify-content: center;
    align-items: center;
    border-radius: 10px; */
    display: grid;

    background-color: black;
    justify-content: center;
    align-items: center;
    border-radius: 10px;
}
.info {
    display: flex;
    background-color: yellow;
}
.container-item2 {
    display: flex;
    /* background-color: blueviolet; */
}
.v-container {
    display: grid;
    grid-template-rows: 3fr 7fr;
}
.v-container-item1 {
    display: flex;
    /* background-color: brown; */
}
.v-container-item2 {
    display: grid;
    /* background-color: whitesmoke; */
    padding: 0px;
}
.container3 {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    grid-template-rows: 1fr 1fr;
    width: 100%;
    height: 100%;
    align-items: center;
    justify-items: center;
}

.hidden-tag {
    display: none;
}

.btn {
    font-size: 1.2rem;
    padding: 1rem 2.5rem;
    border: none;
    outline: none;
    border-radius: 0.4rem;
    cursor: pointer;
    text-transform: uppercase;
    background-color: rgb(14, 14, 26);
    color: rgb(234, 234, 234);
    font-weight: 700;
    transition: 0.6s;
    box-shadow: 0px 0px 60px #1f4c65;
    -webkit-box-reflect: below 10px linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.4));
}

.btn:active {
    scale: 0.92;
}

.btn:hover {
    background: rgb(2, 29, 78);
    background: linear-gradient(270deg, rgba(2, 29, 78, 0.681) 0%, rgba(31, 215, 232, 0.873) 60%);
    color: rgb(4, 4, 38);
}

.live-chat {
    border: 1px solid silver;
    /* border-radius: 10px; */
    /* margin: auto; */
    margin-left: 20px;
    width: 96%;
    /* height: px; */
}

.chat-container {
    /* margin-right: 50px; */
    /* padding-left: 100px; */
    height: 377px;
}

.menu-btn {
    width: 150px;
    height: 100%;
    font-size: medium;
}

.call-card {
    height: 100%;
    width: 100%;
}

.call-card-center {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.call-image {
    height: 100px;
    width: 100px;
}

/* .call-profile-left,
.call-profile-right {
    margin-top: 30px;
    flex: 5;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
} */
</style>
