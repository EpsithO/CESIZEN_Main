import { ref, computed, watchEffect } from 'vue'
import userService from '@/services/userService'

const user = ref(null)
const isLoading = ref(true) // utile pour affichage conditionnel

export function useAuth() {
  // Détecte si l'utilisateur est connecté
  const isAuthenticated = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.role?.name?.toUpperCase() === 'ADMIN')

  function setUser(userInfo) {
    user.value = userInfo
  }

  function clearUser() {
    user.value = null
    localStorage.removeItem('token')
  }

  async function logout() {
    clearUser()
    window.location.href = '/login'
  }

  // Au chargement du composable, on essaie de récupérer l'utilisateur si token
  async function fetchUserIfTokenExists() {
    const token = localStorage.getItem('token')
    if (!token) {
      isLoading.value = false
      return
    }

    try {
      const response = await userService.getMe()
      user.value = response.data
    } catch {
      clearUser()
    } finally {
      isLoading.value = false
    }
  }

  // Ce hook ne s'exécute qu'une fois
  fetchUserIfTokenExists()

  return {
    user,
    setUser,
    clearUser,
    logout,
    isAuthenticated,
    isAdmin,
    isLoading,
  }
}
