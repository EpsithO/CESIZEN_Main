import api from '@/services/axiosClient'

const userService = {
  /**
   * Connexion avec identifiants
   */
  async login(username, password) {
    const response = await api.post('/auth/login', { username, password })
    const token = response.data.token
  
    console.log('✅ Token reçu :', token)
    localStorage.setItem('token', token)
  
    return this.getMe().then(res => res.data)
  },
  /**
   * Inscription
   */
  register({ username, email, password }) {
    return api.post('/auth/register', { username, email, password })
  },

  /**
   * Inscription + Connexion automatique
   */
  async registerAndLogin({ username, email, password }) {
    await this.register({ username, email, password })
    return await this.login(username, password)
  },

  /**
   * Création d'un utilisateur avec un rôle spécifique (admin only)
   */
  createUserWithRole({ username, email, password, role }) {
    return api.post('/auth/admin/users', {
      username,
      email,
      password,
      role
    })
  },

  /**
   * Récupère l'utilisateur connecté
   */
  getMe() {
    return api.get('/users/me')
  },

  /**
   * Mise à jour des infos personnelles
   */
  updateProfile({ username, email }) {
    return api.patch('/users/me', { username, email })
  },

  /**
   * Mise à jour du mot de passe
   */
  updatePassword({ oldPassword, newPassword }) {
    return api.patch('/users/me/password', { oldPassword, newPassword })
  },

  /**
   * Déconnexion + nettoyage localStorage
   */
  logout() {
    localStorage.removeItem('token')
    window.location.href = '/login'
  },

  /**
   * Supprimer le compte actuel
   */
  deleteAccount() {
    return api.delete('/users/me')
  },

  /**
   * Télécharger ses données personnelles (RGPD)
   */
  getPersonalData() {
    return api.get('/users/me/data', {
      responseType: 'blob' // si tu veux générer un téléchargement
    })
  },

  /**
   * Demander la suppression de ses données personnelles (RGPD)
   */
  deletePersonalData() {
    return api.delete('/users/me/data')
  },

  /**
   * Récupérer tous les utilisateurs (admin only)
   */
  getAllUsers() {
    return api.get('/users')
  },

  /**
   * Activer un utilisateur (admin only)
   */
  activateUser(id) {
    return api.patch(`/users/${id}/activate`)
  },

  /**
   * Désactiver un utilisateur (admin only)
   */
  deactivateUser(id) {
    return api.patch(`/users/${id}/deactivate`)
  },

  /**
   * Supprimer un utilisateur (admin only)
   */
  deleteUser(id) {
    return api.delete(`/users/${id}`)
  }
}

export default userService
