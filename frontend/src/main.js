import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import router from './router' // 导入路由
import store from './store' // 导入状态管理

// 创建应用实例
const app = createApp(App)

// 配置axios
axios.defaults.baseURL = process.env.VUE_APP_API_URL || ''
axios.defaults.timeout = 30000

// 添加请求拦截器
axios.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    return config
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 添加响应拦截器
axios.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    return response
  },
  error => {
    // 对响应错误做点什么
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 全局挂载axios
app.config.globalProperties.$axios = axios

// 使用插件
app.use(ElementPlus)
app.use(router)
app.use(store)

// 挂载应用
app.mount('#app') 