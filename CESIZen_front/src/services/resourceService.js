import api from '@/services/axiosClient'

const resourceService = {
  getResources() {
    return api.get('/resources')
  },
  
  getResourceById(id) {
    return api.get(`/resources/${id}`)
  },

  deleteResource(id) {
    return api.delete(`/resources/${id}`)
  },
  createResource(data) {
    return api.post('/resources', data)
  },
  updateResource(id, data) {
    return api.patch(`/resources/${id}`, data)
  }
}

export default resourceService