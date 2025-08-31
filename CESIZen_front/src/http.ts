import axios from 'axios';
const api = axios.create({ withCredentials: true });
api.defaults.xsrfCookieName = 'XSRF-TOKEN';
api.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
export default api;
