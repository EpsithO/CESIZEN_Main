<template>
  <section class="resources-wrapper">
    <div class="resources-hero">
      <h1 class="hero-title">Explorez Nos Ressources</h1>
      <p class="hero-subtitle">Des conseils, articles et outils pour mieux vivre au quotidien.</p>
    </div>

    <div class="resource-list">
      <div
        class="resource-box"
        v-for="resource in resources"
        :key="resource.id"
      >
      <div class="resource-image-wrapper">
        <img
          :src="resource.imageUrl || 'https://images.pexels.com/photos/3184465/pexels-photo-3184465.jpeg?auto=compress&cs=tinysrgb&h=300&w=600'"
          alt="Image de la ressource"
          class="resource-cover"
        />
      </div>
        <div class="resource-body">
          <h2 class="resource-title">{{ resource.title }}</h2>
          <p class="resource-snippet">{{ truncate(resource.content, 180) }}</p>
          <el-button
            type="success"
            @click="readMore(resource.id)"
            class="read-more"
          >
            Découvrir
          </el-button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import resourceService from '@/services/resourceService'

const resources = ref([])

onMounted(async () => {
  try {
    const response = await resourceService.getResources()
    resources.value = response.data
  } catch (err) {
    console.error('Erreur chargement ressources :', err)
  }
})

function truncate(text, length) {
  return text.length > length ? text.slice(0, length) + '...' : text
}

function readMore(id) {
  window.location.href = `/ressources/${id}`
}
</script>

<style scoped>
/* Hero section */
.resources-wrapper {
  background: linear-gradient(to bottom right, #e8f5e9, #fffde7);
  padding: 60px 20px;
  min-height: 100vh;
}

.resources-hero {
  text-align: center;
  margin-bottom: 40px;
}

.hero-title {
  font-size: 2.5rem;
  color: #388e3c;
  margin-bottom: 12px;
  font-weight: bold;
}

.hero-subtitle {
  font-size: 1.1rem;
  color: #666;
}

/* Cards */
.resource-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.resource-box {
  background-color: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease;
}

.resource-box:hover {
  transform: translateY(-6px);
}

.resource-image-wrapper {
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.resource-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.resource-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.resource-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2e7d32;
}

.resource-snippet {
  font-size: 0.95rem;
  color: #444;
  flex-grow: 1;
}

.read-more {
  align-self: flex-start;
  background-color: #43a047;
  color: white;
  border-radius: 20px;
  padding: 8px 20px;
}

.read-more:hover {
  background-color: #388e3c;
}
</style>
