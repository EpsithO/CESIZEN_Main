<template>
  <div class="resource-detail">
    <div class="resource-card">
      <img
        :src="resource.imageUrl || fallbackImage"
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
const fallbackImage = 'https://images.pexels.com/photos/3184287/pexels-photo-3184287.jpeg?auto=compress&cs=tinysrgb&h=500&w=800'


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
  background: linear-gradient(to bottom right, #f1f8e9, #ffffff);
  min-height: 100vh;
  font-family: 'Inter', 'Segoe UI', sans-serif;
}

.resource-card {
  background: white;
  border-radius: 20px;
  max-width: 850px;
  width: 100%;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.resource-image {
  width: 100%;
  height: 320px;
  object-fit: cover;
  border-bottom: 1px solid #eee;
}

.resource-title {
  font-size: 2.3rem;
  font-weight: 700;
  color: #2e7d32;
  margin: 30px 30px 10px;
}

.resource-description {
  font-size: 1.125rem;
  color: #444;
  line-height: 1.8;
  padding: 0 30px 40px;
  white-space: pre-line;
  text-align: left;
}

.back-button {
  margin-top: 40px;
  background-color: #4caf50;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 1rem;
  font-weight: 600;
  color: white;
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #388e3c;
}

</style>

