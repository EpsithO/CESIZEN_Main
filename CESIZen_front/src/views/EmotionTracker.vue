<template>
  <div class="tracker-container">
    <h1 class="tracker-title">Comment vous sentez-vous aujourd'hui&nbsp;?</h1>

    <el-select
      v-model="selectedEmotion"
      placeholder="Sélectionnez une émotion"
      class="emotion-select"
      filterable
    >
      <el-option-group
        v-for="cat in emotionCategories"
        :key="cat.id"
        :label="cat.name"
      >
        <el-option
          v-for="emo in cat.children"
          :key="emo.id"
          :label="emo.name"
          :value="emo"
        />
      </el-option-group>
    </el-select>

    <el-button
      type="success"
      @click="submitEmotion"
      :loading="submitting"
      class="submit-button"
    >
      Enregistrer
    </el-button>

    <el-alert
      v-if="successMessage"
      :title="successMessage"
      type="success"
      show-icon
      class="alert"
    />

    <el-button
      v-if="reportsAvailable"
      type="primary"
      @click="goToReports"
      class="report-button"
    >
      Voir mes rapports émotionnels
    </el-button>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import emotionService from '@/services/emotionService'

const emotionCategories = ref([])
const selectedEmotion = ref(null)
const description = ref('')
const successMessage = ref('')
const errorMessage = ref('')
const submitting = ref(false)
const reportsAvailable = ref(false)

onMounted(async () => {
  try {
    const { data } = await emotionService.getEmotionLevel1()
    emotionCategories.value = data
  } catch (err) {
    console.error('Erreur récupération émotions:', err)
    errorMessage.value = 'Impossible de charger les émotions'
  }
})

async function submitEmotion() {
  if (!selectedEmotion.value) {
    errorMessage.value = 'Veuillez sélectionner une émotion'
    return
  }

  try {
    await emotionService.createTracker({
      emotionId: selectedEmotion.value.id,
      description: description.value,
      date: new Date().toISOString().split('T')[0], // format YYYY-MM-DD
    })

    successMessage.value = 'Émotion enregistrée avec succès !'
    selectedEmotion.value = null
    description.value = ''
    errorMessage.value = ''
  } catch (err) {
    console.error('Erreur save émotion:', err)
    errorMessage.value = 'Erreur lors de l\'enregistrement'
  }
}
</script>



<style scoped>
.tracker-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.1);
  background: linear-gradient(135deg, #f0fff0, #e8ffe8);
  text-align: center;
}
.tracker-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #2e7d32;
  font-weight: bold;
}
.emotion-select {
  width: 100%;
  margin-bottom: 20px;
}
.submit-button {
  width: 100%;
  border-radius: 30px;
  font-weight: bold;
}
.alert {
  margin-top: 20px;
}
</style>