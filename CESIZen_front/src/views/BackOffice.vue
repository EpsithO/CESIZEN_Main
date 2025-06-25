<template>
  <div class="admin-panel">
    <h1 class="page-title">Panneau d'administration</h1>

    <!-- Gestion utilisateurs -->
    <section class="admin-section">
      <h2>Utilisateurs</h2>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="Nom d'utilisateur" />
        <el-table-column prop="email" label="Email" />
        <el-table-column prop="enabled" label="Actif">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
              {{ scope.row.enabled ? 'Oui' : 'Non' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions">
          <template #default="scope">
            <el-button size="small" @click="toggleUserStatus(scope.row)">
              {{ scope.row.enabled ? 'Désactiver' : 'Activer' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">
              Supprimer
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- Gestion des émotions -->
    <section class="admin-section">
      <h2>Gestion des émotions</h2>
      <el-form @submit.prevent="createEmotion">
        <el-input v-model="newEmotion.level1" placeholder="Émotion niveau 1" class="form-input" />
        <el-button type="primary" native-type="submit">Ajouter</el-button>
      </el-form>

      <ul class="emotion-list">
        <li v-for="emotion in emotions" :key="emotion.id">
          <div style="display: flex; align-items: center; gap: 10px;">
            <strong>{{ emotion.name }}</strong>
            <el-button size="small" type="danger" @click="deleteEmotion(emotion.id)">
              Supprimer
            </el-button>
          </div>
          <ul>
            <li v-for="child in emotion.children" :key="child.id">
              - {{ child.name }}
            </li>
          </ul>
          <el-input
            v-model="emotion.newChild"
            size="small"
            placeholder="Ajouter une émotion niveau 2"
            style="width: 300px; margin-top: 5px"
          />
          <el-button
            size="small"
            type="primary"
            @click="createEmotionLevel2(emotion.id, emotion.newChild)"
          >
            Ajouter
          </el-button>
        </li>
      </ul>
    </section>

    <!-- Gestion des ressources -->
    <section class="admin-section">
      <h2>Gestion des ressources</h2>
      <el-form @submit.prevent="createResource">
        <el-input v-model="newResource.title" placeholder="Titre" class="form-input" />
        <el-input type="textarea" v-model="newResource.content" placeholder="Contenu" class="form-input" />
        <el-button type="success" native-type="submit">Ajouter</el-button>
      </el-form>

      <el-table :data="resources" style="width: 100%; margin-top: 20px">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="title" label="Titre" />
        <el-table-column label="Actions">
          <template #default="scope">
            <div v-if="!scope.row.editing">
              <el-button size="small" @click="enableEdit(scope.row)">Modifier</el-button>
              <el-button size="small" type="danger" @click="deleteResource(scope.row.id)">Supprimer</el-button>
            </div>
            <div v-else>
              <el-input v-model="scope.row.updatedTitle" placeholder="Titre" size="small" />
              <el-input v-model="scope.row.updatedContent" placeholder="Contenu" size="small" type="textarea" />
              <el-button size="small" type="success" @click="saveEdit(scope.row)">Enregistrer</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import userService from '@/services/userService'
import resourceService from '@/services/resourceService'
import emotionService from '@/services/emotionService'

const users = ref([])
const emotions = ref([])
const resources = ref([])

const newEmotion = ref({ level1: '' })
const newResource = ref({ title: '', content: '' })

async function fetchUsers() {
  const res = await userService.getAllUsers()
  users.value = res.data
}

async function toggleUserStatus(user) {
  if (user.enabled) {
    await userService.deactivateUser(user.id)
  } else {
    await userService.activateUser(user.id)
  }
  fetchUsers()
}

async function deleteUser(id) {
  await userService.deleteUser(id)
  fetchUsers()
}

// EMOTIONS
async function fetchEmotions() {
  const res = await emotionService.getEmotionLevel1()
  emotions.value = res.data.map(e => ({ ...e, newChild: '' }))
}

async function createEmotion() {
  if (!newEmotion.value.level1) return
  await emotionService.createEmotionLevel1({ name: newEmotion.value.level1 })
  newEmotion.value.level1 = ''
  fetchEmotions()
}

async function createEmotionLevel2(parentId, name) {
  if (!name) return
  await emotionService.createEmotionLevel2({ parentId, name })
  fetchEmotions()
}

async function deleteEmotion(id) {
  await emotionService.deleteEmotionLevel1(id)
  fetchEmotions()
}

// RESSOURCES
async function fetchResources() {
  const res = await resourceService.getResources()
  resources.value = res.data.map(r => ({
    ...r,
    editing: false,
    updatedTitle: r.title,
    updatedContent: r.content
  }))
}

async function createResource() {
  await resourceService.createResource(newResource.value)
  newResource.value.title = ''
  newResource.value.content = ''
  fetchResources()
}

async function deleteResource(id) {
  await resourceService.deleteResource(id)
  fetchResources()
}

function enableEdit(resource) {
  resource.editing = true
}

async function saveEdit(resource) {
  await resourceService.updateResource(resource.id, {
    title: resource.updatedTitle,
    content: resource.updatedContent
  })
  resource.editing = false
  fetchResources()
}

onMounted(() => {
  fetchUsers()
  fetchResources()
  fetchEmotions()
})
</script>

<style scoped>
.admin-panel {
  max-width: 1000px;
  margin: 40px auto;
  padding: 30px;
  background: #f9f9f9;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.page-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 40px;
  color: #2e7d32;
}

.admin-section {
  margin-bottom: 40px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.admin-section h2 {
  margin-bottom: 20px;
  font-size: 1.4rem;
  color: #388e3c;
}

.form-input {
  margin-bottom: 16px;
  display: block;
}

.emotion-list {
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.emotion-list li {
  margin-bottom: 15px;
  font-weight: 500;
}
</style>
