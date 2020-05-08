import Vue from 'vue';
import App from '@/App.vue';
import router from '@/router';
import store from '@/store';
import vuetify from '@/vuetify';
import DatetimePicker from 'vuetify-datetime-picker';

Vue.config.productionTip = false;
Vue.use(DatetimePicker);

new Vue({
  vuetify,
  router,
  store,
  render: h => h(App)
}).$mount('#app');
