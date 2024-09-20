import { createApp } from 'vue'
import App from './App.vue'
import route from './router'
import 'element-plus/theme-chalk/el-loading.css';
import 'element-plus/theme-chalk/el-message.css';
const app=createApp(App)
app.use(route)
app.mount('#app')
