<template>
  <div class="admin-panel">
    <h1 class="page-title">Panneau d'administration</h1>

    <section class="admin-section">
      <h2>Utilisateurs</h2>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id"        label="ID" width="60" />
        <el-table-column prop="username"  label="Nom d'utilisateur" />
        <el-table-column prop="email"     label="Email" />
        <el-table-column prop="enabled"   label="Actif">
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

    <section class="admin-section">
      <h2>Gestion des émotions</h2>

      <!-- Ajout niveau 1 -->
      <el-form @submit.prevent="createEmotion">
        <el-input v-model="newEmotion.level1" placeholder="Émotion niveau 1" class="form-input" />
        <el-button type="primary" native-type="submit">Ajouter</el-button>
      </el-form>

      <!-- Liste émotions -->
      <ul class="emotion-list">
        <li v-for="emotion in emotions" :key="emotion.id">
          <div class="level1-row">
            <strong>{{ emotion.name }}</strong>

            <!-- bouton suppression niveau 1 -->
            <el-button
              size="small"
              type="danger"
              @click="deleteEmotionLevel1(emotion.id)"
            >
              Supprimer
            </el-button>
          </div>

          <!-- Enfants niveau 2 -->
          <ul class="child-list">
            <li v-for="child in emotion.children" :key="child.id" class="child-row">
              {{ child.name }}

              <el-button
                size="small"
                type="danger"
                @click="deleteEmotionLevel2(child.id)"
              >
                x
              </el-button>
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

    <section class="admin-section">
      <h2>Gestion des ressources</h2>
      <el-form @submit.prevent="createResource">
        <el-input v-model="newResource.title"   placeholder="Titre"    class="form-input" />
        <el-input v-model="newResource.content" placeholder="Contenu" type="textarea" class="form-input" />
        <el-button type="success" native-type="submit">Ajouter</el-button>
      </el-form>

      <el-table :data="resources" style="width: 100%; margin-top: 20px">
        <el-table-column prop="id"    label="ID"    width="50" />
        <el-table-column prop="title" label="Titre" />
        <el-table-column label="Actions">
          <template #default="scope">
            <div v-if="!scope.row.editing">
              <el-button size="small" @click="enableEdit(scope.row)">Modifier</el-button>
              <el-button size="small" type="danger" @click="deleteResource(scope.row.id)">Supprimer</el-button>
            </div>
            <div v-else>
              <el-input v-model="scope.row.updatedTitle"   placeholder="Titre"    size="small" />
              <el-input v-model="scope.row.updatedContent" placeholder="Contenu" type="textarea" size="small" />
              <el-button size="small" type="success" @click="saveEdit(scope.row)">Enregistrer</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted }            from 'vue'
import { ElMessageBox, ElMessage }   from 'element-plus'
import userService     from '@/services/userService'
import resourceService from '@/services/resourceService'
import emotionService  from '@/services/emotionService'

const users      = ref([])
const emotions   = ref([])
const resources  = ref([])

const newEmotion  = ref({ level1: '' })
const newResource = ref({ title: '', content: '' })

const fetchUsers = async () => {
  users.value = (await userService.getAllUsers()).data
}
const toggleUserStatus = async (u) => {
  await (u.enabled ? userService.deactivateUser(u.id)
                   : userService.activateUser(u.id))
  fetchUsers()
}
const deleteUser = async (id) => {
  await userService.deleteUser(id)
  fetchUsers()
}

const fetchEmotions = async () => {
  const { data } = await emotionService.getEmotionLevel1()
  emotions.value = data.map(e => ({ ...e, newChild: '' }))
}

const createEmotion = async () => {
  if (!newEmotion.value.level1) return
  await emotionService.createEmotionLevel1({ name: newEmotion.value.level1 })
  newEmotion.value.level1 = ''
  fetchEmotions()
}

const createEmotionLevel2 = async (parentId, name) => {
  if (!name) return
  await emotionService.createEmotionLevel2({ parentId, name })
  fetchEmotions()
}

const deleteEmotionLevel1 = async (id) => {
  await confirmAndRun(
    'Supprimer cette émotion niveau 1 (et tous ses enfants) ?',
    () => emotionService.deleteEmotionLevel1(id)
  )
}

const deleteEmotionLevel2 = async (id) => {
  await confirmAndRun(
    'Supprimer cette émotion niveau 2 ?',
    async () => {
      try {
        console.log('[DELETE] /emotion/level2/', id)
        const res = await emotionService.deleteEmotionLevel2(id)
        console.log('Réponse:', res)
      } catch (e) {
        if (e.response) {
          console.error('Status', e.response.status)
          console.error('Data',   e.response.data)
        } else {
          console.error(e)
        }
        throw e         
      }
    }
  )
}


async function confirmAndRun(message, apiCall) {
  try {
    await ElMessageBox.confirm(message, 'Confirmer', {
      type: 'warning',
      confirmButtonText: 'Supprimer',
      cancelButtonText : 'Annuler'
    })
    await apiCall()
    ElMessage.success('Suppression effectuée')
    fetchEmotions()
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('Échec de la suppression')
  }
}

const fetchResources = async () => {
  const { data } = await resourceService.getResources()
  resources.value = data.map(r => ({
    ...r,
    editing: false,
    updatedTitle:   r.title,
    updatedContent: r.content
  }))
}

const createResource = async () => {
  await resourceService.createResource(newResource.value)
  newResource.value.title = newResource.value.content = ''
  fetchResources()
}
const deleteResource = async (id) => {
  await resourceService.deleteResource(id)
  fetchResources()
}
const enableEdit = (r) => { r.editing = true }
const saveEdit   = async (r) => {
  await resourceService.updateResource(r.id, {
    title:   r.updatedTitle,
    content: r.updatedContent
  })
  r.editing = false
  fetchResources()
}

onMounted(() => {
  fetchUsers()
  fetchEmotions()
  fetchResources()
})
</script>

<style scoped>
.admin-panel      { max-width: 1000px; margin: 40px auto; padding: 30px;
                    background:#f9f9f9; border-radius:20px;
                    box-shadow:0 4px 16px rgba(0,0,0,.1);}
.page-title       { text-align:center; font-size:2rem; margin-bottom:40px; color:#2e7d32;}
.admin-section    { margin-bottom:40px; background:white; padding:20px;
                    border-radius:12px; box-shadow:0 2px 6px rgba(0,0,0,.05);}
.admin-section h2 { margin-bottom:20px; font-size:1.4rem; color:#388e3c;}
.form-input       { margin-bottom:16px; display:block;}

.emotion-list     { list-style:none; padding:0; margin-top:20px;}
.level1-row       { display:flex; align-items:center; gap:10px;}
.child-list       { list-style:none; margin:6px 0 10px 20px; padding:0;}
.child-row        { display:flex; align-items:center; gap:8px; margin:3px 0;}
</style>
