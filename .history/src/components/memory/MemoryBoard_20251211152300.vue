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
          <!-- Rückseite (immer ?) -->
          <div class="card-face card-back">
            <span class="symbol-back">?</span>
          </div>

          <!-- Vorderseite (zeigt Zahl/Symbol) -->
          <div class="card-face card-front">
            <!--
              Hier wird das eigentliche Kartensymbol angezeigt.
              In deinem Deck ist das bereits eine Nummer als String ( "1", "2", ... ).
              Wenn du lieber die ID sehen willst, nimm: {{ card.id }}
            -->
            <span class="symbol-front">{{ card.symbol }}</span>
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

.memory-card[data-state='matched'] {
  cursor: default;
}

/* Flip-Innenleben */
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

/* beim Umdrehen Vorderseite anzeigen */
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

/* Rückseite mit ? */
.card-back {
  background: #2c3e50;
  color: #ecf0f1;
}

/* Vorderseite mit Zahl/Symbol */
.card-front {
  background: #ffffff;
  border: 2px solid #2c3e50;
  transform: rotateY(180deg);
  color: #2c3e50;
}

/* Styles für Symbole */
.symbol-back {
  font-size: 1.8rem;
  font-weight: 700;
}

.symbol-front {
  font-size: 2rem;
  font-weight: 800;
}

/* Gefundene Karten hervorheben */
.memory-card[data-state='matched'] .card-front {
  background: #d4edda;
  border-color: #28a745;
}

/* Pop-Effekt beim Match */
.memory-card[data-state='matched'] .card-inner {
  animation: matched-pop 0.25s ease-out;
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
