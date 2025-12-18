<!-- src/App.vue -->
<template>
  <div id="app">
    <header class="app-header">
      <h1>Memory Game</h1>
    </header>

    <main class="app-main">
      <!-- Hier erscheinen Home oder Game, je nach Route -->
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>
