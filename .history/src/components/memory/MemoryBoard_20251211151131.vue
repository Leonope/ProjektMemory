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
            <!-- Rückseite -->
            <div class="card-face card-back d-flex align-items-center justify-content-center">
              <span class="fs-3 fw-bold">?</span>
            </div>

            <!-- Vorderseite -->
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
/* Container für die Karte */
.memory-card {
  position: relative;
  border: none;
  padding: 0;
  background: transparent;
  cursor: pointer;
  perspective: 1000px; /* wichtig für 3D-Effekt */
}

/* Kein Pointer für bereits gefundene Karten */
.memory-card[data-state='matched'] {
  cursor: default;
}

/* Äußere Hülle */
.card-outer {
  border-radius: 0.9rem;
  overflow: hidden;
}

/* Innenleben mit Flip-Animation */
.card-inner {
  position: relative;
  width: 100%;
  padding-top: 100%; /* quadratische Karte */
  transform-style: preserve-3d;
  transition: transform 0.4s ease;
}

/* Wenn Karte aufgedeckt oder gematcht ist → umdrehen */
.memory-card[data-state='faceup'] .card-inner,
.memory-card[data-state='matched'] .card-inner {
  transform: rotateY(180deg);
}

/* Vorder- und Rückseite */
.card-face {
  position: absolute;
  inset: 0;
  backface-visibility: hidden;
  border-radius: 0.9rem;
}

/* Rückseite (Fragezeichen) */
.card-back {
  background: #2c3e50;
  color: #ecf0f1;
}

/* Vorderseite (Symbol) */
.card-front {
  background: #ffffff;
  border: 2px solid #2c3e50;
  transform: rotateY(180deg);
}

/* Symbol-Styling */
.symbol {
  display: inline-block;
}

/* Gefundene Karten hervorheben */
.memory-card[data-state='matched'] .card-front {
  background: #d4edda;
  border-color: #28a745;
}

/* Kleiner „Pop“-Effekt bei gematchten Karten */
.memory-card[data-state='matched'] .card-inner {
  animation: matched-pop 0.3s ease-out;
}

@keyframes matched-pop {
  0% {
    transform: rotateY(180deg) scale(1);
  }
  50% {
    transform: rotateY(180deg) scale(1.06);
  }
  100% {
    transform: rotateY(180deg) scale(1);
  }
}
</style>
