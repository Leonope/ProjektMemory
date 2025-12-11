import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import MemoryGameView from '../views/MemoryGameView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
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
