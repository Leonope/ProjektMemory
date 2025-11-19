(function () {
  // -------- DOM Refs --------
  const board = document.getElementById('board');
  if (!board) return;

  const moveEl = document.getElementById('moves');
  const foundEl = document.getElementById('found');
  const star = document.getElementById('timerStar');
  const starShape = document.getElementById('starShape');
  const timerLabel = document.getElementById('timerLabel');
  const mouth = document.getElementById('smilePath');

  // -------- Config --------
  const TIMER_DURATION_MS = 17000;
  const startColor = { r: 255, g: 213, b: 79 };   // gelb
  const endColor   = { r: 220, g: 53,  b: 69 };   // rot

  // -------- Spielstatus --------
  let totalPairs = parseInt(board.dataset.totalPairs || '8', 10);
  let deck = [];
  let firstPick = null;   // {el, symbol}
  let secondPick = null;
  let lock = false;
  let moves = 0;
  let found = 0;
  let gameOver = false;

  // -------- Timerstatus --------
  let timerStart = 0;
  let timerRAF = null;
  let lastFoundAtReset = 0;
  let timerRunning = false;

  // -------- Utils --------
  const lerp = (a, b, t) => a + (b - a) * t;
  const clamp01 = (x) => Math.max(0, Math.min(1, x));
  const toRGB = (c) => `rgb(${Math.round(c.r)}, ${Math.round(c.g)}, ${Math.round(c.b)})`;

  const shuffle = (arr) => {
    for (let i = arr.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [arr[i], arr[j]] = [arr[j], arr[i]];
    }
    return arr;
  };

  function buildDeck(pairs) {
    const base = Array.from({ length: pairs }, (_, i) => String(i + 1));
    return shuffle(base.concat(base));
  }

  // ---------- Board (NEU: generiert DOM je nach pairs) ----------
  function renderBoard(pairs) {
    const count = pairs * 2;
    let html = '';
    for (let i = 0; i < count; i++) {
      html += `
        <div class="col">
          <button class="memory-card w-100" type="button" data-state="facedown" aria-label="Karte verdeckt">
            <div class="card-outer shadow-sm">
              <div class="card-inner">
                <div class="card-face card-back d-flex align-items-center justify-content-center">
                  <span class="fs-3 fw-bold">?</span>
                </div>
                <div class="card-face card-front d-flex align-items-center justify-content-center">
                  <span class="symbol fs-3 fw-bold"></span>
                </div>
              </div>
            </div>
          </button>
        </div>`;
    }
    board.innerHTML = html;
  }

  // ---------- Reset/Init ----------
  function resetCounters() {
    moves = 0;
    found = 0;
    if (moveEl) moveEl.textContent = '0';
    if (foundEl) foundEl.textContent = '0';
  }

  function initBoard() {
    deck = buildDeck(totalPairs);
    const cards = board.querySelectorAll('.memory-card');
    cards.forEach((card, idx) => {
      const symbol = deck[idx];
      card.dataset.symbol = symbol;
      card.dataset.state = 'facedown';
      card.classList.remove('is-flipped','matched','mismatch');
      card.removeAttribute('disabled');

      const face = card.querySelector('.symbol');
      if (face) face.textContent = symbol;
      card.setAttribute('aria-label', 'Karte verdeckt');
    });
    firstPick = secondPick = null;
    lock = false;
    gameOver = false;

    // Timer frisch
    resetTimer(true);
  }

  // ---------- Timer ----------
  function startTimer() {
    timerStart = performance.now();
    lastFoundAtReset = found;
    timerRunning = true;

    updateTimerVisual(0);

    if (timerRAF) cancelAnimationFrame(timerRAF);
    timerRAF = requestAnimationFrame(tickTimer);
  }

  function resetTimer(forceFlashClear) {
    if (timerRAF) cancelAnimationFrame(timerRAF);
    if (forceFlashClear && star) star.classList.remove('timer-flash');
    startTimer();
  }

  function stopTimer() {
    timerRunning = false;
    if (timerRAF) cancelAnimationFrame(timerRAF);
    timerRAF = null;
  }

  function tickTimer(now) {
    if (!timerRunning) return;
    const elapsed = now - timerStart;
    updateTimerVisual(elapsed);

    if (elapsed >= TIMER_DURATION_MS) {
      timerRunning = false;

      // Strafe nur, wenn in diesem Fenster kein Paar gefunden wurde
      if (found === lastFoundAtReset && !gameOver) {
        moves += 1;
        if (moveEl) moveEl.textContent = String(moves);
        if (star) {
          star.classList.add('timer-flash');
          setTimeout(() => star.classList.remove('timer-flash'), 650);
        }
      }
      startTimer();
      return;
    }
    timerRAF = requestAnimationFrame(tickTimer);
  }

  function updateTimerVisual(elapsedMs) {
    const t = clamp01(elapsedMs / TIMER_DURATION_MS);
    const col = {
      r: lerp(startColor.r, endColor.r, t),
      g: lerp(startColor.g, endColor.g, t),
      b: lerp(startColor.b, endColor.b, t)
    };
    if (starShape) starShape.setAttribute('fill', toRGB(col));

    const remain = Math.ceil((TIMER_DURATION_MS - elapsedMs) / 1000);
    if (timerLabel) timerLabel.textContent = `${Math.max(0, remain)}s`;

    if (mouth) {
      const qy = lerp(70, 58, t); // Smile -> neutral
      mouth.setAttribute('d', `M35,60 Q50,${qy} 65,60`);
    }
  }

  // ---------- Karten-Interaktion ----------
  function flipCard(card, toFaceUp) {
    if (toFaceUp) {
      card.dataset.state = 'faceup';
      card.classList.add('is-flipped');
      card.setAttribute('aria-label', 'Karte aufgedeckt: ' + card.dataset.symbol);
    } else {
      card.dataset.state = 'facedown';
      card.classList.remove('is-flipped');
      card.setAttribute('aria-label', 'Karte verdeckt');
    }
  }

  function setMatched(card) {
    card.dataset.state = 'matched';
    card.classList.add('matched');
    card.classList.remove('mismatch');
    card.setAttribute('disabled', 'true');
  }

  function setMismatch(card) {
    card.classList.add('mismatch');
  }

  function clearMismatch(card) {
    card.classList.remove('mismatch');
  }

  function onCardClick(e) {
    const btn = e.currentTarget;
    if (lock) return;
    if (btn.dataset.state !== 'facedown') return;

    flipCard(btn, true);

    const pick = { el: btn, symbol: btn.dataset.symbol };

    if (!firstPick) {
      firstPick = pick;
      return;
    }

    // zweiter Pick
    secondPick = pick;
    lock = true;
    board.classList.add('no-click');

    moves += 1; // ein Versuch
    if (moveEl) moveEl.textContent = String(moves);

    if (firstPick.symbol === secondPick.symbol) {
      setTimeout(() => {
        setMatched(firstPick.el);
        setMatched(secondPick.el);

        found += 1;
        if (foundEl) foundEl.textContent = String(found);

        if (!gameOver) resetTimer(true);

        firstPick = null;
        secondPick = null;
        lock = false;
        board.classList.remove('no-click');

        if (found === totalPairs) {
          gameOver = true;
          stopTimer();
          flashWinBanner();
        }
      }, 220);
    } else {
      setMismatch(firstPick.el);
      setMismatch(secondPick.el);

      setTimeout(() => {
        flipCard(firstPick.el, false);
        flipCard(secondPick.el, false);
      }, 500);

      setTimeout(() => {
        clearMismatch(firstPick.el);
        clearMismatch(secondPick.el);
        firstPick = null;
        secondPick = null;
        lock = false;
        board.classList.remove('no-click');
      }, 850);
    }
  }

  function flashWinBanner() {
    const banner = document.createElement('div');
    banner.textContent = '🎉 Alle Paare gefunden!';
    banner.className = 'position-fixed top-0 start-50 translate-middle-x mt-3 px-4 py-2 rounded-3 bg-success text-white shadow';
    document.body.appendChild(banner);
    setTimeout(() => banner.remove(), 1800);
  }

  function bindCardEvents() {
    const cards = board.querySelectorAll('.memory-card');
    cards.forEach(c => c.addEventListener('click', onCardClick));
    cards.forEach(c => c.addEventListener('keydown', (ev) => {
      if (ev.key === ' ' || ev.key === 'Enter') {
        ev.preventDefault();
        c.click();
      }
    }));
  }

  // ---------- New Game (AJAX) ----------
  // Wichtig: Bei AJAX-Start muss das Board neu gerendert werden
  function hookNewGameForm() {
    const form = document.getElementById('newGameForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
      e.preventDefault();

      // Aus Formular lesen (IDs müssen existieren)
      const pairsInput = document.getElementById('pairs');
      const nameInput = document.getElementById('playerName');
      const playerCountInput = document.getElementById('playerCount');

      const pairsVal = parseInt((pairsInput && pairsInput.value) ? pairsInput.value : '8', 10);
      const nameVal = nameInput ? nameInput.value : 'Player';
      const pCountVal = playerCountInput ? parseInt(playerCountInput.value || '1', 10) : 1;

      // 1) Server informieren (optional – du hast das bisher gemacht)
      const payload = new URLSearchParams();
      payload.set('playerName', nameVal);
      payload.set('pairs', String(pairsVal));
      payload.set('playerCount', String(pCountVal));

      fetch('/game/newui/new', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: payload.toString()
      })
      .then(() => {
        // 2) UI umschalten (wie vorher)
        const setup_container = document.querySelector('.game-setup-container');
        const game_container = document.querySelector('.game-container');
        if (setup_container) setup_container.classList.add('hidden');
        if (game_container) game_container.classList.remove('hidden');

        // 3) Board komplett neu erzeugen
        totalPairs = pairsVal;
        board.dataset.totalPairs = String(totalPairs);

        // Counters resetten
        resetCounters();

        // DOM neu rendern und Events neu binden
        renderBoard(totalPairs);
        bindCardEvents();

        // Deck + Timer neu
        initBoard();
      })
      .catch(() => {
        alert('Fehler beim Starten des Spiels!');
      });
    });
  }

  // ---------- Buttons, die Setup/Game togglen ----------
  function hookGameWindowButtons() {
    document.querySelectorAll('.game-window-button').forEach(btn => {
      btn.addEventListener('click', function () {
        const setup_container = document.querySelector('.game-setup-container');
        const game_container = document.querySelector('.game-container');
        if (setup_container) setup_container.classList.remove('hidden');
        if (game_container) game_container.classList.add('hidden');
      });
    });
  }

  // ---------- Init ----------
  function firstInit() {
    // Beim initialen Laden: Board anhand des serverseitigen data-total-pairs bauen,
    // falls das HTML schon die richtige Anzahl Karten enthält, lassen wir es – sonst bauen wir sicher neu.
    const existingCards = board.querySelectorAll('.memory-card').length;
    if (existingCards !== totalPairs * 2) {
      renderBoard(totalPairs);
    }
    bindCardEvents();
    resetCounters();
    initBoard();

    hookNewGameForm();
    hookGameWindowButtons();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', firstInit);
  } else {
    firstInit();
  }
})();
