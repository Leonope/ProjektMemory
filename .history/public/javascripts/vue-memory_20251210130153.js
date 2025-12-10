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
      const message     = ref('Willkommen bei der Vue+Vuetify-Version von Memory!');

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

      // Nur Backend informieren – Deck bleibt clientseitig
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
          `Vue+Vuetify-Spiel für ${name} mit ${pairs.value} Paar(en) gestartet.`;

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

    template: /*html*/`
      <v-app>
        <v-main>
          <v-container class="py-6" fluid="sm">
            <v-row justify="center">
              <v-col cols="12" md="10" lg="8">
                <v-card elevation="6" class="pa-4" color="blue-grey-darken-4">
                  <v-card-title class="text-h4 text-center text-white">
                    Memory – Vue + Vuetify
                  </v-card-title>
                  <v-card-subtitle class="text-center text-grey-lighten-1 mb-4">
                    Gesamtes Spiel als eine Vue-Komponente mit Vuetify UI
                  </v-card-subtitle>

                  <!-- Setup -->
                  <div v-if="showSetup">
                    <v-alert type="info" variant="tonal" class="mb-4">
                      Wähle Name, Spieleranzahl und Anzahl Paare für dein Spiel.
                    </v-alert>

                    <v-row class="mb-2" dense>
                      <v-col cols="12" md="4">
                        <v-text-field
                          v-model="playerName"
                          label="Name"
                          variant="outlined"
                          color="teal-lighten-2"
                          hide-details="auto"
                          placeholder="Dein Name"
                        />
                      </v-col>

                      <v-col cols="12" md="4">
                        <v-text-field
                          v-model.number="pairs"
                          type="number"
                          min="2"
                          max="18"
                          step="2"
                          label="Anzahl Paare"
                          variant="outlined"
                          color="teal-lighten-2"
                          hide-details="auto"
                        />
                      </v-col>

                      <v-col cols="12" md="4">
                        <v-select
                          v-model.number="playerCount"
                          :items="[1,2,3,4]"
                          label="Anzahl Spieler"
                          variant="outlined"
                          color="teal-lighten-2"
                          hide-details="auto"
                        />
                      </v-col>
                    </v-row>

                    <v-row justify="center" class="mt-4">
                      <v-btn
                        color="teal-accent-4"
                        size="large"
                        @click="startGame"
                      >
                        Vue+Vuetify-Spiel starten
                      </v-btn>
                    </v-row>
                  </div>

                  <!-- Spiel -->
                  <div v-else>
                    <v-alert
                      v-if="message"
                      type="info"
                      variant="tonal"
                      class="mb-4"
                    >
                      {{ message }}
                    </v-alert>

                    <v-card
                      class="mb-4"
                      color="blue-grey-darken-3"
                      variant="flat"
                    >
                      <v-card-text class="d-flex flex-wrap align-center gap-4">
                        <div>
                          <strong>Spieler:</strong>
                          <span v-if="displayName">{{ displayName }}</span>
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
                      </v-card-text>
                    </v-card>

                    <v-row class="justify-center" dense>
                      <v-col
                        v-for="(card, index) in cards"
                        :key="card.id"
                        cols="6"
                        sm="4"
                        md="3"
                        lg="2"
                        class="mb-3"
                      >
                        <button
                          type="button"
                          class="memory-card w-100"
                          :data-state="card.state"
                          @click="flipCard(index)"
                        >
                          <div class="card-outer shadow-sm">
                            <div class="card-inner">
                              <div class="card-face card-back d-flex align-items-center justify-content-center">
                                <span class="fs-3 fw-bold">?</span>
                              </div>
                              <div class="card-face card-front d-flex align-items-center justify-content-center">
                                <span class="symbol fs-3 fw-bold">
                                  {{ card.symbol }}
                                </span>
                              </div>
                            </div>
                          </div>
                        </button>
                      </v-col>
                    </v-row>

                    <v-row class="mt-4" justify="center" align="center">
                      <v-col cols="12" class="text-center">
                        <v-btn
                          variant="outlined"
                          color="teal-lighten-3"
                          class="me-2"
                          @click="showSetup = true"
                        >
                          Neues Vue+Vuetify-Spiel konfigurieren
                        </v-btn>

                        <span v-if="allMatched" class="ms-2 text-success fw-bold">
                          🎉 Alle Paare gefunden!
                        </span>
                      </v-col>
                    </v-row>
                  </div>
                </v-card>
              </v-col>
            </v-row>
          </v-container>
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
