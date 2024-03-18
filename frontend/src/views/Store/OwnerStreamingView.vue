<template>
    <div class="container">
        <!-- 2. 통화 -->
        <v-dialog v-model="toggleModal" persistent width="auto">
            <v-card v-if="acceptCallFlag">
                <v-card-title class="text-h5">
                    <p style="font-size: xx-large">'{{ callSenderNickname }}'님과 통화중</p>
                </v-card-title>
                <v-card-text>
                    <!-- <v-container><v-icon icon="mdi-account"></v-icon></v-container> -->

                    <!-- 오디오 세션 -->
                    <user-video :stream-manager="callStreamManager" class="hidden-tag" />
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
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="red" variant="text" @click="callOff"> 통화끊기 </v-btn>
                </v-card-actions>
            </v-card>

            <v-card v-else>
                <v-card-title class="text-h5"> 전화 문의 요청이 들어왔습니다. </v-card-title>
                <v-card-text>'{{ callSenderNickname }}'님의 요청을 수락하시겠습니까?</v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <!-- 전화 거절 이벤트 -->
                    <v-btn color="red" variant="text" @click="callCancel"> cancle </v-btn>
                    <!-- 전화 받기 이벤트 -->
                    <v-btn color="green-darken-1" variant="text" @click="callOk"> Ok </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <!-- 2. 통화 -->
        <div class="container2">
            <!-- 1. 스트리밍 -->
            <div class="container-item1" v-if="storeStatus['streaming']">
                <div class="container-stop" v-if="isStreamStop">
                    <p style="color: white; display: flex; font-size: 50px">Calling...Wait...</p>
                </div>
                <div class="container-start" v-else="isStreamStop">
                    <user-video :stream-manager="mainStreamManager" />
                </div>
            </div>
            <!-- 1. 스트리밍 -->
            <div class="container-item1" v-else>
                <v-container
                    style="
                        color: white;
                        align-items: center;
                        justify-items: center;
                        font-size: 50px;
                        width: 100%;
                    ">
                    Streaming Offline
                </v-container>
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
                            border: 1px solid black;
                            border-radius: 50px;
                            margin: 5px;
                        "
                        :src="image"
                        alt="userimg" />
                    <!-- 방송제목 -->
                    <div>
                        <p>{{ realTitle }}</p>
                        <P style="color: gray; font-size: small">{{ userCount }}명 시청중</P>
                    </div>
                </v-card>
            </div>
        </div>
        <v-container class="right-side">
            <!-- <v-row class="input-row"> -->
            <v-row class="option-input">
                <v-col v-for="option in ['call', 'wait', 'chat']" :key="option">
                    <label v-if="option == 'call'"> 전화문의</label>
                    <label v-if="option == 'wait'"> 웨이팅</label>
                    <label v-if="option == 'chat'"> 채팅</label>
                    <!-- 기능 이벤트 처리 -->
                    <!-- v-model="switchStates[option]" -->
                    <v-switch
                        color="blue"
                        v-model="storeStatus[option]"
                        @change="sendStoreStates(option)">
                    </v-switch>
                </v-col>
            </v-row>

            <v-row>
                <!-- <v-col><v-btn id="btn-1" @click="onClick('btn-1')">방송설정</v-btn></v-col> -->
                <!-- <v-col v-if="storeStatus['wait']"
                    ><v-btn id="btn-2" @click="onClick('btn-2')">웨이팅현황</v-btn></v-col
                >
                <v-col v-if="storeStatus['chat']"
                    ><v-btn id="btn-3" @click="onClick('btn-3')">실시간채팅</v-btn></v-col
                > -->
                <div class="option-check">
                    <div class="option-btn">
                        <v-btn id="btn-1" @click="onClick('btn-1')">방송설정</v-btn>
                    </div>
                    <div class="option-btn">
                        <v-btn v-show="storeStatus['wait']" id="btn-2" @click="onClick('btn-2')"
                            >웨이팅현황</v-btn
                        >
                    </div>
                    <div class="option-btn">
                        <v-btn v-show="storeStatus['chat']" id="btn-3" @click="onClick('btn-3')"
                            >실시간채팅</v-btn
                        >
                    </div>
                </div>
            </v-row>
            <v-row>
                <!-- 전화자리 -->
                <div class="click-component">
                    <v-container v-if="clickId === 'btn-1'">
                        <div v-if="storeStatus['streaming']">
                            <v-btn @click="offStream" color="red">방송종료</v-btn>
                        </div>
                        <div v-else>
                            <v-form>
                                <div class="streaming-title">
                                    <!-- <v-col>
                                                <label for="title">제목:</label>
                                            </v-col>
                                            <v-col>
                                                <input
                                                    v-model="title"
                                                    style="border: 1px solid black"
                                                    type="text"
                                                    id="title" />
                                            </v-col> -->

                                    <label class="title-label" for="title">방송제목</label>
                                    <input
                                        v-model="title"
                                        type="text"
                                        id="title"
                                        class="title-input" />
                                </div>

                                <div class="category-choice">
                                    <label style="width: 25%; padding: 0.7rem" for="category"
                                        >카테고리</label
                                    >
                                    <div class="category-select">
                                        <v-select
                                            id="category"
                                            :items="['먹방', '쿡방', '소통']"
                                            variant="outlined"
                                            v-model="category"
                                            density="compact">
                                        </v-select>
                                    </div>
                                </div>
                            </v-form>
                            <v-btn style="margin-left: 2rem" @click="onStream" color="success"
                                >방송시작</v-btn
                            >
                        </div>
                    </v-container>

                    <!-- 웨이팅 자리 -->
                    <v-container v-if="clickId === 'btn-2'">
                        <OwnerWaiting />
                    </v-container>
                    <!-- 3. 채팅 -->
                    <keep-alive>
                        <v-container v-if="clickId === 'btn-3'">
                            <div class="chat-container" v-if="session">
                                <!-- 세션이 연결 되었을 때 사용할 수 있도록 -->
                                <LiveChat
                                    class="live-chat"
                                    :session="session"
                                    :myUserName="myUserName"
                                    :owner="true" />
                            </div>
                        </v-container>
                    </keep-alive>
                    <!-- 3. 채팅 -->
                </div>
            </v-row>
        </v-container>
    </div>
</template>

<script setup>
import axios from 'axios';
import { ref, onBeforeUnmount, onMounted } from 'vue';
import LiveChat from '@/components/StreamingMenuBar/LiveChat.vue';
import OwnerWaiting from '@/components/StreamingMenuBar/OwnerWaiting.vue';
import { OpenVidu } from 'openvidu-browser';
import UserVideo from '../../components/Openvidu/UserVideo.vue';

import { useCounterStore } from '@/stores/counter';

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                       variable                       //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 피니아
const store = useCounterStore();

// 방송 제목
const title = ref('방송 제목을 입력하세요!');
const realTitle = ref('방송 제목을 입력하세요!');
const category = ref('먹방');

// 우측 설정 컴포넌트
const clickId = ref('btn-1');

// 스토어 상태 변수
const storeStatus = ref({
    streaming: false,
    call: false,
    wait: false,
    chat: false,
});

const onClick = function (id) {
    clickId.value = id;
};

axios.defaults.headers.post['Content-Type'] = 'application/json';
const APPLICATION_SERVER_URL =
    import.meta.env.NODE_ENV === 'production' ? '' : 'https://teddysopenvidu.kro.kr:5000/';
const OV = ref(undefined); // openvidu
const session = ref(undefined);
const callSession = ref(undefined);
const mainStreamManager = ref(undefined);
const callStreamManager = ref(undefined);
const callSubscribers = ref([]);
const callSender = ref('');
const callSenderNickname = ref('');

// const mySessionId = 'SessionA';
const mySessionId = store.storeId + '_streaming';
const myUserName = store.userId + '';

// 스트리밍 중 전화 flag
const isStreamStop = ref(false);

const accessToken = localStorage.getItem('accessToken');

const image = ref(undefined);

const userCount = ref(0);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                      onMounted                       //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 타이머 ID를 저장하기 위한 ref 생성
const intervalId = ref(null);

onMounted(async () => {
    await checkStoreStates();
    await settingMainSession();

    // 초기 지연 후 첫 캡처를 위한 setTimeout
    setTimeout(() => {
        captureAndUploadThumbnail(); // 즉시 첫 썸네일 캡처 및 업로드

        // 그 후 60초마다 반복 캡처 및 업로드를 위한 setInterval
        intervalId.value = setInterval(() => {
            captureAndUploadThumbnail(); // 주기적 썸네일 캡처 및 업로드
        }, 60000); // 60초마다 반복
    }, 10000); // 시작 후 10초 지연
});

// 썸네일 캡처 및 업로드 로직을 별도의 함수로 분리
async function captureAndUploadThumbnail() {
    if (mainStreamManager.value && storeStatus.value.streaming) {
        const videoElement = document.querySelector('video');
        if (videoElement) {
            const canvas = document.createElement('canvas');
            canvas.width = videoElement.videoWidth;
            canvas.height = videoElement.videoHeight;
            const context = canvas.getContext('2d');
            context.drawImage(videoElement, 0, 0, canvas.width, canvas.height);

            canvas.toBlob(async (blob) => {
                let formData = new FormData();
                formData.append('image', blob);

                try {
                    const response = await axios.put(
                        `${store.API_URL}/api/playroom/${store.storeId}/thumbnail`,
                        formData,
                        {
                            headers: {
                                Authorization: `Bearer ${accessToken}`,
                            },
                        }
                    );
                    console.log('썸네일 저장 성공:', response.data);
                } catch (error) {
                    console.error('썸네일 저장 실패:', error);
                }
            }, 'image/png');
        }
    }
}

// 스트리밍, 통화, 채팅 상태 확인
const checkStoreStates = async () => {
    // 상태 전송
    await axios({
        method: 'get',
        url: `${store.API_URL}/api/playroom/${store.storeId}`,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            console.log('가게 상태 정보 검색에 성공했습니다.');
            // 스토어 상태 초기화
            storeStatus.value = {
                streaming: res.data.broadcasting,
                call: res.data.calling,
                wait: res.data.waiting,
                chat: res.data.chatting,
            };
            image.value = res.data.userImage;

            if (res.data.broadcasting) {
                title.value = res.data.title;
                realTitle.value = res.data.title;
            }
            if (res.data.userCount != null) {
                userCount.value = res.data.userCount;
            }
        })
        .catch((err) => {
            // 업데이트에 실패한 경우 처리
            console.log('가게 상태 정보 검색에 실패했습니다.', err);
        });

    if (storeStatus.value['streaming']) {
        await onStream();
    }
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                    join session                      //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 스트리밍 ON
const onStream = async function () {
    if (title.value == '' || title.value == '방송 제목을 입력하세요!') {
        alert('방송 제목을 입력하세요!');
    } else {
        await navigator.mediaDevices
            .getUserMedia({ video: true, audio: true })
            .then(async (stream) => {
                console.log('카메라와 마이크가 감지되었습니다.');
                // 여기서 방송을 시작하거나 필요한 작업을 수행할 수 있습니다.
                storeStatus.value['streaming'] = true;
                await sendStoreStates('streaming');
                await settingMainSession();
                createStreaming();
            })
            .catch(async (error) => {
                console.error('카메라 또는 마이크를 감지할 수 없습니다:', error);
                // 사용자에게 오류 메시지를 표시하거나 방송을 시작하지 않는 등의 처리를 수행할 수 있습니다.
                alert('카메라 또는 마이크를 감지할 수 없습니다.');
                storeStatus.value['streaming'] = false;
                await sendStoreStates('streaming');
                await settingMainSession();
            });
    }
};

// 스트리밍 OFF
const offStream = async () => {
    // 1-4-2. 스트리밍 종료 알림
    await session.value.unpublish(mainStreamManager.value);
    mainStreamManager.value = undefined;
    await session.value
        .signal({
            data: 'streamEnd',
            to: [],
            type: 'streamEnd',
        })
        .then(() => {
            console.log('Message successfully send');
        })
        .catch((error) => {
            console.error(error);
        });

    storeStatus.value['streaming'] = false;
    settingMainSession();
    sendStoreStates();
};

// 세션 관리 로직
const settingMainSession = async () => {
    if (storeStatus.value['streaming'] || storeStatus.value['call'] || storeStatus.value['chat']) {
        if (!session.value) {
            await joinSession();
        }
    } else {
        leaveSession();
    }
};

const sendStoreStates = async (option) => {
    if (!storeStatus.value[option]) {
        if (option == 'chat') {
            closeChat();
        }
        if (
            (option == 'wait' && clickId.value == 'btn-2') ||
            (option == 'chat' && clickId.value == 'btn-3')
        ) {
            clickId.value = 'btn-1';
        }
    }

    let data = {
        title: null,
        image: null,
        info: null,
        userCount: null,
        category: null,
        chatting: storeStatus.value['chat'],
        calling: storeStatus.value['call'],
        waiting: storeStatus.value['wait'],
        broadcasting: storeStatus.value['streaming'],
    };

    if (storeStatus.value['streaming']) {
        if (category.value == '먹방') {
            data['category'] = 'eat';
        } else if (category.value == '쿡방') {
            data['category'] = 'cook';
        } else {
            data['category'] = 'justChat';
        }
        data['title'] = title.value;
        realTitle.value = title.value;
    } else {
        realTitle.value = '';
        if (storeStatus.value['chat']) {
            realTitle.value += '채팅 ';
        }
        if (storeStatus.value['call']) {
            realTitle.value += '전화 ';
        }
        if (storeStatus.value['wait']) {
            realTitle.value += '웨이팅 ';
        }

        data['title'] = realTitle.value;
        data['category'] = '';
    }

    axios({
        method: 'put',
        url: `${store.API_URL}/api/playroom/${store.storeId}`,
        data: data,
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })
        .then((res) => {
            // 성공적으로 업데이트된 경우 처리
            console.log('가게 상태 정보가 성공적으로 업데이트되었습니다.');
            settingMainSession();
        })
        .catch((err) => {
            // 업데이트에 실패한 경우 처리
            console.log('가게 상태 정보 업데이트에 실패했습니다.', err);
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

    // 2-1. 전화 요청 수신 (Main Session)
    session.value.on('signal:call', (event) => {
        callSenderNickname.value = event.data;
        callSender.value = event.from.connectionId;
        toggleModal.value = true;
    });

    // 2-4-1. 전화 End (Main Session)
    session.value.on('signal:call_end', () => {
        isStreamStop.value = false;
        leaveCallSession();
        session.value.publish(mainStreamManager.value);
        sendStreamingStatus('restart_streaming');
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
        await session.value.connect(token, { clientData: myUserName }).catch((error) => {
            console.log('세션에 연결하는 중에 오류가 발생했습니다:', error.code, error.message);
        });
    });

    // 1-4. 종료시 세션 정리(창이 닫혔을 때)
    window.addEventListener('beforeunload', leaveSession);
};

const createStreaming = () => {
    let newPublisher = OV.value.initPublisher(undefined, {
        audioSource: undefined,
        videoSource: undefined,
        publishAudio: true,
        publishVideo: true,
        resolution: '640x420',
        frameRate: 30,
        insertMode: 'APPEND',
        mirror: false,
    });

    // 1-3. 퍼블리셔 생성 (영상, 음성)
    mainStreamManager.value = newPublisher;
    session.value.publish(mainStreamManager.value);
};

// 1-4-1. 세션 떠나기
const leaveSession = () => {
    // 커넥션 끊기
    if (session.value) session.value.disconnect();

    // 변수 초기화
    session.value = undefined;
    mainStreamManager.value = undefined;
    OV.value = undefined;

    window.removeEventListener('beforeunload', leaveSession);
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                      call item                       //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 전화 flag
const toggleModal = ref(false);
// 전화 수신 flag
const acceptCallFlag = ref(false);

// 2-2-1. 전화 수신 OK ((Main Session)
const callOk = function () {
    acceptCallFlag.value = true;
    isStreamStop.value = true;

    sendCallResultMessage('callOk');
    session.value.unpublish(mainStreamManager.value);

    sendStreamingStatus('stopStreaming');
    joinCallSession();
};

// 2-2-2. 전화 수신 Cancel (Main Session)
const callCancel = function () {
    toggleModal.value = false;
    sendCallResultMessage('callCancel');
};

// 2-4. 전화 End
const callOff = async function () {
    isStreamStop.value = false;
    await leaveCallSession();
    await sendCallEndMessage();
    if (storeStatus.value['streaming']) {
        session.value.publish(mainStreamManager.value);
    }
    sendStreamingStatus('restartStreaming');
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                  join call session                   //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 2-2. 전화 수신 Send Call Result (Main Session)
const sendCallResultMessage = (data) => {
    session.value
        .signal({
            data: data,
            to: [],
            type: 'call_result_' + callSender.value,
        })
        .then(() => {
            console.log('Message successfully send : ' + data);
        })
        .catch((error) => {
            console.error(error);
        });
};

// 2-4-1. 전화 종료 Send Call End (Main Session)
const sendCallEndMessage = async () => {
    console.log('전화종료 메세지 보냄');
    console.log('callSender.value');
    await session.value
        .signal({
            data: 'call_end',
            to: [],
            type: 'call_end_' + callSender.value,
        })
        .then(() => {
            console.log('Message successfully send');
        })
        .catch((error) => {
            console.error(error);
        });
};

// 스트리밍 상태 Send Streaming Status (Main Session)
const sendStreamingStatus = (data) => {
    session.value
        .signal({
            data: data,
            to: [],
            type: data,
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
    toggleModal.value = false;
    acceptCallFlag.value = false;

    if (callSession.value) {
        if (storeStatus.value['streaming']) {
            await callSession.value.unpublish(callStreamManager.value);
        }
        await callSession.value.disconnect();
    }

    callSession.value = undefined;
    callSubscribers.value = [];

    window.removeEventListener('beforeunload', leaveCallSession);
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                          chat                        //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

const closeChat = () => {
    session.value
        .signal({
            data: 'closeChat',
            to: [],
            type: 'closeChat',
        })
        .then(() => {
            console.log('Message successfully send');
        })
        .catch((error) => {
            console.error(error);
        });
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                 create token/ session                //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// 토큰 가져오기
const getToken = async (mySessionId) => {
    const sessionId = await createSession(mySessionId);
    console.log(sessionId);
    console.log(mySessionId);
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////                         end                          //////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

onBeforeUnmount(() => {
    leaveSession();
    leaveCallSession();

    if (intervalId.value) {
        clearInterval(intervalId.value);
        console.log('타이머가 정리되었습니다.');
    }
});
</script>

<style scoped>
.container {
    display: grid;
    grid-template-columns: 3fr 3fr;

    width: 100%;
    height: 80%;
    margin: 1rem;
    padding-left: 3rem;
    padding-right: 3rem;
}
.container2 {
    display: grid;
    grid-template-rows: 17fr 2fr;
    padding-right: 2rem;
}
.container-item1 {
    display: grid;

    background-color: black;
    justify-content: center;
    align-items: center;
    border-radius: 10px;
}

.right-side {
    width: 100%;
    padding-left: 2rem;
    padding-right: 2rem;
}

/* .v-switch::v-deep .v-selection-control {
    display: inline;
    padding: 0.5rem;
} */

:deep(.v-selection-control) {
    display: inline;
    padding: 0.5rem;
}
:deep(.v-input__details) {
    display: none;
}

.option-input {
    text-align: center;
}

.option-check {
    text-align: center;
    display: flex;
    margin: auto;
    width: 100%;
}
.option-btn {
    margin: 1rem;
    width: 30%;
}

.click-component {
    width: 100%;
    height: 20rem;
}

.streaming-title {
    display: flex;
    padding-left: 1rem;
    margin-bottom: 1rem;
}
.title-label {
    width: 25%;
    padding: 0.7rem;
}
.title-input {
    width: 60%;
    border: 1px solid rgb(151, 150, 150);
    border-radius: 7px;
    padding: 0.5rem;
}

.category-choice {
    display: flex;
    padding-left: 1rem;
    margin-bottom: 1rem;
}
.category-select {
    display: inline;
    /* margin-left: 0.5rem; */
    /* width: 20%; */
    padding: 0;
}

.info {
    display: flex;
    background-color: yellow;
}

.chat-container {
    margin-left: 1rem;
    height: 23.8rem;
}
.live-chat {
    border: 1px solid silver;
    /* border-radius: 10px; */
    margin: auto;
}
.hidden-tag {
    display: none; /* 테그를 화면에서 숨김 */
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
</style>
