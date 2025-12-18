const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: process.env.NODE_ENV === 'production'
    ? '/ProjektMemory/'
    : '/',
  pwa: {
    workboxPluginMode: 'GenerateSW',
    workboxOptions: {
      navigateFallback: '/index.html',
      runtimeCaching: [
        // Offline-Fallback für Navigation (wenn index.html nicht verfügbar wäre)
      ]
    }
  }
})
/*
module.exports = {
  pwa: {
    workboxPluginMode: 'GenerateSW',
    workboxOptions: {
      navigateFallback: '/index.html',
      runtimeCaching: [
        // Offline-Fallback für Navigation (wenn index.html nicht verfügbar wäre)
      ]
    }
  }
}
module.exports = {
  publicPath: process.env.NODE_ENV === 'production'
    ? '/a/'
    : '/'
}*/