<template>
  <div class="not-found">
    <h1 class="main-title">404 - Page non trouvée</h1>
    <p class="paragraph">La page que vous cherchez n'existe pas.</p>

    <el-button type="success" class="home-button" @click="$router.push({name: 'Home'})">
      Retour à l'accueil
    </el-button>
  </div>
</template>

<style scoped>
.home-button {
  display: block;
  margin: 0 auto;
}
</style>