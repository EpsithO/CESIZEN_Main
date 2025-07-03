import api from '@/services/axiosClient'

const emotionService = {
  getTrackers() {
    return api.get('/api/trackers')
  },

  createTracker(data) {
    return api.post('/api/trackers', data)
  },

  updateTracker(id, data) {
    return api.put(`/api/trackers/${id}`, data)
  },

  deleteTracker(id) {
    return api.delete(`/api/trackers/${id}`)
  },

  getEmotionReport(period = 'month') {
    return api.get(`/api/trackers/report?period=${period}`)
  },


  getEmotionLevel1() {
    return api.get('/api/emotions/level1')
  },

    deleteEmotionLevel2(id) {
      return api.delete(`/api/emotions/level2/${id}`)
    },

  createEmotionLevel1({ name }) {
    return api.post('/api/emotions/level1', { name })
  },

  createEmotionLevel2({ parentId, name }) {
    return api.post('/api/emotions/level2', { parentId, name })
  },


  deleteEmotionLevel1(id) {
    return api.delete(`/api/emotions/level1/${id}`)
  }
}

export default emotionService
