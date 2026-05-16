import { createWebHistory, createRouter } from 'vue-router'
import routes from './routes'

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { x: 0, y: 0 }
  },
})

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('esun_token')
  const role  = sessionStorage.getItem('esun_role')

  if (to.meta.requiresAdmin) {
    if (!token || role !== 'ADMIN') return next('/admin/login')
  }
  if (to.meta.requiresMember) {
    if (!token || role !== 'MEMBER') return next('/login')
  }
  next()
})

router.beforeResolve((to, from, next) => {
  document.title = (to.meta.title || 'E-Commerce') + ' | 玉山電商'
  next()
})

export default router
