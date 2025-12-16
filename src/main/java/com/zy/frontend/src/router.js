import { createRouter, createWebHistory } from 'vue-router'
import HomeDashboard from './components/HomeDashboard.vue'
import BlastMonitoring from './components/BlastMonitoring.vue'
import EnvironmentalAnalysis from './components/EnvironmentalAnalysis.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeDashboard
  },
  {
    path: '/blast-monitoring',
    name: 'BlastMonitoring',
    component: BlastMonitoring
  },
  {
    path: '/environmental',
    name: 'EnvironmentalAnalysis',
    component: EnvironmentalAnalysis
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router