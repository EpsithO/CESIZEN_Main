<template>
  <header class="premium-header">
    <div class="container">
      <!-- Logo -->
      <router-link to="/" class="brand">
        <span>CESI<span class="highlight">ZEN</span></span>
      </router-link>

      <!-- ===== NAV DESKTOP ===== -->
      <nav class="nav-links">
        <router-link to="/" class="nav-item">Accueil</router-link>
        <router-link to="/ressources" class="nav-item">Ressources</router-link>
        <router-link to="/tracker" class="nav-item">Tracker</router-link>

        <!-- Profil : nom d’utilisateur s’il est dispo -->
        <router-link
          v-if="isAuthenticated"
          to="/profile"
          class="nav-item username"
        >
          {{ user.value?.username || 'Mon profil' }}
        </router-link>

        <!-- CTA login si non connecté -->
        <el-button
          v-if="!isAuthenticated"
          type="success"
          tag="router-link"
          to="/login"
          class="cta"
        >
          Se connecter
        </el-button>

        <!-- Back-office (admin only) -->
        <el-button
          v-if="isAdmin"
          type="warning"
          tag="router-link"
          to="/backoffice"
          class="cta"
        >
          BackOffice
        </el-button>

        <!-- Déconnexion -->
        <el-button
          v-if="isAuthenticated"
          type="danger"
          class="cta"
          @click="handleLogout"
        >
          Déconnexion
        </el-button>
      </nav>

      <!-- ===== BURGER ===== -->
      <div class="burger" @click="toggleMenu">
        <el-icon><Menu /></el-icon>
      </div>
    </div>

    <!-- ===== NAV MOBILE ===== -->
    <transition name="fade-slide">
      <div v-if="menuOpen" class="nav-mobile">
        <router-link to="/" class="nav-item" @click="closeMenu">Accueil</router-link>
        <router-link to="/ressources" class="nav-item" @click="closeMenu">Ressources</router-link>
        <router-link to="/tracker" class="nav-item" @click="closeMenu">Tracker</router-link>

        <router-link
          v-if="isAuthenticated"
          to="/profile"
          class="nav-item username"
          @click="closeMenu"
        >
          {{ user.value?.username || 'Mon profil' }}
        </router-link>

        <router-link
          v-if="isAdmin"
          to="/backoffice"
          class="nav-item"
          @click="closeMenu"
          style="color: orange"
        >
          BackOffice
        </router-link>

        <el-button
          v-if="isAuthenticated"
          type="danger"
          class="cta"
          @click="handleLogout"
        >
          Déconnexion
        </el-button>

        <el-button
          v-else
          type="success"
          tag="router-link"
          to="/login"
          class="cta"
          @click="closeMenu"
        >
          Se connecter
        </el-button>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Menu } from '@element-plus/icons-vue'
import userService from '@/services/userService'
import { useAuth } from '@/composables/useAuth'

const router = useRouter()
const { user, setUser, logout, isAdmin, isAuthenticated } = useAuth()

const menuOpen = ref(false)
const toggleMenu = () => (menuOpen.value = !menuOpen.value)
const closeMenu  = () => (menuOpen.value = false)

const handleLogout = () => {
  logout()
  router.push('/login')
}

onMounted(async () => {
  // si on a déjà un user ou pas de token, inutile d’appeler /me
  if (user.value || !localStorage.getItem('token')) return
  try {
    const { data } = await userService.getMe()
    setUser(data)
  } catch {
    logout()
  }
})
</script>

<style scoped>
.premium-header {
  position: sticky;
  top: 0;
  z-index: 999;
  background: #ffffffcc;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: background 0.3s ease;
}
.container {
  max-width: 1280px;
  margin: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 24px;
}
.brand {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2e7d32;
  text-decoration: none;
}
.brand .highlight {
  color: #fdd835;
}
/* --- Nav desktop --- */
.nav-links {
  display: flex;
  align-items: center;
  gap: 28px;
}
.nav-item {
  font-weight: 500;
  font-size: 1rem;
  color: #333;
  text-decoration: none;
  transition: color 0.2s ease;
}
.nav-item:hover {
  color: #43a047;
}
/* Nom d’utilisateur un peu plus mis en valeur */
.username {
  font-weight: 600;
  color: #2e7d32;
}
/* CTA */
.cta {
  border-radius: 24px;
  font-weight: bold;
  padding: 8px 24px;
  transition: background 0.3s ease;
}
/* Burger */
.burger {
  display: none;
  font-size: 1.6rem;
  color: #43a047;
  cursor: pointer;
}
/* --- Nav mobile --- */
.nav-mobile {
  display: flex;
  flex-direction: column;
  background: white;
  padding: 20px;
  gap: 16px;
  text-align: center;
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.05);
}

/* Animation */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Responsive */
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
  .burger {
    display: block;
  }
}
</style>
