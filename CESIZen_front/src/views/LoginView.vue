<template>
  <div class="login-page">
    <div class="login-container">
      <div class="left-panel">
        <h1 class="main-title">Bienvenue sur <span>CESIZen</span></h1>
        <p class="description">
          Prenez soin de votre santé mentale. Connectez-vous ou créez un compte pour commencer votre suivi émotionnel.
        </p>
      </div>

      <div class="right-panel">
        <!-- Connexion -->
        <el-card class="login-card" shadow="hover">
          <h2 class="card-title">Connexion</h2>
          <el-form @submit.prevent="handleLogin" :model="{ username, password }" label-position="top">
            <el-form-item>
              <el-input v-model="username" placeholder="Pseudo" clearable :disabled="loading" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="password" placeholder="Mot de passe" show-password clearable :disabled="loading" />
            </el-form-item>
            <div class="forgot-password">
              <router-link to="/forgot-password">Mot de passe oublié ?</router-link>
            </div>
            <el-alert v-if="error" :title="error" type="error" show-icon class="form-error" />
            <el-button
              type="success"
              native-type="submit"
              :loading="loading"
              :disabled="loading"
              class="submit-button"
            >
              Se connecter
            </el-button>
          </el-form>
        </el-card>

        <!-- Inscription -->
        <el-card class="register-card" shadow="hover">
          <h2 class="card-title">Créer un compte</h2>
          <el-form @submit.prevent="handleRegister" :model="registerForm" label-position="top">
            <el-form-item>
              <el-input v-model="registerForm.username" placeholder="Pseudo" clearable :disabled="registerLoading" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.email" placeholder="Email" clearable :disabled="registerLoading" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.emailConfirm" placeholder="Confirmation email" clearable :disabled="registerLoading" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.password" placeholder="Mot de passe" show-password clearable :disabled="registerLoading" />
            </el-form-item>
            <small class="password-info">
              8 caractères min. avec majuscule, minuscule, chiffre, et caractère spécial.
            </small>
            <el-form-item>
              <el-input v-model="registerForm.passwordConfirm" placeholder="Confirmation mot de passe" show-password clearable :disabled="registerLoading" />
            </el-form-item>
            <el-alert v-if="registerError && registerError.length" type="error" show-icon class="form-error">
              <ul><li v-for="(msg, i) in registerError" :key="i">{{ msg }}</li></ul>
            </el-alert>
            <el-button
              type="success"
              native-type="submit"
              :loading="registerLoading"
              :disabled="registerLoading"
              class="submit-button"
            >
              Créer mon compte
            </el-button>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import userService from '@/services/userService'
import { useAuth } from '@/composables/useAuth'

const { setUser } = useAuth()
const router = useRouter()

const username = ref('')
const password = ref('')
const error = ref(null)
const loading = ref(false)

const registerForm = ref({
  username: '',
  email: '',
  emailConfirm: '',
  password: '',
  passwordConfirm: ''
})
const registerError = ref(null)
const registerLoading = ref(false)

// 🔐 Connexion
const handleLogin = async () => {
  error.value = null

  if (!username.value || !password.value) {
    error.value = 'Pseudo et mot de passe requis.'
    return
  }

  loading.value = true
  try {
    const userData = await userService.login(username.value, password.value)
    setUser(userData)
    
    console.log('✅ Connexion réussie, utilisateur :', userData)

    const role = userData.role?.name?.toUpperCase?.()
    if (role === 'ADMIN') {
      router.push('/backoffice')
    } else {
      router.push('/profile')
    }
  } catch (err) {
    console.error('❌ Erreur lors de la connexion :', err)
    error.value = err.response?.data?.message || 'Erreur de connexion'
  } finally {
    loading.value = false
  }
}



const handleRegister = async () => {
  registerError.value = []

  const { username, email, emailConfirm, password, passwordConfirm } = registerForm.value
  const errors = []

  if (!username || !email || !emailConfirm || !password || !passwordConfirm) {
    errors.push("Tous les champs sont obligatoires.")
  }
  if (email !== emailConfirm) errors.push("Les emails ne correspondent pas.")
  if (password !== passwordConfirm) errors.push("Les mots de passe ne correspondent pas.")

  if (errors.length) {
    registerError.value = errors
    return
  }

  registerLoading.value = true
  try {
    const userData = await userService.registerAndLogin({ username, email, password })
    setUser(userData)

    const role = userData.role?.name?.toUpperCase?.()
    if (role === 'ADMIN') {
      router.push('/backoffice')
    } else {
      router.push('/')
    }
  } catch (err) {
    const response = err.response?.data
    registerError.value = response?.errors || [response?.message || 'Erreur inconnue.']
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  background: linear-gradient(to bottom right, #e8f5e9, #fffde7);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.login-container {
  display: flex;
  max-width: 1200px;
  width: 100%;
  background: white;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  flex-wrap: wrap;
}

.left-panel {
  background: #75c878;
  color: white;
  padding: 40px;
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 20px;
  text-align: center;
}

.left-panel span {
  color: #ffeb3b;
}

.left-panel .description {
  font-size: 1rem;
}

.right-panel {
  flex: 1;
  min-width: 300px;
  padding: 40px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background-color: #fafafa;
}

.card-title {
  font-size: 1.4rem;
  margin-bottom: 20px;
  font-weight: 600;
  color: #4caf50;
  text-align: center;
}

.form-error {
  margin: 15px 0;
}

.password-info {
  font-size: 0.75rem;
  color: #777;
  margin-bottom: 10px;
}

.forgot-password {
  margin: 8px 0 16px;
  font-size: 0.85rem;
  text-align: right;
}

.forgot-password a {
  color: #4caf50;
  text-decoration: none;
}
.forgot-password a:hover {
  text-decoration: underline;
}

.submit-button {
  width: 100%;
  border-radius: 30px;
  font-weight: bold;
  padding: 12px 0;
}
</style>
