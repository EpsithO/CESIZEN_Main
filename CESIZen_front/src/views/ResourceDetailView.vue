<template>
  <div class="resource-detail">
    <div class="resource-card">
      <img
        v-if="resource.imageUrl"
        :src="resource.imageUrl"
        alt="Image de la ressource"
        class="resource-image"
      />

      <h1 class="resource-title">{{ resource.title }}</h1>

      <p class="resource-description">{{ resource.content }}</p>
    </div>

    <el-button
      type="primary"
      class="back-button"
      @click="$router.push({ name: 'Ressources' })"
    >
      ← Retour aux ressources
    </el-button>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import resourceService from '@/services/resourceService'
import logo from '@/assets/images/big-logo.png'

const route = useRoute()
const resource = ref({})
const defaultLogo = logo

onMounted(async () => {
  try {
    const response = await resourceService.getResourceById(route.params.id)
    resource.value = response.data
  } catch (err) {
    console.error('Erreur chargement ressource :', err)
  }
})
</script>

<style scoped>
.resource-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  background: linear-gradient(to bottom right, #f8f9fa, #eef2f5);
  min-height: 100vh;
  font-family: 'Inter', 'Helvetica Neue', sans-serif;
}

.resource-card {
  background: #ffffff;
  border-radius: 24px;
  max-width: 800px;
  width: 100%;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05);
  text-align: center;
  position: relative;
}

.resource-image {
  width: 100%;
  height: auto;
  max-height: 400px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 30px;
  box-shadow: 0 10px 20px rgba(0,0,0,0.06);
}

.resource-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2e2e2e;
  margin-bottom: 20px;
}

.resource-description {
  font-size: 1.15rem;
  line-height: 1.8;
  color: #444;
  white-space: pre-line;
  text-align: left;
}

.back-button {
  margin-top: 40px;
  background-color: #2c3e50;
  border-radius: 12px;
  font-weight: 600;
  padding: 10px 24px;
  color: white;
}

.back-button:hover {
  background-color: #1f2c38;
}
</style>

