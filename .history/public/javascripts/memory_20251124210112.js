(function () {
  // DOM-Refs
  const board = document.getElementById('board');
  if (!board) return; // Falls View nicht sichtbar

  const moveEl = document.getElementById('moves');
  const foundEl = document.getElementById('found');
  const star = document.getElementById('timerStar');
  const starShape = document.getElementById('starShape');
  const timerLabel = document.getElementById('timerLabel');
  const mouth = document.getElementById('smilePath');

  // Konfiguration
  let totalPairs = parseInt(board.dataset.totalPairs || '2', 10);
  const TIMER_DURATION_MS = 17000;

  // Farben (Start gelb -> Ende rot)
  const startColor = { r: 255, g: 213, b: 79 };   // #FFD54F
  const endColor   = { r: 220, g: 53,  b: 69 };   // #DC3545

  // Spielstatus
  let deck = [];
  let serverDeck = null;   // <- Deck, das wir aus matrix.xml lesen
  let firstPick = null;    // {el, symbol}
  let secondPick = null;
  let lock = false;
  let moves = 0;
  let found = 0;
  let gameOver = false;

  // aktuelle Spiel-Metadaten
  let currentPlayerName = '';
  let currentPlayerCount = 1;
  let currentSessionId = 'default';

  // Timerstatus
  let timerStart = 0;
  let timerRAF = null;
  let lastFoundAtReset = 0;
  let timerRunning = false;

  // WebSocket
  let socket = null;
  let pendingJoinInfo = null;

  // === Utils ===
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

  // -- Deck aus XML des Servers extrahieren ----------------------------------
  function parseDeckFromXml(xml) {
    const $xml = $(xml);
    let values = [];

    // Struktur:
    // <matrix>
    //   <cells>
    //     <cell>
    //       <id>2</id>
    //       <state>FaceDownState</state>
    //     </cell>
    //     ...
    //   </cells>
    // </matrix>
    const cells = $xml.find('matrix > cells > cell');
    if (cells.length > 0) {
      cells.each(function () {
        const $cell = $(this);
        let idText = $cell.find('id').first().text();
        if (idText) {
          idText = idText.trim();
          if (idText.length > 0) {
            values.push(idText);
          }
        }
      });
    }

    return values;
  }

  // --- Board dynamisch nach pairs erzeugen ---
  function renderBoard(pairs) {
    const count = pairs * 2;
    let html = "";
    for (let i = 0; i < count; i++) {
      html += `
        <div class="col">
          <button class="memory-card w-100"
                  type="button"
                  data-state="facedown"
                  aria-label="Karte verdeckt">
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

  function resetCounters() {
    moves = 0;
    found = 0;
    if (moveEl) moveEl.textContent = "0";
    if (foundEl) foundEl.textContent = "0";
  }

  function initBoard() {
    // Deck aus dem Server verwenden, wenn vorhanden und passend
    if (serverDeck && serverDeck.length >= totalPairs * 2) {
      deck = serverDeck.slice(0, totalPairs * 2);
    } else {
      deck = buildDeck(totalPairs);
    }

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
    resetTimer(true);
  }

  // === Timer ===
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
      const qy = lerp(70, 58, t);
      mouth.setAttribute('d', `M35,60 Q50,${qy} 65,60`);
    }
  }

  // --- WebSocket-Flip-Update-Helfer ---
  function sendFlipUpdateFor(card, state) {
    if (!socket || socket.readyState !== WebSocket.OPEN) return;
    if (!currentSessionId) return;

    const cards = Array.from(board.querySelectorAll('.memory-card'));
    const index = cards.indexOf(card);
    if (index < 0) return;

    socket.send(JSON.stringify({
      type: 'flip',
      sessionId: currentSessionId,
      index: index,
      state: state
    }));
  }

  // === Karten-Interaktion & Animationen ===
  function flipCard(card, toFaceUp, silent = false) {
    if (toFaceUp) {
      card.dataset.state = 'faceup';
      card.classList.add('is-flipped');
      card.setAttribute('aria-label', 'Karte aufgedeckt: ' + card.dataset.symbol);
      if (!silent) sendFlipUpdateFor(card, 'faceup');
    } else {
      card.dataset.state = 'facedown';
      card.classList.remove('is-flipped');
      card.setAttribute('aria-label', 'Karte verdeckt');
      if (!silent) sendFlipUpdateFor(card, 'facedown');
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

    flipCard(btn, true); // sendet Flip über WebSocket (lokale Aktion)

    const pick = { el: btn, symbol: btn.dataset.symbol };

    if (!firstPick) {
      firstPick = pick;
      return;
    }

    // zweiter Pick
    secondPick = pick;
    lock = true;
    board.classList.add('no-click');

    moves += 1; // Versuch
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
          submitHighscore();  // Highscore senden
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

  function bindEvents() {
    const cards = board.querySelectorAll('.memory-card');
    cards.forEach(c => c.addEventListener('click', onCardClick));
    cards.forEach(c => c.addEventListener('keydown', (ev) => {
      if (ev.key === ' ' || ev.key === 'Enter') {
        ev.preventDefault();
        c.click();
      }
    }));
  }

  // --- Ajax: CSRF aus hidden Feld ---
  function getCsrfToken() {
    return $('input[name="csrfToken"]').val();
  }

  // --- Highscore-POST ---
  function submitHighscore() {
    if (!currentPlayerName) {
      currentPlayerName = $('#playerNameDisplay').text() || 'Player';
    }
    const csrfToken = getCsrfToken();
    $.ajax({
      url: '/game/highscore',
      method: 'POST',
      contentType: 'application/json',
      dataType: 'json',
      headers: {
        'Csrf-Token': csrfToken,
        'X-CSRF-Token': csrfToken
      },
      data: JSON.stringify({
        playerName: currentPlayerName || 'Player',
        pairs: totalPairs,
        moves: moves
      })
    });
  }

  // --- Highscores laden + Tabelle füllen (gefiltert nach totalPairs) ---
  function loadHighscores() {
    $.getJSON('/game/highscores', { pairs: totalPairs }, function(list) {
      const $body = $('#highscoreBody');
      $body.empty();
      list.forEach(function(entry, idx) {
        const row = `
          <tr>
            <td>${idx + 1}</td>
            <td>${entry.playerName}</td>
            <td>${entry.pairs}</td>
            <td>${entry.moves}</td>
          </tr>`;
        $body.append(row);
      });
      $('#highscoreTitle').text(`Highscores (${totalPairs} Paare)`);
      $('#highscoreArea').show();
    });
  }

  // --- WebSocket: Verbindung + ServerPush-Handling ---
  function initWebSocket() {
    try {
      const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
      const url = protocol + '://' + window.location.host + '/ws/game';
      socket = new WebSocket(url);

      socket.onopen = function() {
        console.log('WebSocket verbunden');
        sendJoinNow();
      };

      socket.onmessage = function(event) {
        handleServerMessage(event.data);
      };

      socket.onclose = function() {
        console.log('WebSocket geschlossen');
      };
    } catch (e) {
      console.error('WebSocket Fehler:', e);
    }
  }

  function sendJoinNow() {
    if (!socket || socket.readyState !== WebSocket.OPEN) return;
    if (!pendingJoinInfo) return;

    socket.send(JSON.stringify({
      type: 'joinSession',
      sessionId: pendingJoinInfo.sessionId,
      playerName: pendingJoinInfo.playerName,
      pairs: pendingJoinInfo.pairs
    }));
    pendingJoinInfo = null;
  }

  function sendJoinOverWebSocket() {
    pendingJoinInfo = {
      playerName: currentPlayerName || 'Player',
      pairs: totalPairs,
      sessionId: currentSessionId || 'default'
    };
    sendJoinNow();
  }

  function handleServerMessage(raw) {
    let msg;
    try { msg = JSON.parse(raw); } catch (e) { return; }

    if (msg.type === 'info' && msg.text) {
      const status = $('#statusMessage');
      status.text(msg.text).show();
    } else if (msg.type === 'flip') {
      const idx = msg.index;
      const state = msg.state;
      const cards = board.querySelectorAll('.memory-card');
      if (idx >= 0 && idx < cards.length) {
        const card = cards[idx];
        const toFaceUp = state === 'faceup';
        // silent = true -> keinen weiteren WebSocket-Flip senden
        flipCard(card, toFaceUp, true);
      }
    }
  }

  // --- New Game über Ajax + JSON (Host/Join) ---
  function hookNewGameForm() {
    $('#newGameForm').on('submit', function(e) {
      e.preventDefault(); // kein klassisches POST

      const nameVal      = $('#playerName').val() || 'Player';
      const pairsVal     = parseInt($('#pairs').val() || '2', 10);
      const pCountVal    = parseInt($('#playerCount').val() || '1', 10);
      const sessionInput = $('#sessionId').val() || '';
      const isHost       = $('#isHost').is(':checked');

      currentPlayerName  = nameVal;
      currentPlayerCount = pCountVal;
      currentSessionId   = (sessionInput.trim() || 'default');

      $('#sessionDisplay').text(currentSessionId);

      const csrfToken = getCsrfToken();

      if (isHost) {
        // HOST: neues Spiel im Backend erzeugen
        $.ajax({
          url: '/game/newui/new-json',
          method: 'POST',
          contentType: 'application/json',
          dataType: 'json',
          headers: {
            'Csrf-Token': csrfToken,
            'X-CSRF-Token': csrfToken
          },
          data: JSON.stringify({
            playerName: nameVal,
            pairs: pairsVal,
            playerCount: pCountVal
          }),
          success: function(resp) {
            totalPairs = resp.pairs || pairsVal;
            board.dataset.totalPairs = String(totalPairs);

            resetCounters();
            renderBoard(totalPairs);
            bindEvents();

            // Deck aus XML holen (vom Backend generiert)
            $.get('/game-matrix', function(xml) {
              const parsed = parseDeckFromXml(xml);
              if (parsed && parsed.length > 0) {
                serverDeck = parsed;
                console.log('ServerDeck aus XML:', serverDeck);
              } else {
                serverDeck = null;
                console.warn('Konnte kein Deck aus matrix.xml extrahieren, nutze lokales Deck.');
              }
              initBoard(); // init mit serverDeck ODER lokalem Deck
            });

            // Setup verstecken, Spiel zeigen
            $('.game-setup-container').addClass('hidden');
            $('.game-container').removeClass('hidden');

            // Text aktualisieren
            currentPlayerName  = resp.playerName || nameVal;
            currentPlayerCount = resp.playerCount || pCountVal;

            $('#playerNameDisplay').text(currentPlayerName || '–');
            $('#playerCountDisplay').text(currentPlayerCount);
            $('#pairsDisplay').text(resp.pairs || pairsVal);
            $('#pairsTotalDisplay').text(resp.pairs || pairsVal);

            if (resp.message && resp.message.length > 0) {
              $('#statusMessage').text(resp.message).show();
            }

            // WebSocket: Session beitreten
            sendJoinOverWebSocket();
          },
          error: function() {
            alert('Fehler beim Starten des Spiels!');
          }
        });
      } else {
        // JOINER: kein neues Spiel erzeugen – nur vorhandene matrix.xml nutzen
        totalPairs = pairsVal; // Clientseitige Erwartung; kann durch XML noch "korrigiert" werden
        board.dataset.totalPairs = String(totalPairs);

        resetCounters();
        renderBoard(totalPairs);
        bindEvents();

        $.get('/game-matrix', function(xml) {
          const parsed = parseDeckFromXml(xml);
          if (parsed && parsed.length > 0) {
            serverDeck = parsed;
            console.log('ServerDeck aus XML (Joiner):', serverDeck);

            // totalPairs ggf. aus Deck ableiten
            totalPairs = serverDeck.length / 2;
            board.dataset.totalPairs = String(totalPairs);
            $('#pairsDisplay').text(totalPairs);
            $('#pairsTotalDisplay').text(totalPairs);
          } else {
            serverDeck = null;
            console.warn('Joiner: Konnte kein Deck aus matrix.xml extrahieren, nutze lokales Deck.');
          }
          initBoard();
        });

        // Setup verstecken, Spiel zeigen
        $('.game-setup-container').addClass('hidden');
        $('.game-container').removeClass('hidden');

        // Text aktualisieren (kein Backend-Resp)
        $('#playerNameDisplay').text(currentPlayerName || '–');
        $('#playerCountDisplay').text(currentPlayerCount);
        $('#pairsDisplay').text(pairsVal);
        $('#pairsTotalDisplay').text(pairsVal);

        // WebSocket: Session beitreten
        sendJoinOverWebSocket();
      }
    });
  }

  // Preset-Liste: per Ajax JSON holen und Formular füllen
  function hookPresets() {
    $('#presetSelect').on('change', function() {
      const key = $(this).val();
      if (!key) return;

      $.getJSON('/game/preset/' + encodeURIComponent(key), function(resp) {
        // Name soll NICHT ausgefüllt werden
        if (resp.playerName && resp.playerName.trim().length > 0) {
          $('#playerName').val(resp.playerName);
        } else {
          $('#playerName').val('');
        }

        if (resp.pairs) $('#pairs').val(resp.pairs);

        // Spieler immer 1 bei Presets
        $('#playerCount').val(1);
      });
    });
  }

  // Setup/Game umschalten
  function hookGameWindowButtons() {
    $('.game-window-button').on('click', function() {
      $('.game-setup-container').removeClass('hidden');
      $('.game-container').addClass('hidden');
    });
  }

  // Highscore-Button: toggle anzeigen/verstecken
  function hookHighscoreButton() {
    $('#showHighscores').on('click', function() {
      const area = $('#highscoreArea');
      if (area.is(':visible')) {
        area.hide();
      } else {
        loadHighscores();
      }
    });
  }

  // Init, wenn DOM bereit ist
  function firstInit() {
    const existingCards = board.querySelectorAll('.memory-card').length;
    if (existingCards !== totalPairs * 2) {
      renderBoard(totalPairs);
    }
    bindEvents();
    resetCounters();

    // Beim ersten Laden gibt es evtl. noch keine matrix.xml
    // -> initBoard() nimmt dann lokales Deck
    initBoard();

    hookNewGameForm();
    hookPresets();
    hookGameWindowButtons();
    hookHighscoreButton();
    initWebSocket(); // WebSocket-Verbindung aufbauen
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', firstInit);
  } else {
    firstInit();
  }
})();


