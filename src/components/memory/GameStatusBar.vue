<template>
  <div class="row justify-content-center mb-3">
    <div class="col-12 col-md-10">
      <div class="card bg-secondary-subtle border-0 shadow-sm">
        <div class="card-body text-dark d-flex flex-wrap gap-3 align-items-center">
          <div>
            <strong>Spieler:</strong>
            <span v-if="playerName && playerName.trim().length > 0">
              {{ playerName }}
            </span>
            <em v-else>–</em>
          </div>
          <div><strong>Spieleranzahl:</strong> {{ playerCount }}</div>
          <div><strong>Paare:</strong> {{ pairs }}</div>
          <div class="ms-auto">
            <strong>Versuche:</strong> {{ moves }}
          </div>
          <div>
            <strong>Gefundene Paare:</strong> {{ found }} / {{ pairs }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameStatusBar',
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
    },
    moves: {
      type: Number,
      default: 0
    },
    found: {
      type: Number,
      default: 0
    }
  }
}
</script>

<style scoped>
/* optional, Bootstrap macht schon viel */
</style>
