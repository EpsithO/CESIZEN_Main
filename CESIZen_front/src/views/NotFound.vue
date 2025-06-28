<template>
  <div class="not-found">
    <h1 class="main-title">404 - Page non trouvée</h1>
    <p class="paragraph">La page que vous cherchez n'existe pas ou a été déplacée.</p>

  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 80vh;
  text-align: center;
  background-color: #f9fafb;
  padding: 2rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.main-title {
  font-size: 3rem;
  color: #222;
  margin-bottom: 1rem;
}

.paragraph {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 2rem;
}

.home-button {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
}
</style>
