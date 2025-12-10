// public/javascripts/quasar-webcomponent.js

(() => {
  if (!window.Vue || !window.Quasar) {
    console.warn("Vue/Quasar nicht verfügbar – memory-quasar-panel wird nicht initialisiert.");
    return;
  }

  const { createApp, ref, onMounted, onBeforeUnmount } = Vue;
  const { Quasar } = window;

  class MemoryQuasarPanel extends HTMLElement {
    connectedCallback() {
      if (this._app) return;

      const mountPoint = document.createElement('div');
      this.appendChild(mountPoint);

      const Panel = {
        name: 'MemoryQuasarPanelInner',
        setup() {
          const energy = ref(0.35);
          const combo  = ref(0);
          let timerId = null;

          onMounted(() => {
            timerId = setInterval(() => {
              // kleiner zufälliger „Walk“ für das Energie-Level
              let next = energy.value + (Math.random() - 0.5) * 0.2;
              if (next < 0) next = 0;
              if (next > 1) next = 1;
              energy.value = next;

              // Combo steigt nur bei hohem Level
              if (next > 0.8) {
                combo.value += 1;
              } else if (combo.value > 0) {
                combo.value -= 1;
              }
            }, 800);
          });

          onBeforeUnmount(() => {
            if (timerId) {
              clearInterval(timerId);
            }
          });

          return { energy, combo };
        },
        template: `
          <q-card flat bordered class="bg-grey-10 text-white">
            <q-card-section class="row items-center no-wrap">
              <div class="col-auto">
                <q-avatar size="42px" icon="memory" color="deep-purple-5" text-color="white" />
              </div>
              <div class="col">
                <div class="text-subtitle2">Quasar Mission Control</div>
                <div class="text-caption">
                  Energie-Level deiner aktuellen Session
                </div>
              </div>
            </q-card-section>

            <q-card-section>
              <div class="row items-center q-col-gutter-sm">
                <div class="col-9">
                  <q-linear-progress
                    :value="energy"
                    color="deep-purple-4"
                    track-color="grey-8"
                    animation-speed="600"
                    rounded
                    striped
                  />
                </div>
                <div class="col-3 text-right">
                  <div class="text-caption">Energy</div>
                  <div class="text-body2">{{ Math.round(energy * 100) }}%</div>
                </div>
              </div>

              <div class="q-mt-sm text-caption">
                Combo-Streak: <strong>{{ combo }}</strong>
              </div>
            </q-card-section>
          </q-card>
        `
      };

      const app = createApp(Panel);
      app.use(Quasar);
      this._app = app;
      app.mount(mountPoint);
    }

    disconnectedCallback() {
      if (this._app) {
        this._app.unmount();
        this._app = null;
      }
      this.innerHTML = '';
    }
  }

  customElements.define('memory-quasar-panel', MemoryQuasarPanel);
})();
