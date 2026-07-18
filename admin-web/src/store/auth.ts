import { defineStore } from 'pinia'
import type { AdminInfo, AdminLoginResponse } from '@/types'

const TOKEN_KEY = 'leileme_admin_token'
const INFO_KEY = 'leileme_admin_info'

interface AuthState {
  token: string
  adminInfo: AdminInfo | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: '',
    adminInfo: null
  }),
  getters: {
    isLoggedIn: (state): boolean => !!state.token
  },
  actions: {
    setLogin(data: AdminLoginResponse) {
      this.token = data.accessToken
      this.adminInfo = data.admin
      localStorage.setItem(TOKEN_KEY, this.token)
      localStorage.setItem(INFO_KEY, JSON.stringify(this.adminInfo))
    },
    logout() {
      this.token = ''
      this.adminInfo = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(INFO_KEY)
    },
    loadFromStorage() {
      const token = localStorage.getItem(TOKEN_KEY)
      const infoStr = localStorage.getItem(INFO_KEY)
      if (token) {
        this.token = token
      }
      if (infoStr) {
        try {
          this.adminInfo = JSON.parse(infoStr) as AdminInfo
        } catch {
          this.adminInfo = null
        }
      }
    }
  }
})
