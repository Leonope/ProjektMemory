<template>
  <div class="memory-app-container">
    <!-- Setup -->
    <GameSetup
      v-if="showSetup"
      :player-name="playerName"
      :player-count="playerCount"
      :pairs="pairs"
      @update:playerName="(val) => (playerName = val)"
      @update:playerCount="(val) => (playerCount = val)"
      @update:pairs="(val) => (pairs = val)"
      @start="startGame"
    />

    <!-- Spiel -->
    <div v-else class="game-container">
      <h1 class="mb-3 text-center">Memory – Vue SPA</h1>

      <div v-if="message" class="alert alert-info text-center mb-3">
        {{ message }}
      </div>

      <div class="game-layout">
        <section class="game-sidebar">
          <GameStatusBar
            :player-name="playerName"
            :player-count="playerCount"
            :pairs="pairs"
            :moves="moves"
            :found="found"
          />

          <div class="info-box mt-3">
            <h5 class="mb-2">Spielinfo</h5>
            <p class="mb-1">Drehe zwei Karten um. Gleiches Symbol → Paar bleibt offen.</p>
            <p class="mb-0">Ziel: Finde alle {{ pairs }} Paare mit möglichst wenigen Versuchen.</p>
          </div>

          <div v-if="!isOnline" class="offline-box mt-3">
            ⚠️ Du bist offline. Backend-Calls werden übersprungen – Spiel läuft lokal.
          </div>
        </section>

        <section class="game-main">
          <div class="board-wrapper">
            <MemoryBoard :cards="cards" @flip="flipCard" />
          </div>

          <div class="controls text-center mt-3">
            <button class="btn btn-outline-secondary me-2" @click="showSetup = true">
              Neues Spiel konfigurieren
            </button>
            <span v-if="allMatched" class="ms-2 text-success fw-bold">
              🎉 Alle Paare gefunden!
            </span>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import GameSetup from './GameSetup.vue'
import GameStatusBar from './GameStatusBar.vue'
import MemoryBoard from './MemoryBoard.vue'

export default {
  name: 'MemoryApp',
  components: {
    GameSetup,
    GameStatusBar,
    MemoryBoard
  },
  data() {
    return {
      showSetup: true,
      playerName: '',
      playerCount: 1,
      pairs: 4,

      moves: 0,
      found: 0,

      cards: [],
      firstPick: null,
      secondPick: null,
      lock: false,

      message: 'Willkommen bei der Vue-Version von Memory!',

      isOnline: navigator.onLine
    }
  },
  computed: {
    allMatched() {
      return this.found === this.pairs
    }
  },
  mounted() {
    // Online/Offline Status live updaten
    window.addEventListener('online', this.handleOnline)
    window.addEventListener('offline', this.handleOffline)
  },
  beforeUnmount() {
    window.removeEventListener('online', this.handleOnline)
    window.removeEventListener('offline', this.handleOffline)
  },
  methods: {
    handleOnline() {
      this.isOnline = true
      this.message = '✅ Wieder online.'
    },
    handleOffline() {
      this.isOnline = false
      this.message = '⚠️ Offline: Server nicht erreichbar. Spiel läuft lokal.'
    },

    buildDeck(pairs) {
      const base = Array.from({ length: pairs }, (_, i) => String(i + 1))
      const doppelt = base.concat(base)
      for (let i = doppelt.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1))
        ;[doppelt[i], doppelt[j]] = [doppelt[j], doppelt[i]]
      }
      return doppelt
    },

    initGame() {
      const deck = this.buildDeck(this.pairs)
      this.cards = deck.map((sym, idx) => ({
        id: idx,
        symbol: sym,
        state: 'facedown'
      }))
      this.moves = 0
      this.found = 0
      this.firstPick = null
      this.secondPick = null
      this.lock = false
    },

    notifyBackendStart() {
      if (!this.isOnline) {
        // offline: skip backend
        return
      }

      const name =
        this.playerName && this.playerName.trim().length > 0
          ? this.playerName.trim()
          : 'Player'

      fetch('/game/vue/start', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          playerName: name,
          playerCount: this.playerCount,
          pairs: this.pairs
        })
      })
        .then((res) => res.json().catch(() => null))
        .catch(() => {
          this.message = '⚠️ Server nicht erreichbar (Play Backend). Spiel läuft lokal.'
        })
    },

    startGame() {
      this.initGame()
      this.showSetup = false
      this.message = `Vue-Spiel für ${this.playerName || 'Player'} mit ${this.pairs} Paar(en) gestartet.`
      this.notifyBackendStart()
    },

    flipCard(index) {
      if (this.lock) return
      const c = this.cards[index]
      if (!c || c.state !== 'facedown') return

      c.state = 'faceup'

      if (!this.firstPick) {
        this.firstPick = { index, symbol: c.symbol }
        return
      }

      this.secondPick = { index, symbol: c.symbol }
      this.lock = true
      this.moves++

      if (this.firstPick.symbol === this.secondPick.symbol) {
        setTimeout(() => {
          this.cards[this.firstPick.index].state = 'matched'
          this.cards[this.secondPick.index].state = 'matched'
          this.found++

          this.firstPick = null
          this.secondPick = null
          this.lock = false
        }, 250)
      } else {
        setTimeout(() => {
          this.cards[this.firstPick.index].state = 'facedown'
          this.cards[this.secondPick.index].state = 'facedown'

          this.firstPick = null
          this.secondPick = null
          this.lock = false
        }, 600)
      }
    }
  }
}
</script>

<style scoped>
.memory-app-container {
  max-width: 1100px;
  margin: 0 auto;
}

.game-container {
  background: #f8f9fa;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 0.5rem 1.25rem rgba(0, 0, 0, 0.08);
}

.game-layout {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.game-sidebar {
  flex: 0 0 280px;
}

.game-main {
  flex: 1;
}

.board-wrapper {
  background: white;
  border-radius: 1rem;
  padding: 1rem;
  box-shadow: 0 0.5rem 1.25rem rgba(0, 0, 0, 0.04);
}

.info-box {
  background: white;
  border-radius: 0.75rem;
  padding: 0.75rem 1rem;
  box-shadow: 0 0.25rem 0.75rem rgba(0, 0, 0, 0.04);
  font-size: 0.9rem;
}

.offline-box {
  background: #fff3cd;
  border: 1px solid #ffeeba;
  color: #856404;
  border-radius: 0.75rem;
  padding: 0.75rem 1rem;
  font-size: 0.9rem;
}

@media (min-width: 900px) {
  .game-layout {
    flex-direction: row;
    align-items: flex-start;
  }
}
</style>
