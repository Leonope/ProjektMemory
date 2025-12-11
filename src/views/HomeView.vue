<template>
  <div class="home">
    <section class="hero">
      <h2>Willkommen beim Memory Game</h2>
      <p>
        Klicke auf <strong>Start</strong>, um die Vue-Version des Spiels zu starten.
      </p>

      <button class="start-button" @click="goToGame">
        ▶ Start
      </button>
    </section>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  methods: {
    goToGame() {
      // navigiert zur /game Route
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
  text-align: center;
  max-width: 480px;
}

.start-button {
  margin-top: 1.5rem;
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
