/* eslint-disable no-console 

import { register } from 'register-service-worker'

if (process.env.NODE_ENV === 'production') {
  register(`${process.env.BASE_URL}service-worker.js`, {
    ready() {
      console.log('PWA: Service worker active (cached for offline use).')
    },
    registered() {
      console.log('PWA: Service worker registered.')
    },
    cached() {
      console.log('PWA: Content has been cached for offline use.')
    },
    updatefound() {
      console.log('PWA: New content is downloading.')
    },
    updated() {
      console.log('PWA: New content is available; please refresh.')
    },
    offline() {
      console.log('PWA: No internet connection found. App is running in offline mode.')
    },
    error(error) {
      console.error('PWA: Error during service worker registration:', error)
    }
  })
}*/
