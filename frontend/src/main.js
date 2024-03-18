import { createApp } from 'vue';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import App from './App.vue';
import router from './router';
import './assets/article.css';

import '@mdi/font/css/materialdesignicons.css'; // Vuetify icons
import 'vuetify/styles'; // Vuetify styles
import { createVuetify } from 'vuetify'; // Vuetify core
import * as components from 'vuetify/components'; // Vuetify components
import * as directives from 'vuetify/directives'; // Vuetify directives
import { VCalendar } from 'vuetify/labs/VCalendar'; // Vuetify Labs VCalendar

const app = createApp(App);

// Pinia
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// Vuetify
const vuetify = createVuetify({
    components: {
        ...components, // spread all the components
        VCalendar, // add VCalendar
    },
    directives,
});

app.use(pinia);
app.use(router);
app.use(vuetify);

app.mount('#app');
