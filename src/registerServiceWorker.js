/* eslint-disable no-console */
import { register } from 'register-service-worker'

if (process.env.NODE_ENV === 'production') {
  register(`${process.env.BASE_URL}service-worker.js`, {
    ready() {
      console.log('PWA: SW active.')
    },
    registered() {
      console.log('PWA: SW registered.')
    },
    cached() {
      console.log('PWA: Content cached for offline.')
    },
    updatefound() {
      console.log('PWA: Update found.')
    },
    updated() {
      console.log('PWA: Updated; refresh.')
    },
    offline() {
      console.log('PWA: Offline mode.')
    },
    error(error) {
      console.error('PWA: SW registration error:', error)
    }
  })
}
