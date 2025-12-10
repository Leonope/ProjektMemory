// public/javascripts/vue-memory.js

(() => {
  const { createApp, ref, computed } = Vue;
  const { createVuetify } = Vuetify;

  // --- Hilfsfunktion: Deck bauen --------------------------------
  function buildDeck(pairs) {
    const base = Array.from({ length: pairs }, (_, i) => String(i + 1));
    const doppelt = base.concat(base);
    for (let i = doppelt.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [doppelt[i], doppelt[j]] = [doppelt[j], doppelt[i]];
    }
    return doppelt;
  }

  // --- Component: Setup-Formular --------------------------------
  const GameSetup = {
    name: 'GameSetup',
    props: {
      playerName: String,
      playerCount: Number,
      pairs: Number,
    },
    emits: [
      'update:playerName',
      'update:playerCount',
      'update:pairs',
      'start',
    ],
    template: /*html*/`
      <div class="game-setup-container">
        <h1 class="mb-4 text-center">Memory – Vue SPA</h1>

        <div class="alert alert-info text-center mb-3">
          Wähle Name, Spieleranzahl und Anzahl Paare für die Vue-Version.
        </div>

        <form @submit.prevent="$emit('start')" class="row g-3 mb-4 justify-content-center">

          <div class="col-12 col-md-4">
            <label for="playerName" class="form-label">Name</label>
            <input id="playerName"
                   type="text"
                   class="form-control"
                   :value="playerName"
                   @input="$emit('update:playerName', $event.target.value)"
                   placeholder="Dein Name">
          </div>

          <div class="col-12 col-md-4">
            <label for="pairs" class="form-label">Anzahl Paare</label>
            <input id="pairs"
                   type="number"
                   min="2" max="18" step="2"
                   class="form-control"
                   :value="pairs"
                   @input="$emit('update:pairs', parseInt($event.target.value || '2',10))">
          </div>

          <div class="col-12 col-md-4">
            <label for="playerCount" class="form-label">Anzahl Spieler</label>
            <select id="playerCount"
                    class="form-select"
                    :value="playerCount"
                    @change="$emit('update:playerCount', parseInt($event.target.value || '1',10))">
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
    `
  };

  // --- Component: Statusleiste ----------------------------------
  const GameStatusBar = {
    name: 'GameStatusBar',
    props: {
      playerName: String,
      playerCount: Number,
      pairs: Number,
      moves: Number,
      found: Number,
    },
    template: /*html*/`
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
    `
  };

  // --- Component: Memory-Board ----------------------------------
  const MemoryBoard = {
    name: 'MemoryBoard',
    props: {
      cards: Array,   // [{ id, symbol, state }]
    },
    emits: ['flip'],
    template: /*html*/`
      <div id="board"
           class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-6 g-2 g-md-3 justify-content-center">
        <div class="col" v-for="(card, index) in cards" :key="card.id">
          <button class="memory-card w-100"
                  type="button"
                  :data-state="card.state"
                  @click="$emit('flip', index)">
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
    `
  };

  // --- Root-Komponente: MemoryApp -------------------------------
  const MemoryApp = {
    name: 'MemoryApp',
    components: {
      GameSetup,
      GameStatusBar,
      MemoryBoard
    },
    setup() {
      const showSetup   = ref(true);
      const playerName  = ref('');
      const playerCount = ref(1);
      const pairs       = ref(4);

      const moves       = ref(0);
      const found       = ref(0);

      const cards       = ref([]);
      const firstPick   = ref(null);
      const secondPick  = ref(null);
      const lock        = ref(false);

      const message     = ref('Willkommen bei der Vue-Version von Memory!');

      function initGame() {
        const deck = buildDeck(pairs.value);
        cards.value = deck.map((sym, idx) => ({
          id: idx,
          symbol: sym,
          state: 'facedown'
        }));
        moves.value = 0;
        found.value = 0;
        firstPick.value = null;
        secondPick.value = null;
        lock.value = false;
      }

      function notifyBackendStart() {
        const name =
          playerName.value && playerName.value.trim().length > 0
            ? playerName.value.trim()
            : "Player";

        fetch("/game/vue/start", {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            playerName:  name,
            playerCount: playerCount.value,
            pairs:       pairs.value
          })
        })
          .then(res => res.json().catch(() => null))
          .catch(err => console.error("Fehler bei /game/vue/start:", err));
      }

      function startGame() {
        initGame();
        showSetup.value = false;
        message.value = `Vue-Spiel für ${playerName.value || 'Player'} mit ${pairs.value} Paar(en) gestartet.`;
        notifyBackendStart();
      }

      function flipCard(index) {
        if (lock.value) return;
        const c = cards.value[index];
        if (!c || c.state !== 'facedown') return;

        c.state = 'faceup';

        if (!firstPick.value) {
          firstPick.value = { index, symbol: c.symbol };
          return;
        }

        secondPick.value = { index, symbol: c.symbol };
        lock.value = true;
        moves.value++;

        if (firstPick.value.symbol === secondPick.value.symbol) {
          setTimeout(() => {
            cards.value[firstPick.value.index].state  = 'matched';
            cards.value[secondPick.value.index].state = 'matched';
            found.value++;

            firstPick.value = null;
            secondPick.value = null;
            lock.value = false;
          }, 250);
        } else {
          setTimeout(() => {
            cards.value[firstPick.value.index].state  = 'facedown';
            cards.value[secondPick.value.index].state = 'facedown';

            firstPick.value = null;
            secondPick.value = null;
            lock.value = false;
          }, 600);
        }
      }

      const allMatched = computed(() => found.value === pairs.value);

      return {
        showSetup,
        playerName,
        playerCount,
        pairs,
        moves,
        found,
        cards,
        message,
        allMatched,
        startGame,
        flipCard
      };
    },
    template: /*html*/`
      <div class="memory-app-container">

        <!-- Setup -->
        <game-setup
          v-if="showSetup"
          :player-name="playerName"
          :player-count="playerCount"
          :pairs="pairs"
          @update:playerName="playerName = $event"
          @update:playerCount="playerCount = $event"
          @update:pairs="pairs = $event"
          @start="startGame"
        ></game-setup>

        <!-- Spiel -->
        <div v-else class="game-container">
          <h1 class="mb-3 text-center">Memory – Vue SPA</h1>

          <div v-if="message" class="alert alert-info text-center mb-3">
            {{ message }}
          </div>

          <game-status-bar
            :player-name="playerName"
            :player-count="playerCount"
            :pairs="pairs"
            :moves="moves"
            :found="found"
          ></game-status-bar>

          <!-- Vaadin Web Component: Accordion -->
                    <!-- Vaadin Web Component: Accordion – klein & unten rechts -->
          <div class="tech-accordion-container">
            <vaadin-accordion class="tech-accordion">
              <vaadin-accordion-panel theme="filled">
                <div slot="summary">Frameworks</div>
                <<div class="p-2 accordion-content">
  <ul class="mb-0">
    <li>Vue 3 SPA mit Vuetify</li>
    <li>Bootstrap-Version mit WebSockets</li>
    <li>React & Angular Varianten</li>
  </ul>
</div>

              </vaadin-accordion-panel>

              <vaadin-accordion-panel theme="filled">
                <div slot="summary">Echtzeit &amp; Kommunikation</div>
                <div class="p-2">
                  <ul class="mb-0">
                    <li>WebSockets für Spiel-Synchronisation</li>
                    <li>Comet-Chat zwischen Spielern</li>
                    <li>Server-Sent Events (SSE) für Session-Counter</li>
                  </ul>
                </div>
              </vaadin-accordion-panel>

              <vaadin-accordion-panel theme="filled">
                <div slot="summary">Speicher &amp; Formate</div>
                <div class="p-2">
                  <ul class="mb-0">
                    <li>JSON für Highscores &amp; Presets</li>
                    <li>XML für das Karten-Deck</li>
                  </ul>
                </div>
              </vaadin-accordion-panel>
            </vaadin-accordion>
          </div>

          <memory-board
            :cards="cards"
            @flip="flipCard"
          ></memory-board>

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
    `
  };

  // --- Vue + Vuetify mounten -----------------------------------
  const vuetify = createVuetify();

  createApp({
    components: { MemoryApp },
    template: `<v-app><memory-app /></v-app>`
  })
    .use(vuetify)
    .mount('#vue-app');

})();
