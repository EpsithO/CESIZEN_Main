import api from '@/services/axiosClient'

const emotionTypeService = {
  getAllLevels() {
    return api.get('/emotions') // qui retourne tous les level1 avec leurs children
  },

  createLevel1(name) {
    return api.post('/emotions/level1', { name })
  },

  updateLevel1(id, name) {
    return api.put(`/emotions/level1/${id}`, { name })
  },

  deleteLevel1(id) {
    return api.delete(`/emotions/level1/${id}`)
  },

  createLevel2(parentId, name) {
    return api.post(`/emotions/level2`, { parentId, name })
  },

  updateLevel2(id, name) {
    return api.put(`/emotions/level2/${id}`, { name })
  },

  deleteLevel2(id) {
    return api.delete(`/emotions/level2/${id}`)
  }
}

export default emotionTypeService
