import { createPinia } from 'pinia'
import { useLayoutStore } from './pinia/layout'
import { useAuthStore } from './pinia/auth'

const pinia = createPinia()
export default pinia
export { useLayoutStore, useAuthStore }
