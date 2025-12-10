// public/javascripts/memory-webcomponent.js

(() => {
  // Wir brauchen die globalen Objekte von Vue & Vuetify (CDN)
  if (!window.Vue || !window.Vuetify) {
    console.warn("Vue/Vuetify nicht gefunden – memory-vuetify-badge wird nicht initialisiert.");
    return;
  }

  const { createApp } = Vue;
  const { createVuetify } = Vuetify;

  class MemoryVuetifyBadge extends HTMLElement {
    connectedCallback() {
      // Mehrfach-Mount verhindern
      if (this._app) return;

      const mountPoint = document.createElement('div');
      this.appendChild(mountPoint);

      const vuetify = createVuetify();

      // Innere Vue-Komponente, die Vuetify-Komponenten verwendet
      const BadgeComponent = {
        name: 'MemoryVuetifyBadgeInner',
        props: {
          label: {
            type: String,
            default: 'Vue + Vuetify Web Component'
          }
        },
        template: `
          <v-card
            class="ma-4 pa-3 d-inline-flex align-center"
            elevation="4"
            style="
              background-color: rgba(0,0,0,0.45);
              color: #f8f9fa;
              border-radius: 16px;
            "
          >
            <div class="d-flex flex-column">
              <span class="fw-bold mb-1">{{ label }}</span>

              <!-- HIER: fertige Vuetify-Komponente aus der Collection -->
              <v-chip
                size="small"
                class="mt-1"
                color="green-accent-3"
                text-color="#111111"
                variant="elevated"
              >
                Vuetify v-chip Component
              </v-chip>
            </div>
          </v-card>
        `
      };

      const outerLabel = this.getAttribute('label') || 'Vue + Vuetify Web Component aktiv';

      const app = createApp({
        components: { BadgeComponent },
        data() {
          return {
            label: outerLabel
          };
        },
        template: `<badge-component :label="label" />`
      });

      app.use(vuetify);
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

  // Custom Web Component beim Browser registrieren
  customElements.define('memory-vuetify-badge', MemoryVuetifyBadge);
})();
