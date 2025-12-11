<!-- src/components/main_bootstrap.vue -->
<template>
  <div class="home">
    <section class="hero">
      <h2>Willkommen beim Memory Game</h2>

      <p class="lead">
        Dies ist die Startseite. Klicke auf
        <strong>Start</strong>, um die Vue-Version des Spiels zu öffnen.
      </p>

      <button class="start-button" @click="goToGame">
        ▶ Start
      </button>
    </section>
  </div>
</template>

<script>
export default {
  name: 'MainBootstrap',
  methods: {
    goToGame() {
      // Route zur Spielseite
      this.$router.push({ name: 'Game' })
    },
  },
}
</script>

<style scoped>
.home {
  display: flex;
  justify-content: center;
  align-items: center;
}

.hero {
  max-width: 480px;
  text-align: center;
}

.lead {
  margin-top: 0.5rem;
  margin-bottom: 1.5rem;
}

.start-button {
  padding: 0.75rem 2rem;
  font-size: 1.1rem;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  background: #42b983;
  color: white;
}

.start-button:hover {
  opacity: 0.9;
}
</style>
