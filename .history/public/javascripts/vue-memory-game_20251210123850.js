// ===========================================
// Memory Game as a Vue Component + Vuetify
// ===========================================

const MemoryGame = {
  name: "MemoryGame",

  data() {
    return {
      playerName: "",
      playerCount: 1,
      pairs: 4,
      board: [],
      first: null,
      second: null,
      lock: false,
      found: 0,
      moves: 0,
      started: false,
    };
  },

  mounted() {
    console.log("Vue MemoryGame mounted.");
  },

  methods: {
    // ============================
    // Backend Start
    // ============================
    async startGame() {
      const payload = {
        playerName: this.playerName,
        playerCount: this.playerCount,
        pairs: this.pairs
      };

      // -> Play Controller (GameStarting + askPlayerName ...)
      const res = await fetch("/game/vue/start", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        alert("Backend konnte Spiel nicht starten.");
        return;
      }

      const json = await res.json();
      console.log("Backend Start:", json);

      // Deck übernehmen
      this.board = json.deck.map(id => ({
        id,
        state: "down",
      }));

      this.started = true;
      this.found = 0;
      this.moves = 0;
    },

    // ============================
    // UI Flip Logic (local only)
    // ============================
    flipCard(card) {
      if (this.lock) return;
      if (card.state !== "down") return;

      card.state = "up";

      if (!this.first) {
        this.first = card;
        return;
      }

      this.second = card;
      this.lock = true;
      this.moves++;

      if (this.first.id === this.second.id) {
        this.first.state = "matched";
        this.second.state = "matched";
        this.found++;
        this.resetTurn();
      } else {
        setTimeout(() => {
          this.first.state = "down";
          this.second.state = "down";
          this.resetTurn();
        }, 550);
      }
    },

    resetTurn() {
      this.first = null;
      this.second = null;
      this.lock = false;
    }
  },

  // ============================
  // TEMPLATE
  // ============================
  template: `
  <v-container class="pa-4" style="max-width: 900px;">
    <h2 class="text-center mb-4">Vue + Vuetify Memory Game</h2>

    <!-- Setup -->
    <div v-if="!started">
      <v-text-field label="Name" v-model="playerName"></v-text-field>

      <v-text-field 
        type="number"
        label="Player Count"
        v-model.number="playerCount"
        min="1"
        max="4">
      </v-text-field>

      <v-text-field 
        type="number"
        label="Pairs"
        v-model.number="pairs"
        min="2"
        max="12">
      </v-text-field>

      <v-btn 
        color="primary"
        class="mt-3"
        @click="startGame">
        Start Game
      </v-btn>
    </div>

    <!-- Game Board -->
    <div v-else>
      <div class="d-flex justify-space-between mb-3">
        <div><strong>Player:</strong> {{ playerName }}</div>
        <div><strong>Moves:</strong> {{ moves }}</div>
        <div><strong>Found:</strong> {{ found }} / {{ pairs }}</div>
      </div>

      <div class="grid"
        style="display:grid; grid-template-columns:repeat(4,1fr); gap:10px;">
        <v-card 
          v-for="(card,i) in board"
          :key="i"
          class="pa-6 text-center"
          :color="card.state==='matched' ? 'green lighten-3' : '#333'"
          style="cursor:pointer; height:120px;"
          @click="flipCard(card)">
          
          <span v-if="card.state==='up' || card.state==='matched'"
                style="font-size:2rem; font-weight:bold;">
            {{ card.id }}
          </span>

          <span v-else style="font-size:2rem; opacity:0.2;">?</span>
        </v-card>
      </div>
    </div>
  </v-container>
  `
};

