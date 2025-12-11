<template>
  <div class="board">
    <div
      class="card-wrapper"
      v-for="(card, index) in cards"
      :key="card.id"
    >
      <button
        type="button"
        :class="['memory-card', card.state]"
        @click="$emit('flip', index)"
      >
        <div class="card-inner">
          <span class="symbol">
            {{ card.state === 'facedown' ? '?' : card.symbol }}
            <!-- Falls du lieber die ID sehen willst:
                 {{ card.state === 'facedown' ? '?' : card.id }} -->
          </span>
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
/* Grid-Layout */
.board {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 0.75rem;
}

.card-wrapper {
  width: 100%;
}

/* Button als Karte */
.memory-card {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 4;
  border: none;
  padding: 0;
  background: transparent;
  cursor: pointer;
  perspective: 1000px;
}

/* Kein Cursor für gematchte Karten */
.memory-card.matched {
  cursor: default;
}

/* Innenleben mit Flip-Effekt */
.card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 0.6rem;
  box-shadow: 0 0.35rem 0.8rem rgba(0, 0, 0, 0.15);
  overflow: hidden;

  background: #2c3e50;
  color: #ecf0f1;
  display: flex;
  align-items: center;
  justify-content: center;

  transform-style: preserve-3d;
  transition:
    transform 0.35s ease,
    background 0.2s,
    color 0.2s;
}

/* Flip-Animation abhängig vom state (per Klasse) */
.memory-card.faceup .card-inner,
.memory-card.matched .card-inner {
  transform: rotateY(180deg);
  background: #ffffff;
  color: #2c3e50;
}

/* Gefundene Karten grün einfärben */
.memory-card.matched .card-inner {
  background: #d4edda;
}

/* Mini-Pop bei Match */
.memory-card.matched .card-inner {
  animation: matched-pop 0.25s ease-out;
}

.symbol {
  font-size: 2rem;
  font-weight: 800;
  transform: rotateY(180deg); /* wichtig! */
}


@keyframes matched-pop {
  0% {
    transform: rotateY(180deg) scale(1);
  }
  50% {
    transform: rotateY(180deg) scale(1.07);
  }
  100% {
    transform: rotateY(180deg) scale(1);
  }
}
</style>
