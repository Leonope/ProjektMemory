<template>
  <div class="board">
    <div
      class="card-wrapper"
      v-for="(card, index) in cards"
      :key="card.id"
    >
      <button
        class="memory-card"
        type="button"
        :data-state="card.state"
        @click="$emit('flip', index)"
      >
        <div class="card-inner">
          <!-- Rückseite -->
          <div class="card-face card-back">
            <span class="symbol">?</span>
          </div>

          <!-- Vorderseite -->
          <div class="card-face card-front">
            <span class="symbol">{{ card.symbol }}</span>
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
/* Einfaches Grid-Layout, unabhängig von Bootstrap */
.board {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 0.75rem;
}

/* Wrapper, damit das Grid ordentlich skaliert */
.card-wrapper {
  width: 100%;
}

/* Button als Karte */
.memory-card {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 4; /* sorgt für Kartenform */
  border: none;
  padding: 0;
  background: transparent;
  cursor: pointer;
  perspective: 1000px; /* für 3D-Effekt */
}

/* Kein Cursor für gematchte Karten */
.memory-card[data-state='matched'] {
  cursor: default;
}

/* Innenleben mit Flip-Effekt */
.card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  transition: transform 0.4s ease;
  border-radius: 0.6rem;
  box-shadow: 0 0.35rem 0.8rem rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

/* Flip bei aufgedeckter/gematchter Karte */
.memory-card[data-state='faceup'] .card-inner,
.memory-card[data-state='matched'] .card-inner {
  transform: rotateY(180deg);
}

/* Vorder- und Rückseite */
.card-face {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  backface-visibility: hidden;
  border-radius: 0.6rem;
}

/* Rückseite – Fragezeichen */
.card-back {
  background: #2c3e50;
  color: #ecf0f1;
}

/* Vorderseite – Symbol */
.card-front {
  background: #ffffff;
  border: 2px solid #2c3e50;
  transform: rotateY(180deg);
  color: #2c3e50;
}

/* Gefundene Karten grün einfärben */
.memory-card[data-state='matched'] .card-front {
  background: #d4edda;
  border-color: #28a745;
}

/* Kleiner Pop-Effekt bei Match */
.memory-card[data-state='matched'] .card-inner {
  animation: matched-pop 0.25s ease-out;
}

.symbol {
  font-size: 1.8rem;
  font-weight: 700;
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
