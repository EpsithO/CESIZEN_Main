import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ProfileView from '@/views/ProfileView.vue'
import ResourceView from '@/views/ResourceView.vue'
import ResourceDetailView from '@/views/ResourceDetailView.vue'
import EmotionTracker from '@/views/EmotionTracker.vue'
import BackOfficeView from '@/views/BackOffice.vue'
import NotFoundView from '@/views/NotFound.vue'

const routes = [
  { path: '/', name: 'Home', component: HomeView },
  { path: '/login', name: 'Login', component: LoginView },
  { path: '/profile', name: 'Profile', component: ProfileView, meta: { requiresAuth: true } },
  { path: '/ressources', name: 'Ressources', component: ResourceView },
  { path: '/ressources/:id', name: 'RessourceDetail', component: ResourceDetailView },
  { path: '/tracker', name: 'EmotionTracker', component: EmotionTracker, meta: { requiresAuth: true } },
  { path: '/backoffice', name: 'BackOffice', component: BackOfficeView, meta: { requiresAdmin: true } },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFoundView }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const isAuthenticated = !!token

  const { user } = useAuth()
  const isAdmin = user.value?.role?.name?.toUpperCase?.() === 'ADMIN'

  if (to.name === 'Login' && isAuthenticated) {
    return next({ name: 'Profile' })
  }

  if (to.meta.requiresAuth && !isAuthenticated) {
    return next({ name: 'Login' })
  }

  if (to.meta.requiresAdmin && !isAdmin) {
    return next({ name: 'Home' })
  }

  next()
})

export default router
