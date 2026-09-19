import axios from 'axios'

// 统一的 axios 实例：所有业务请求都从这里发出，方便统一加 token、统一处理错误
const request = axios.create({
  // /api 会由 Vite 代理转发到 http://localhost:8080（见 vite.config.js）
  baseURL: '/api',
  timeout: 10000
})

// ===== 请求拦截器：发请求前自动带上 token =====
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      // 后端 TokenInterceptor 是从请求头 "token" 里取令牌的
      config.headers.token = token
    }
    return config
  },
  error => Promise.reject(error)
)

// ===== 响应拦截器：统一处理"未登录" =====
request.interceptors.response.use(
  response => response,
  error => {
    // 后端令牌缺失或解析失败时返回 401
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录状态已失效，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('loginName')
      // 这里拿不到 router 实例，用原生跳转最简单
      setTimeout(() => {
        window.location.href = '/login'
      }, 600)
    }
    return Promise.reject(error)
  }
)

export default request
