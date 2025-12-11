// src/api.js
const API_URL = process.env.VUE_APP_API_URL || 'http://localhost:9000';

export function fetchData() {
  return fetch(`${API_URL}/cmd`).then(res => res.json());
}