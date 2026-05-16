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
    return res.data
  },
  (err) => Promise.reject(err)
)

export default request
