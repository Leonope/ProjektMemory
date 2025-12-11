// public/javascripts/memory-webcomponent.js

(() => {
  /**
   * Kleiner Helfer: Text aus Attribut oder Default.
   */
  function getAttrOr(el, name, fallback) {
    const v = el.getAttribute(name);
    return (v && v.trim().length > 0) ? v.trim() : fallback;
  }

  /**
   * Einfache "Pill"-Web-Component, die nur hübsch aussieht.
   *
   * <memory-pill kind="hint">Klick auf die Energie!</memory-pill>
   */
  class MemoryPill extends HTMLElement {
    connectedCallback() {
      const kind = this.getAttribute('kind') || 'default';
      const text = this.innerHTML || '';

      this.innerHTML = `
        <span class="memory-pill memory-pill--${kind}">
          ${text}
        </span>
      `;
    }
  }

  customElements.define('memory-pill', MemoryPill);

  /**
   * Haupt-Web-Component:
   * <memory-vuetify-badge
   *    label="Vue + Vuetify Web Component aktiv"
   *    framework="Vue"
   *    games-played="3">
   * </memory-vuetify-badge>
   *
   * Nutzt Vuetify-ähnliche Chip-Styles (v-chip-Klassen),
   * interaktive Energie-Anzeige & Modus-Umschalter.
   */
  class MemoryVuetifyBadge extends HTMLElement {

    constructor() {
      super();
      this.energyLevels = [100, 75, 50, 25];
      this.currentEnergyIndex = 0;
      this.modes = ['Demo', 'Training', 'Match'];
      this.currentModeIndex = 0;
    }

    connectedCallback() {
      this.render();
      this.bindEvents();
    }

    static get observedAttributes() {
      return ['label', 'framework', 'games-played'];
    }

    attributeChangedCallback() {
      // Bei Attributänderungen neu rendern.
      this.render();
      this.bindEvents();
    }

    render() {
      const label      = getAttrOr(this, 'label', 'Memory – Vue/Vuetify Badge');
      const framework  = getAttrOr(this, 'framework', 'Vue');
      const gamesPlayed = getAttrOr(this, 'games-played', '0');

      const energy = this.energyLevels[this.currentEnergyIndex];
      const mode   = this.modes[this.currentModeIndex];

      this.innerHTML = `
        <div class="memory-badge-wrapper">
          <!-- Hauptchip -->
          <div class="v-chip v-chip--label v-chip--clickable memory-badge-main">
            <span class="memory-badge-dot"></span>
            <span class="memory-badge-label">${label}</span>
          </div>

          <!-- Kleine Status-Chips -->
          <div class="memory-badge-row">

            <div class="v-chip v-chip--label v-chip--clickable memory-badge-chip" data-role="energy">
              ⚡ Energie: <span class="memory-badge-energy">${energy}%</span>
            </div>

            <div class="v-chip v-chip--label v-chip--clickable memory-badge-chip" data-role="mode">
              🎮 Modus: <span class="memory-badge-mode">${mode}</span>
            </div>

          </div>

          <div class="memory-badge-row">

            <div class="v-chip v-chip--label memory-badge-chip memory-badge-info">
              🧩 Framework: ${framework} &nbsp;|&nbsp; Spiele: ${gamesPlayed}
            </div>

          </div>

          <!-- Kleiner Hinweis als zweite Web-Component -->
          <div class="memory-badge-row memory-badge-hint-row">
            <memory-pill kind="hint">
              Tipp: Klicke auf ⚡ oder 🎮, um Status zu ändern.
            </memory-pill>
          </div>

          <div class="memory-badge-row memory-badge-actions">
            <button type="button" class="memory-badge-btn" data-role="reset">
              Reset
            </button>
          </div>
        </div>
      `;
    }

    bindEvents() {
      const energyChip = this.querySelector('[data-role="energy"]');
      const modeChip   = this.querySelector('[data-role="mode"]');
      const resetBtn   = this.querySelector('[data-role="reset"]');

      if (energyChip) {
        energyChip.onclick = () => {
          this.currentEnergyIndex = (this.currentEnergyIndex + 1) % this.energyLevels.length;
          this.updateEnergyDisplay();
        };
      }

      if (modeChip) {
        modeChip.onclick = () => {
          this.currentModeIndex = (this.currentModeIndex + 1) % this.modes.length;
          this.updateModeDisplay();
        };
      }

      if (resetBtn) {
        resetBtn.onclick = () => {
          this.currentEnergyIndex = 0;
          this.currentModeIndex   = 0;
          this.updateEnergyDisplay();
          this.updateModeDisplay();
        };
      }
    }

    updateEnergyDisplay() {
      const val = this.energyLevels[this.currentEnergyIndex];
      const span = this.querySelector('.memory-badge-energy');
      if (span) {
        span.textContent = `${val}%`;
      }
    }

    updateModeDisplay() {
      const val = this.modes[this.currentModeIndex];
      const span = this.querySelector('.memory-badge-mode');
      if (span) {
        span.textContent = val;
      }
    }
  }

  customElements.define('memory-vuetify-badge', MemoryVuetifyBadge);
})();
