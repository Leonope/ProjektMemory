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

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  min-height: 100vh;
  margin: 0;
  display: flex;
  flex-direction: column;
}

.app-header {
  padding: 1rem 2rem;
  border-bottom: 1px solid #ddd;
}

.app-main {
  flex: 1;
  padding: 2rem;
}
</style>
