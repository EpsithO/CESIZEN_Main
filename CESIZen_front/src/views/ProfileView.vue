<template>
  <div class="page">
    <h1 class="title">Mon profil</h1>

    <!-- Section principale -->
    <div class="card-grid">

      <!-- Informations personnelles -->
      <el-card class="profile-card">
        <div class="section-header">
          <h2>Informations personnelles</h2>
        </div>
        <el-form @submit.prevent="handleUpdateInfo" :model="infoForm" label-position="top">
          <el-form-item label="Pseudo">
            <el-input v-model="infoForm.username" :disabled="infoLoading" />
          </el-form-item>
          <el-form-item label="Email">
            <el-input v-model="infoForm.email" :disabled="infoLoading" />
          </el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="infoLoading"
            block
          >
            Mettre à jour
          </el-button>
        </el-form>
      </el-card>

      <!-- Mot de passe -->
      <el-card class="profile-card">
        <div class="section-header">
          <h2>Changer le mot de passe</h2>
        </div>
        <el-form @submit.prevent="handleUpdatePassword" :model="passwordForm" label-position="top">
          <el-form-item label="Ancien mot de passe">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="Nouveau mot de passe">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="Confirmation">
            <el-input v-model="passwordForm.newPasswordConfirm" type="password" show-password />
          </el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="passwordLoading"
            block
          >
            Changer le mot de passe
          </el-button>
        </el-form>
      </el-card>

      <!-- Données & RGPD -->
      <el-card class="profile-card">
        <div class="section-header">
          <h2>Données & RGPD</h2>
        </div>
        <el-button type="success" @click="handleAccessData" block>📄 Télécharger mes données</el-button>
        <el-button type="danger" @click="handleDeleteData" block>🗑️ Supprimer mes données</el-button>
      </el-card>

      <!-- Compte -->
      <el-card class="profile-card danger">
        <div class="section-header">
          <h2>Suppression du compte</h2>
        </div>
        <el-button type="danger" @click="handleDeleteAccount" block>⚠️ Supprimer mon compte</el-button>
      </el-card>

    </div>

    <!-- Déconnexion -->
    <div class="logout">
      <el-button type="warning" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon> Se déconnecter
      </el-button>
    </div>

    <el-alert
      v-if="message"
      :title="message"
      :type="messageType"
      show-icon
      class="message-alert"
      @close="message = null"
      closable
    />
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import userService from '../services/userService'
import { useAuth } from '../composables/useAuth'
import { Edit, Delete, DeleteFilled, Document, SwitchButton } from '@element-plus/icons-vue'

const { user, setUser } = useAuth()

const infoForm = ref({ username: '', email: '' })
const infoLoading = ref(false)
const passwordLoading = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', newPasswordConfirm: '' })

const message = ref(null)
const messageType = ref('success')

onMounted(() => {
  if (user.value) {
    infoForm.value.username = user.value.username
    infoForm.value.email = user.value.email
  }
})

function handleLogout() {
  userService.logout()
}

const handleUpdateInfo = async () => {
  infoLoading.value = true
  message.value = null
  try {
    const response = await userService.updateProfile({
      username: infoForm.value.username,
      email: infoForm.value.email
    })
    setUser(response.data)
    message.value = "Informations personnelles mises à jour avec succès."
    messageType.value = "success"
  } catch (err) {
    message.value = err.response?.data?.message || "Erreur lors de la mise à jour."
    messageType.value = "error"
  } finally {
    infoLoading.value = false
  }
}

const handleUpdatePassword = async () => {
  message.value = null

  if (passwordForm.value.newPassword !== passwordForm.value.newPasswordConfirm) {
    message.value = "Le nouveau mot de passe et sa confirmation ne correspondent pas."
    messageType.value = "error"
    return
  }

  passwordLoading.value = true
  try {
    await userService.updatePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    message.value = "Mot de passe modifié avec succès."
    messageType.value = "success"

    passwordForm.value.oldPassword = ''
    passwordForm.value.newPassword = ''
    passwordForm.value.newPasswordConfirm = ''
  } catch (err) {
    const response = err.response?.data
    if (response?.errors && Array.isArray(response.errors)) {
      message.value = response.errors.join(', ')
    } else {
      message.value = response?.message || "Erreur lors de la modification du mot de passe."
    }
    messageType.value = "error"
  } finally {
    passwordLoading.value = false
  }
}

const handleDeleteAccount = async () => {
  try {
    await userService.deleteAccount()
    userService.logout()
  } catch (err) {
    message.value = err.response?.data?.message || "Erreur lors de la suppression du compte."
    messageType.value = "error"
  }
}

const handleAccessData = async () => {
  try {
    const response = await userService.getPersonalData()
    const json = JSON.stringify(response.data, null, 2)
    const blob = new Blob([json], { type: 'application/json' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = 'mes_donnees.json'
    link.click()
    message.value = "Vos données personnelles ont été téléchargées."
    messageType.value = "success"
  } catch (err) {
    message.value = err.response?.data?.message || "Erreur lors de la récupération des données."
    messageType.value = "error"
  }
}

const handleDeleteData = async () => {
  try {
    await userService.deletePersonalData()
    userService.logout()
  } catch (err) {
    message.value = err.response?.data?.message || "Erreur lors de la suppression des données."
    messageType.value = "error"
  }
}

</script>

<style scoped>

.page {
  padding: 40px;
  max-width: 1200px;
  margin: auto;
  background: #f8f9fa;
  border-radius: 16px;
}

.title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 30px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
}

.profile-card {
  padding: 20px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.profile-card.danger {
  border: 1px solid #f56c6c;
}

.section-header {
  margin-bottom: 20px;
  font-weight: bold;
  font-size: 1.2rem;
}

.logout {
  margin-top: 30px;
  text-align: center;
}

.message-alert {
  max-width: 600px;
  margin: 40px auto 0;
}

</style>