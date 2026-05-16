import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import BootstrapVueNext from 'bootstrap-vue-next'
import vClickOutside from 'click-outside-vue3'
import { registerScrollSpy } from 'vue3-scroll-spy'
import { vMaska } from 'maska'
import i18n from './i18n'
import pinia from '@/state/pinia'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css'
import '@/assets/scss/app.scss'

createApp(App)
  .use(pinia)
  .use(router)
  .use(BootstrapVueNext)
  .use(vClickOutside)
  .use(i18n)
  .use(registerScrollSpy)
  .directive('maska', vMaska)
  .mount('#app')
