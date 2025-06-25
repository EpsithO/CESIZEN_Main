<template>
  <div class="landing-wrapper">
    <section class="hero">
      <h1 class="hero-title">Gérez votre stress avec <span>CESIZen</span></h1>
      <p class="hero-subtitle">La plateforme intelligente pour comprendre, suivre et améliorer votre bien-être émotionnel.</p>
    </section>

    <section class="features">
      <div class="feature-box">
        <h2>📅 Suivi quotidien</h2>
        <p>Ajoutez vos émotions au quotidien et observez votre progression sur le long terme.</p>
      </div>
      <div class="feature-box">
        <h2>📚 Ressources bien-être</h2>
        <p>Articles, vidéos, et conseils d’experts pour vous guider vers plus de sérénité.</p>
      </div>
    </section>

    <section class="cta-section">
      <h2>Rejoignez dès maintenant la communauté CESIZen</h2>
      <el-button type="success" class="cta-button" tag="router-link" :to="ressourcesLink">Découvrir les ressources</el-button>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuth } from '@/composables/useAuth'

const { user } = useAuth()

const diagnosticsLink = computed(() =>
  user.value?.role?.name === 'ADMIN' ? '/admin/diagnostics' : '/diagnostics'
)

const ressourcesLink = computed(() =>
  user.value?.role?.name === 'ADMIN' ? '/admin/ressources' : '/ressources'
)
</script>

<style scoped>
/* Wrapper */
.landing-wrapper {
  font-family: 'Segoe UI', sans-serif;
  background: linear-gradient(145deg, #e8f5e9, #fffde7);
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Hero section */
.hero {
  text-align: center;
  max-width: 800px;
  margin-bottom: 60px;
}

.logo {
  max-width: 160px;
  margin-bottom: 20px;
}

.hero-title {
  font-size: 2.5rem;
  margin-bottom: 10px;
  color: #2e2e2e;
}

.hero-title span {
  color: #4caf50;
}

.hero-subtitle {
  font-size: 1.2rem;
  color: #4e4e4e;
  margin-bottom: 30px;
}

.cta-button {
  font-size: 1rem;
  padding: 12px 28px;
  border-radius: 30px;
  background-color: #4caf50;
  border: none;
  color: white;
  transition: background 0.3s;
}

.cta-button:hover {
  background-color: #388e3c;
}

/* Feature section */
.features {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  justify-content: center;
  margin-bottom: 60px;
}

.feature-box {
  background: white;
  border-radius: 20px;
  padding: 30px;
  max-width: 280px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.2s;
}

.feature-box:hover {
  transform: translateY(-5px);
}

.feature-box h2 {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: #4caf50;
}

.feature-box p {
  font-size: 1rem;
  color: #555;
}

/* CTA Section */
.cta-section {
  background-color: #4caf50;
  padding: 40px 20px;
  border-radius: 30px;
  text-align: center;
  color: white;
  max-width: 700px;
}

.cta-section h2 {
  font-size: 1.8rem;
  margin-bottom: 20px;
}
</style>
