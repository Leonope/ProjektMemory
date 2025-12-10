// public/javascripts/vue-memory.js
(function () {
  const { createApp, ref, computed } = Vue;
  const { createVuetify } = Vuetify;

  // Hilfsfunktion: Deck erzeugen (rein clientseitig)
  function buildDeck(pairs) {
    const base = Array.from({ length: pairs }, (_, i) => String(i + 1));
    const doubled = base.concat(base);
    for (let i = doubled.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [doubled[i], doubled[j]] = [doubled[j], doubled[i]];
    }
    return doubled;
  }

  const MemoryGame = {
    name: 'MemoryGame',
    setup() {
      const showSetup   = ref(true);
      const playerName  = ref('');
      const playerCount = ref(1);
      const pairs       = ref(4);

      const moves       = ref(0);
      const found       = ref(0);
      const cards       = ref([]);

      const firstPick   = ref(null); // { index, symbol }
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
        moves.value      = 0;
        found.value      = 0;
        firstPick.value  = null;
        secondPick.value = null;
        lock.value       = false;
      }

      // Backend nur informieren – Deck bleibt clientseitig
      function notifyBackendStart(name, pCount, pPairs) {
        fetch('/game/vue/start', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            playerName:  name,
            playerCount: pCount,
            pairs:       pPairs
          })
        })
          .then(res => {
            if (!res.ok) {
              console.error('Backend /game/vue/start fehlgeschlagen:', res.status);
            }
            return res.json().catch(() => null);
          })
          .then(data => {
            if (!data || data.status !== 'ok') {
              console.warn('Backend /game/vue/start Antwort:', data);
            }
          })
          .catch(err => {
            console.error('Fehler bei Fetch /game/vue/start:', err);
          });
      }

      function startGame() {
        const name =
          playerName.value && playerName.value.trim().length > 0
            ? playerName.value.trim()
            : 'Player';

        // 1) Lokales Spiel aufsetzen (Deck etc.)
        initGame();
        showSetup.value = false;

        message.value =
          `Vue-Spiel für ${name} mit ${pairs.value} Paar(en) gestartet.`;

        // 2) Backend benachrichtigen (askPlayer..., GameStarting)
        notifyBackendStart(name, playerCount.value, pairs.value);
      }

      function flipCard(index) {
        if (lock.value) return;
        const card = cards.value[index];
        if (!card || card.state !== 'facedown') return;

        const newCards = cards.value.slice();
        newCards[index] = { ...newCards[index], state: 'faceup' };
        cards.value = newCards;

        if (!firstPick.value) {
          firstPick.value = { index, symbol: card.symbol };
          return;
        }

        secondPick.value = { index, symbol: card.symbol };
        lock.value = true;
        moves.value += 1;

        if (firstPick.value.symbol === secondPick.value.symbol) {
          // Match
          setTimeout(() => {
            const c = cards.value.slice();
            c[firstPick.value.index]  = { ...c[firstPick.value.index],  state: 'matched' };
            c[secondPick.value.index] = { ...c[secondPick.value.index], state: 'matched' };
            cards.value = c;

            found.value += 1;
            firstPick.value  = null;
            secondPick.value = null;
            lock.value       = false;
          }, 250);
        } else {
          // Kein Match
          setTimeout(() => {
            const c = cards.value.slice();
            c[firstPick.value.index]  = { ...c[firstPick.value.index],  state: 'facedown' };
            c[secondPick.value.index] = { ...c[secondPick.value.index], state: 'facedown' };
            cards.value = c;
          }, 600);

          setTimeout(() => {
            firstPick.value  = null;
            secondPick.value = null;
            lock.value       = false;
          }, 650);
        }
      }

      const allMatched = computed(() => {
        return pairs.value > 0 && found.value === pairs.value;
      });

      const displayName = computed(() => {
        if (playerName.value && playerName.value.trim().length > 0) {
          return playerName.value.trim();
        }
        return null;
      });

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
        displayName,
        startGame,
        flipCard
      };
    },

    // Template nutzt jetzt wieder dein ursprüngliches HTML + CSS
    template: /*html*/`
      <v-app>
        <v-main>
          <main class="container py-4">

            <!-- Setup-Ansicht wie bei Bootstrap-Version -->
            <div v-if="showSetup" class="game-setup-container">
              <h1 class="mb-4 text-center">Memory (Vue + Vuetify, altes Layout)</h1>

              <div class="alert alert-info text-center mb-3">
                Wähle Name, Spieleranzahl und Anzahl Paare.
              </div>

              <form class="row g-3 mb-4 justify-content-center" @submit.prevent="startGame">
                <div class="col-12 col-md-4">
                  <label for="vue-playerName" class="form-label">Name</label>
                  <input id="vue-playerName"
                         type="text"
                         class="form-control"
                         placeholder="Dein Name"
                         v-model="playerName">
                </div>

                <div class="col-12 col-md-4">
                  <label for="vue-pairs" class="form-label">Anzahl Paare</label>
                  <input id="vue-pairs"
                         type="number"
                         min="2" max="18" step="2"
                         class="form-control"
                         v-model.number="pairs">
                </div>

                <div class="col-12 col-md-4">
                  <label for="vue-playerCount" class="form-label">Anzahl Spieler</label>
                  <select id="vue-playerCount"
                          class="form-select"
                          v-model.number="playerCount">
                    <option :value="1">1</option>
                    <option :value="2">2</option>
                    <option :value="3">3</option>
                    <option :value="4">4</option>
                  </select>
                </div>

                <div class="col-12 text-center">
                  <button type="submit" class="btn btn-primary px-4">
                    Spiel starten
                  </button>
                </div>
              </form>
            </div>

            <!-- Spiel-Ansicht -->
            <div v-else class="game-container">
              <h1 class="mb-3 text-center">Memory (Vue-Version)</h1>

              <div v-if="message" class="alert alert-info text-center mb-3">
                {{ message }}
              </div>

              <!-- Statusleiste wie bisher -->
              <div class="row justify-content-center mb-3">
                <div class="col-12 col-md-10">
                  <div class="card bg-secondary-subtle border-0 shadow-sm">
                    <div class="card-body text-dark d-flex flex-wrap gap-3 align-items-center">
                      <div>
                        <strong>Spieler:</strong>
                        <span v-if="displayName">{{ displayName }}</span>
                        <em v-else>–</em>
                      </div>
                      <div><strong>Spieleranzahl:</strong> {{ playerCount }}</div>
                      <div><strong>Paare:</strong> {{ pairs }}</div>
                      <div class="ms-auto"><strong>Versuche:</strong> {{ moves }}</div>
                      <div><strong>Gefundene Paare:</strong> {{ found }} / {{ pairs }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Board: identisches Markup wie Bootstrap-/memory.css-Version -->
              <div id="board"
                   class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-6 g-2 g-md-3 justify-content-center">
                <div class="col"
                     v-for="(card, index) in cards"
                     :key="card.id">
                  <button class="memory-card w-100"
                          type="button"
                          :data-state="card.state"
                          @click="flipCard(index)">
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

              <div class="text-center mt-3">
                <button
                  type="button"
                  class="btn btn-outline-light me-2"
                  @click="showSetup = true">
                  Neues Spiel konfigurieren
                </button>

                <span v-if="allMatched" class="ms-2 text-success fw-bold">
                  🎉 Alle Paare gefunden!
                </span>
              </div>
            </div>
          </main>
        </v-main>
      </v-app>
    `
  };

  // App + Vuetify erstellen und <memory-game> mounten
  document.addEventListener('DOMContentLoaded', function() {
    const root = document.getElementById('vue-root');
    if (!root) return;

    const vuetify = createVuetify();

    const app = createApp({
      components: { MemoryGame },
      template: `<memory-game />`
    });

    app.use(vuetify);
    app.mount('#vue-root');
  });
})();
