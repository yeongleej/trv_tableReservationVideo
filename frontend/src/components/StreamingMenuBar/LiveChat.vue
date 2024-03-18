<template>
    <div class="chat-container">
        <!-- <div class="chat-header">Chat</div> -->
        <div class="chat-messages" ref="chatContainerRef">
            <div class="message-container" v-for="(message, index) in messages" :key="index">
                <div
                    :class="
                        message.type === 'send' ? 'message send-message' : 'message recv-message'
                    ">
                    {{ message.text }}
                </div>
            </div>
        </div>
        <div class="chat-input">
            <form style="width: 100%" @submit.prevent="sendMessage">
                <input
                    class="message-input"
                    style="width: 75%"
                    v-model="newMessage"
                    type="text"
                    placeholder="메시지를 입력하세요" />
                <button class="send-btn" type="submit">전송</button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { useCounterStore } from '@/stores/counter';
const store = useCounterStore();

const props = defineProps({
    session: Object,
    myUserName: String,
    owner: Boolean,
});

// Vue의 반응성을 위한 데이터
const messages = ref([]);
const newMessage = ref('');
const chatContainerRef = ref(undefined);

////////////////////////////////////////////////////////
// 메세지 송신
const sendMessage = () => {
    var user;
    if (store.userId === undefined) {
        user = props.myUserName;
    } else {
        if (props.owner) {
            user = '사장님';
        } else {
            user = store.userNickname + '(' + store.userId + ')';
        }
    }
    var message = user + ' : ' + newMessage.value;

    if (newMessage.value.trim() != '') {
        props.session
            .signal({
                data: message, // Any string (optional)
                to: [], // Array of Connection objects (optional. Broadcast to everyone if empty)
                type: 'chat', // The type of message (optional)
            })
            .then(() => {
                console.log('Message successfully send');
            })
            .catch((error) => {
                console.error(error);
            });

        // 새로운 메시지를 Vue 데이터에 추가
        messages.value.push({ type: 'send', text: message });
        // 입력창 비우기
        newMessage.value = '';
        scrollToBottom();
    }
};

// 메세지 수신
props.session.on('signal:chat', (event) => {
    // 자기 자신을 제외한
    if (props.session.connection.connectionId !== event.from.connectionId) {
        // 새로운 메시지를 Vue 데이터에 추가
        messages.value.push({ type: 'recv', text: event.data });
        scrollToBottom();
    }
});

const scrollToBottom = () => {
    nextTick(() => {
        chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight;
    });
};
</script>

<style scoped>
body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
}

.chat-container {
    /* position: relative; */
    /* overflow-y: auto; */
    height: 100px;
}

.chat-header {
    background-color: #4caf50;
    color: white;
    padding: 10px;
    text-align: center;
}

.chat-messages {
    background-color: white;
    overflow-y: auto;
    padding: 10px;
    height: 80%;
}

.chat-input {
    /* position: absolute; */
    /* bottom: 0; */
    /* left: 0; */
    width: 100%;
    height: 20%;
    padding: auto;
    background-color: #f1f1f1;
    display: flex;
    text-align: center;
    align-items: center;
}

.message-container {
    /* display: flex; */
    margin-bottom: 10px;
    /* overflow-y: auto; */
}

.message {
    width: auto;
    max-width: 35%;
    padding: 8px;
    border-radius: 8px;
    margin: 5px;
    word-wrap: break-word;
}

.chat-messages::-webkit-scrollbar {
    /* width: 10px;  */
    display: none;
}

/* 
.chat-messages::-webkit-scrollbar-thumb {
    height: 30%; 
    background: lightgray; 
    border-radius: 10px;
}

.chat-messages::-webkit-scrollbar-track {
    background: rgba(211, 211, 211, 0.2); 
} */

.send-message {
    background-color: #4caf50;
    color: white;
    align-self: flex-end;
    margin-left: auto;
}

.recv-message {
    background-color: #ccc;
    align-self: flex-start;
    margin-right: auto;
}

.message-input {
    flex: 1;
    padding: 5px;
    margin-right: 0.5rem;
    border: 2px solid #ccc;
    border-radius: 5px;
}

button {
    background-color: #4caf50;
    color: white;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
    width: 4rem;
    /* text-align: center; */
    border-radius: 10px;
}
</style>
