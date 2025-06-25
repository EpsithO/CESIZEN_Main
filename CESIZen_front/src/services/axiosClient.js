import axios from 'axios'
import userService from '@/services/userService'

const API_URL = import.meta.env.VITE_API_URL

const api = axios.create({
  baseURL: API_URL,
})

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('🔐 Token ajouté aux headers :', token) // 🔍 Ajout temporaire
    } else {
      console.warn('⚠️ Aucun token présent dans localStorage')
    }
    return config
  },
  error => Promise.reject(error)
)


api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      userService.logout()
    }
    return Promise.reject(error)
  }
)

export default api
