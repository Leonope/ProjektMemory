// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/main_bootstrap.vue'
import MemoryGameView from '../views/MemoryGameView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage,
  },
  {
    path: '/game',
    name: 'Game',
    component: MemoryGameView,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
