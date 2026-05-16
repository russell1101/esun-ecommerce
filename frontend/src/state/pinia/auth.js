import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token:    sessionStorage.getItem('esun_token')    || null,
    role:     sessionStorage.getItem('esun_role')     || null,
    userId:   sessionStorage.getItem('esun_user_id')  || null,
    username: sessionStorage.getItem('esun_username') || null,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isMember:   (state) => state.role === 'MEMBER',
    isAdmin:    (state) => state.role === 'ADMIN',
  },
  actions: {
    setAuth(token, data) {
      this.token    = token
      this.role     = data.role
      this.userId   = String(data.userId)
      this.username = data.username
      sessionStorage.setItem('esun_token',    token)
      sessionStorage.setItem('esun_role',     data.role)
      sessionStorage.setItem('esun_user_id',  String(data.userId))
      sessionStorage.setItem('esun_username', data.username)
    },
    logout() {
      this.token    = null
      this.role     = null
      this.userId   = null
      this.username = null
      sessionStorage.removeItem('esun_token')
      sessionStorage.removeItem('esun_role')
      sessionStorage.removeItem('esun_user_id')
      sessionStorage.removeItem('esun_username')
    },
  },
})
