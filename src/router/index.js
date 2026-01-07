import { createRouter, createWebHashHistory } from 'vue-router'
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
  history: createWebHashHistory(), // << use hash mode
  routes,
})

export default router
