import axios from 'axios'
import { API_BASE_URL } from '@/config'

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('esun_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (res) => {
    // 滑動 token
    if (res.data?.token) {
      sessionStorage.setItem('esun_token', res.data.token)
      // 同步更新 pinia store（lazy import 避免循環依賴）
      import('@/state/pinia').then(({ useAuthStore }) => {
        const auth = useAuthStore()
        auth.token = res.data.token
      })
    }
    // token 過期或無效 → 清除登入狀態並導回登入頁
    if (res.data?.success === -999) {
      sessionStorage.clear()
      const currentPath = window.location.pathname
      const loginPath = currentPath.startsWith('/admin') ? '/admin/login' : '/login'
      if (currentPath !== loginPath) {
        window.location.href = loginPath
      }
    }

    return res.data
  },
  (err) => Promise.reject(err)
)

export default request
