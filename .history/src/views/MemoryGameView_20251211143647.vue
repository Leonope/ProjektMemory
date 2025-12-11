<template>
  <div class="game-page">
    <header class="game-header">
      <h2>Memory Game – Vue Version</h2>
      <button class="back-button" @click="goHome">
        ⬅ Zurück zur Startseite
      </button>
    </header>

    <section class="game-content">
      <!--
        HIER kommt später deine echte Memory-App hin.
        Zum Beispiel dein Board, Statusbar, Setup etc.
      -->

      <p class="placeholder">
        Hier wird die Memory-Vue-App eingebunden.
        <br />
        Du kannst hier deine bestehenden Vue-Komponenten oder Logik aus
        <code>vue-memory.js</code> integrieren.
      </p>
    </section>
  </div>
</template>

<script>
export default {
  name: 'MemoryGameView',
  methods: {
    goHome() {
      this.$router.push({ name: 'Home' })
    },
  },
}
</script>

<style scoped>
.game-page {
  max-width: 960px;
  margin: 0 auto;
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

.placeholder {
  padding: 1rem;
  border-radius: 8px;
  border: 1px dashed #bbb;
  background: #fafafa;
}
</style>
<template>
  <div class="game-page">
    <game-setup />
    <memory-board />
  </div>
</template>

<script>
import GameSetup from '../components/GameSetup.vue'
import MemoryBoard from '../components/MemoryBoard.vue'

export default {
  name: 'MemoryGameView',
  components: {
    GameSetup,
    MemoryBoard,
  },
}
</script>
