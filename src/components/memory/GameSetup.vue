<template>
  <div class="game-setup-container">
    <h1 class="mb-4 text-center">Memory – Vue SPA</h1>

    <div class="alert alert-info text-center mb-3">
      Wähle Name, Spieleranzahl und Anzahl Paare für die Vue-Version.
    </div>

    <form @submit.prevent="$emit('start')" class="row g-3 mb-4 justify-content-center">
      <div class="col-12 col-md-4">
        <label for="playerName" class="form-label">Name</label>
        <input
          id="playerName"
          type="text"
          class="form-control"
          :value="playerName"
          @input="$emit('update:playerName', $event.target.value)"
          placeholder="Dein Name"
        >
      </div>

      <div class="col-12 col-md-4">
        <label for="pairs" class="form-label">Anzahl Paare</label>
        <input
          id="pairs"
          type="number"
          min="2"
          max="18"
          step="2"
          class="form-control"
          :value="pairs"
          @input="$emit('update:pairs', parseInt($event.target.value || '2', 10))"
        >
      </div>

      <div class="col-12 col-md-4">
        <label for="playerCount" class="form-label">Anzahl Spieler</label>
        <select
          id="playerCount"
          class="form-select"
          :value="playerCount"
          @change="$emit('update:playerCount', parseInt($event.target.value || '1', 10))"
        >
          <option :value="1">1</option>
          <option :value="2">2</option>
          <option :value="3">3</option>
          <option :value="4">4</option>
        </select>
      </div>

      <div class="col-12 text-center">
        <button type="submit" class="btn btn-primary px-4">
          Vue-Spiel starten
        </button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  name: 'GameSetup',
  props: {
    playerName: {
      type: String,
      default: ''
    },
    playerCount: {
      type: Number,
      default: 1
    },
    pairs: {
      type: Number,
      default: 4
    }
  },
  emits: ['update:playerName', 'update:playerCount', 'update:pairs', 'start']
}
</script>

<style scoped>
.game-setup-container {
  max-width: 960px;
  margin: 0 auto;
}
</style>
