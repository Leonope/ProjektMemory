<!-- filepath: src/components/MainBootstrap.vue -->
<template>
  <div>
    <!-- Hintergrund-Canvas (Sternschnuppen) -->
    <canvas id="stars-canvas" aria-hidden="true"></canvas>

    <nav class="navbar navbar-dark position-relative" style="z-index: 10;">
      <div class="container">
        <a class="navbar-brand" href="/">Memory</a>
      </div>
    </nav>

    <main class="container py-4 position-relative" style="z-index: 10;">
      <!-- Slot for page content -->
      <slot />
    </main>
  </div>
</template>

<script>
export default {
  name: 'MainBootstrap',
  mounted() {
    // Load external JS for Sternschnuppen if needed
    const script = document.createElement('script');
    script.src = '/javascripts/stars.js';
    script.async = true;
    document.body.appendChild(script);
  }
};
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css');
@import url('/stylesheets/main.css');
@import url('/stylesheets/stars.css');

body {
  background-color: #212529;
  color: #f8f9fa;
}
</style>