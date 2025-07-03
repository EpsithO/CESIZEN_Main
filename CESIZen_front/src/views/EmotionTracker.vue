<template>
  <div class="tracker-container">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="Nouvelle émotion" name="track">
        <h1 class="tracker-title">Comment vous sentez-vous aujourd’hui&nbsp;?</h1>

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

        <el-input
          v-model="description"
          placeholder="Ajoutez une description (facultatif)"
          type="textarea"
          :rows="3"
          class="description-input"
        />

        <el-button
          type="success"
          @click="submitEmotion"
          :loading="submitting"
          class="submit-button"
        >
          Enregistrer
        </el-button>

        <el-alert v-if="successMessage" :title="successMessage" type="success" show-icon class="alert" />
        <el-alert v-if="errorMessage"   :title="errorMessage"   type="error"   show-icon class="alert" />

        <div v-if="previousEmotions.length" class="history">
          <h2 class="history-title">Émotions précédentes</h2>
          <ul>
            <li v-for="t in previousEmotions" :key="t.id">
              {{ t.date }} — <strong>{{ t.emotionName }}</strong> : {{ t.description || '—' }}
            </li>
          </ul>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Calendrier" name="calendar">
        <el-calendar v-model="calendarDate" :first-day-of-week="1" class="emo-calendar">
          <template #date-cell="{ data }">
            <div
              class="day-box"
              :class="emotionMap[data.day] && 'has-emo'"
              :style="emotionMap[data.day] && { '--emo-color': emotionMap[data.day].color }"
            >
              <span class="day-number">{{ data.day.split('-').pop() }}</span>
              <el-tooltip v-if="emotionMap[data.day]" placement="top">
                <template #content>
                  {{ emotionMap[data.day].name }}<br />
                  {{ emotionMap[data.day].description || '—' }}
                </template>
                <span class="emo-dot" />
              </el-tooltip>
              <span v-if="emotionMap[data.day]" class="emo-label">{{ emotionMap[data.day].name }}</span>
            </div>
          </template>
        </el-calendar>
      </el-tab-pane>

      <el-tab-pane label="Statistiques" name="report">
        <div class="report-header">
          <el-select v-model="reportPeriod" @change="fetchReport" class="period-select">
            <el-option label="Semaine" value="week" />
            <el-option label="Mois"   value="month" />
            <el-option label="Année"  value="year" />
          </el-select>
        </div>

        <el-alert v-if="reportError" :title="reportError" type="error" show-icon class="alert" />

        <div v-if="chartData.labels.length" class="chart-wrapper">
          <Bar :data="chartData" :options="chartOptions" />
        </div>
        <p v-else-if="!reportError" class="empty-report">Aucune donnée pour cette période.</p>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title, Tooltip, Legend, BarElement,
  CategoryScale, LinearScale
} from 'chart.js'
import emotionService from '@/services/emotionService'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const activeTab         = ref('track')
const calendarDate      = ref(new Date())
const emotionCategories = ref([])
const selectedEmotion   = ref(null)
const description       = ref('')
const submitting        = ref(false)
const successMessage    = ref('')
const errorMessage      = ref('')
const previousEmotions  = ref([])
const emotionMap        = reactive({})

// Couleurs personnalisées par type d’émotion
const emotionColorMap = {
  joie: '#42b983',
  colère: '#ef5350',
  peur: '#5c6bc0',
  tristesse: '#0097a7',
  surprise: '#ffb300',
  dégoût: '#8d6e63',
  neutre: '#90a4ae'
}

// Rapport
const reportPeriod = ref('month')
const reportError  = ref('')
const chartData    = reactive({
  labels: [],
  datasets: [{ label: 'Occurrences', data: [], backgroundColor: '#42b983' }]
})
const chartOptions = { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }

onMounted(async () => {
  await Promise.all([loadEmotions(), loadHistory(), loadTrackers(), fetchReport()])
})

function getEmotionColor(name = '') {
  const lower = name.toLowerCase()
  return emotionColorMap[lower] || '#42b983'
}

async function loadEmotions() {
  try {
    const { data } = await emotionService.getEmotionLevel1()
    emotionCategories.value = data
  } catch {
    errorMessage.value = 'Erreur chargement émotions'
  }
}

async function loadHistory() {
  try {
    const { data } = await emotionService.getTrackers()
    previousEmotions.value = data
  } catch {}
}

async function loadTrackers() {
  try {
    const { data } = await emotionService.getTrackers()
    data.forEach(t => {
      emotionMap[t.date] = {
        name: t.emotionName,
        description: t.description,
        color: getEmotionColor(t.emotionName)
      }
    })
  } catch {
    ElMessage.error('Impossible de charger le calendrier')
  }
}

async function submitEmotion() {
  if (!selectedEmotion.value) {
    errorMessage.value = 'Sélectionnez une émotion'
    return
  }

  submitting.value = true
  const today = new Date().toISOString().split('T')[0]

  try {
    await emotionService.createTracker({
      emotionId: selectedEmotion.value.id,
      description: description.value,
      date: today
    })

    // ⬇️ Ajoute l’émotion du jour immédiatement
    emotionMap[today] = {
      name: selectedEmotion.value.name,
      description: description.value,
      color: getEmotionColor(selectedEmotion.value.name)
    }

    successMessage.value = 'Émotion enregistrée !'
    setTimeout(() => successMessage.value = '', 2500)

    selectedEmotion.value = null
    description.value = ''

    await Promise.all([loadHistory(), fetchReport()])
  } catch {
    errorMessage.value = 'Erreur : impossible d’enregistrer'
  } finally {
    submitting.value = false
  }
}

async function fetchReport() {
  reportError.value = ''
  chartData.labels.length = 0
  chartData.datasets[0].data.length = 0

  try {
    const { data } = await emotionService.getEmotionReport(reportPeriod.value)
    if (!data || !Object.keys(data).length) return
    chartData.labels.push(...Object.keys(data))
    chartData.datasets[0].data.push(...Object.values(data))
  } catch {
    reportError.value = 'Impossible de générer les stats'
  }
}
</script>



<style scoped>
.tracker-container{max-width:900px;margin:40px auto;padding:30px;border-radius:18px;background:#f4fff4;box-shadow:0 8px 24px rgba(0,0,0,.08)}
.tracker-title{margin-bottom:20px;font-size:24px;color:#2e7d32;font-weight:700}
.emotion-select,.description-input{width:100%;margin-bottom:20px}
.submit-button,.period-select{width:100%;border-radius:30px;font-weight:700;margin-top:10px}
.alert{margin-top:20px}
.history{margin-top:30px;text-align:left}
.history-title{font-size:20px;font-weight:700;margin-bottom:10px}
.history ul{list-style:none;padding:0}
.history li{margin-bottom:6px}
.report-header{margin-bottom:20px}
.chart-wrapper{height:400px;margin-top:10px}
.empty-report{text-align:center;color:#666;margin-top:20px}
.emo-calendar{--el-calendar-border:none;box-shadow:0 4px 16px rgba(0,0,0,.05);border-radius:14px;overflow:hidden}
.day-box{display:flex;flex-direction:column;align-items:center;min-height:70px;padding:4px;transition:.2s}
.day-number{font-size:14px;font-weight:600;margin-bottom:2px}
.emo-dot{width:12px;height:12px;border-radius:50%;display:inline-block;background:var(--emo-color);margin-top:2px}
.emo-label{font-size:10px;margin-top:2px;color:var(--emo-color);text-align:center}
.has-emo{border:2px solid var(--emo-color);border-radius:10px}
.el-calendar-table .el-calendar-day:hover{background:rgba(66,185,131,.1)}

.has-emo {
  border: 2px solid var(--emo-color);
  border-radius: 10px;
  background-color: rgba(66, 185, 131, 0.05);
}

</style>
