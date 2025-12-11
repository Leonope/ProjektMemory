<template>
  <div class="game-page">
    <header class="game-header">
      <h2>Memory Game – Vue Version</h2>
      <button class="back-button" @click="goHome">
        ⬅ Zurück zur Startseite
      </button>
    </header>

    <section class="game-content">
      <MemoryApp />
    </section>
  </div>
</template>

<script>
import MemoryApp from '../components/memory/MemoryApp.vue'

export default {
  name: 'MemoryGameView',
  components: {
    MemoryApp
  },
  methods: {
    goHome() {
      this.$router.push({ name: 'Home' })
    }
  }
}
</script>

<style scoped>
.game-page {
  max-width: 960px;
  margin: 0 auto;
  text-align: left;
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-button {
  padding: 0.4rem 1rem;
  border-radius: 999px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
}

.back-button:hover {
  background: #f3f3f3;
}

.game-content {
  margin-top: 1.5rem;
}
</style>
