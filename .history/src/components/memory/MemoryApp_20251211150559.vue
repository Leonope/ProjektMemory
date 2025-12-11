<template>
  <div class="memory-app-container">
    <!-- Setup -->
    <GameSetup
      v-if="showSetup"
      :player-name="playerName"
      :player-count="playerCount"
      :pairs="pairs"
      @update:playerName="val => playerName = val"
      @update:playerCount="val => playerCount = val"
      @update:pairs="val => pairs = val"
      @start="startGame"
    />

    <!-- Spiel -->
    <div v-else class="game-container">
      <h1 class="mb-3 text-center">Memory – Vue SPA</h1>

      <div v-if="message" class="alert alert-info text-center mb-3">
        {{ message }}
      </div>

      <GameStatusBar
        :player-name="playerName"
        :player-count="playerCount"
        :pairs="pairs"
        :moves="moves"
        :found="found"
      />

      <MemoryBoard
        :cards="cards"
        @flip="flipCard"
      />

      <div class="text-center mt-3">
        <button class="btn me-2" @click="showSetup = true">
          Neues Vue-Spiel konfigurieren
        </button>
        <span v-if="allMatched" class="ms-2 text-success fw-bold">
          🎉 Alle Paare gefunden!
        </span>
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

      message: 'Willkommen bei der Vue-Version von Memory!'
    }
  },
  computed: {
    allMatched() {
      return this.found === this.pairs
    }
  },
  methods: {
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
      const name =
        this.playerName && this.playerName.trim().length > 0
          ? this.playerName.trim()
          : 'Player'

      fetch('/game/vue/start', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          playerName: name,
          playerCount: this.playerCount,
          pairs: this.pairs
        })
      })
        .then(res => res.json().catch(() => null))
        .catch(err => console.error('Fehler bei /game/vue/start:', err))
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
  max-width: 960px;
  margin: 0 auto;
}
</style>
