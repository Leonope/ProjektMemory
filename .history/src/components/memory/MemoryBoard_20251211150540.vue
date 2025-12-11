<template>
  <div
    id="board"
    class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-6 g-2 g-md-3 justify-content-center"
  >
    <div
      class="col"
      v-for="(card, index) in cards"
      :key="card.id"
    >
      <button
        class="memory-card w-100"
        type="button"
        :data-state="card.state"
        @click="$emit('flip', index)"
      >
        <div class="card-outer shadow-sm">
          <div class="card-inner">
            <div class="card-face card-back d-flex align-items-center justify-content-center">
              <span class="fs-3 fw-bold">?</span>
            </div>
            <div class="card-face card-front d-flex align-items-center justify-content-center">
              <span class="symbol fs-3 fw-bold">{{ card.symbol }}</span>
            </div>
          </div>
        </div>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MemoryBoard',
  props: {
    cards: {
      type: Array,
      default: () => []
    }
  },
  emits: ['flip']
}
</script>

<style scoped>
/* Hier könntest du später noch Spezial-Styles übernehmen */
.card-outer {
  border-radius: 0.5rem;
  overflow: hidden;
}

.memory-card[disabled] {
  cursor: default;
}
</style>
