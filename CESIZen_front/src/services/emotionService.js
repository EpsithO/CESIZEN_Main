import api from '@/services/axiosClient'

const emotionService = {
  // 🧠 Récupérer tous les trackings d’émotions de l’utilisateur connecté
  getTrackers() {
    return api.get('/api/trackers')
  },

  // ➕ Créer un nouveau suivi émotionnel
  createTracker(data) {
    return api.post('/api/trackers', data)
  },

  // 📝 Mettre à jour un suivi existant
  updateTracker(id, data) {
    return api.put(`/api/trackers/${id}`, data)
  },

  // 🗑 Supprimer un suivi émotionnel
  deleteTracker(id) {
    return api.delete(`/api/trackers/${id}`)
  },

  // 📊 Générer un rapport (ex: ?period=week, month, etc.)
  getEmotionReport(period = 'month') {
    return api.get(`/api/trackers/report?period=${period}`)
  },

  // ✅ Gestion des émotions (back office)

  // 🔹 Récupérer les émotions de niveau 1
  getEmotionLevel1() {
    return api.get('/api/emotions/level1')
  },

  // 🔹 Créer une émotion de niveau 1
  createEmotionLevel1({ name }) {
    return api.post('/api/emotions/level1', { name })
  },

  // 🔹 Créer une émotion de niveau 2 rattachée à une de niveau 1
  createEmotionLevel2({ parentId, name }) {
    return api.post('/api/emotions/level2', { parentId, name })
  }
}

export default emotionService
