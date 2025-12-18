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
