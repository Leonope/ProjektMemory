// public/javascripts/react-memory.js
(function() {
  const e = React.createElement;
  const { useState, useMemo } = React;

  // Hilfsfunktion: Deck erzeugen
  function buildDeck(pairs) {
    const base = Array.from({ length: pairs }, (_, i) => String(i + 1));
    const doubled = base.concat(base);
    for (let i = doubled.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [doubled[i], doubled[j]] = [doubled[j], doubled[i]];
    }
    return doubled;
  }

  // --- GameSetup Component --------------------------------------
  function GameSetup(props) {
    const {
      playerName,
      setPlayerName,
      playerCount,
      setPlayerCount,
      pairs,
      setPairs,
      onStart
    } = props;

    return e(
      'div',
      { className: 'game-setup-container' },
      e(
        'h1',
        { className: 'mb-4 text-center' },
        'Memory – React SPA'
      ),
      e(
        'div',
        { className: 'alert alert-info text-center mb-3' },
        'Wähle Name, Spieleranzahl und Anzahl Paare für die React-Version.'
      ),
      e(
        'form',
        {
          className: 'row g-3 mb-4 justify-content-center',
          onSubmit: function(ev) {
            ev.preventDefault();
            onStart && onStart();
          }
        },
        // Name
        e(
          'div',
          { className: 'col-12 col-md-4' },
          e('label', { htmlFor: 'react-playerName', className: 'form-label' }, 'Name'),
          e('input', {
            id: 'react-playerName',
            type: 'text',
            className: 'form-control',
            value: playerName,
            placeholder: 'Dein Name',
            onChange: function(ev) {
              setPlayerName(ev.target.value);
            }
          })
        ),
        // Paare
        e(
          'div',
          { className: 'col-12 col-md-4' },
          e('label', { htmlFor: 'react-pairs', className: 'form-label' }, 'Anzahl Paare'),
          e('input', {
            id: 'react-pairs',
            type: 'number',
            min: 2,
            max: 18,
            step: 2,
            className: 'form-control',
            value: pairs,
            onChange: function(ev) {
              const v = parseInt(ev.target.value || '2', 10);
              setPairs(isNaN(v) ? 2 : v);
            }
          })
        ),
        // Spieleranzahl
        e(
          'div',
          { className: 'col-12 col-md-4' },
          e('label', { htmlFor: 'react-playerCount', className: 'form-label' }, 'Anzahl Spieler'),
          e(
            'select',
            {
              id: 'react-playerCount',
              className: 'form-select',
              value: playerCount,
              onChange: function(ev) {
                const v = parseInt(ev.target.value || '1', 10);
                setPlayerCount(isNaN(v) ? 1 : v);
              }
            },
            e('option', { value: 1 }, '1'),
            e('option', { value: 2 }, '2'),
            e('option', { value: 3 }, '3'),
            e('option', { value: 4 }, '4')
          )
        ),
        // Start-Button
        e(
          'div',
          { className: 'col-12 text-center' },
          e(
            'button',
            { type: 'submit', className: 'btn btn-primary px-4' },
            'React-Spiel starten'
          )
        )
      )
    );
  }

  // --- GameStatusBar Component ---------------------------------
  function GameStatusBar(props) {
    const { playerName, playerCount, pairs, moves, found } = props;

    const displayName =
      playerName && playerName.trim().length > 0 ? playerName : null;

    return e(
      'div',
      { className: 'row justify-content-center mb-3' },
      e(
        'div',
        { className: 'col-12 col-md-10' },
        e(
          'div',
          { className: 'card bg-secondary-subtle border-0 shadow-sm' },
          e(
            'div',
            {
              className:
                'card-body text-dark d-flex flex-wrap gap-3 align-items-center'
            },
            e(
              'div',
              null,
              e('strong', null, 'Spieler: '),
              displayName ? displayName : e('em', null, '–')
            ),
            e(
              'div',
              null,
              e('strong', null, 'Spieleranzahl: '),
              playerCount
            ),
            e(
              'div',
              null,
              e('strong', null, 'Paare: '),
              pairs
            ),
            e(
              'div',
              { className: 'ms-auto' },
              e('strong', null, 'Versuche: '),
              moves
            ),
            e(
              'div',
              null,
              e('strong', null, 'Gefundene Paare: '),
              found,
              ' / ',
              pairs
            )
          )
        )
      )
    );
  }

  // --- MemoryBoard Component -----------------------------------
  function MemoryBoard(props) {
    const { cards, onFlip } = props;

    return e(
      'div',
      {
        id: 'react-board',
        className:
          'row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-6 g-2 g-md-3 justify-content-center'
      },
      cards.map(function(card, index) {
        return e(
          'div',
          { className: 'col', key: card.id },
          e(
            'button',
            {
              type: 'button',
              className: 'memory-card w-100',
              'data-state': card.state,
              onClick: function() {//event binding
                onFlip && onFlip(index);
              }
            },
            e(
              'div',
              { className: 'card-outer shadow-sm' },
              e(
                'div',
                { className: 'card-inner' },
                e(
                  'div',
                  {
                    className:
                      'card-face card-back d-flex align-items-center justify-content-center'
                  },
                  e('span', { className: 'fs-3 fw-bold' }, '?')
                ),
                e(
                  'div',
                  {
                    className:
                      'card-face card-front d-flex align-items-center justify-content-center'
                  },
                  e(
                    'span',
                    { className: 'symbol fs-3 fw-bold' },
                    card.symbol
                  )
                )
              )
            )
          )
        );
      })
    );
  }

  // --- Root Component: MemoryApp -------------------------------
  function MemoryApp() {
    const [showSetup, setShowSetup] = useState(true);
    const [playerName, setPlayerName] = useState('');
    const [playerCount, setPlayerCount] = useState(1);
    const [pairs, setPairs] = useState(4);

    const [moves, setMoves] = useState(0);//state binding
    const [found, setFound] = useState(0);
    const [cards, setCards] = useState([]);

    const [firstPick, setFirstPick] = useState(null);  // { index, symbol }
    const [secondPick, setSecondPick] = useState(null);
    const [lock, setLock] = useState(false);

    const [message, setMessage] = useState(
      'Willkommen bei der React-Version von Memory!'
    );

    // Neues Deck aufbauen
    function initGame() {
      const deck = buildDeck(pairs);
      const newCards = deck.map(function(sym, idx) {
        return {
          id: idx,
          symbol: sym,
          state: 'facedown'
        };
      });
      setCards(newCards);
      setMoves(0);
      setFound(0);
      setFirstPick(null);
      setSecondPick(null);
      setLock(false);
    }

    function startGame() {
      initGame();
      setShowSetup(false);
      const name = playerName && playerName.trim().length > 0 ? playerName : 'Player';
      setMessage(
        'React-Spiel für ' + name + ' mit ' + pairs + ' Paar(en) gestartet.'
      );
    }

    function flipCard(index) {
      if (lock) return;
      const card = cards[index];
      if (!card || card.state !== 'facedown') return;

      // Karte umdrehen
      const newCards = cards.slice();
      newCards[index] = Object.assign({}, newCards[index], { state: 'faceup' });
      setCards(newCards);

      if (!firstPick) {
        setFirstPick({ index: index, symbol: card.symbol });
        return;
      }

      // zweiter Pick
      const second = { index: index, symbol: card.symbol };
      setSecondPick(second);
      setLock(true);
      setMoves(function(m) { return m + 1; });

      if (firstPick.symbol === second.symbol) {
        // Match
        setTimeout(function() {
          setCards(function(prev) {
            const updated = prev.slice();
            updated[firstPick.index] = Object.assign({}, updated[firstPick.index], { state: 'matched' });
            updated[second.index]    = Object.assign({}, updated[second.index], { state: 'matched' });
            return updated;
          });
          setFound(function(f) { return f + 1; });
          setFirstPick(null);
          setSecondPick(null);
          setLock(false);
        }, 250);
      } else {
        // kein Match
        setTimeout(function() {
          setCards(function(prev) {
            const updated = prev.slice();
            updated[firstPick.index] = Object.assign({}, updated[firstPick.index], { state: 'facedown' });
            updated[second.index]    = Object.assign({}, updated[second.index], { state: 'facedown' });
            return updated;
          });
        }, 600);

        setTimeout(function() {
          setFirstPick(null);
          setSecondPick(null);
          setLock(false);
        }, 650);
      }
    }

    const allMatched = useMemo(
      function() {
        return found === pairs && pairs > 0;
      },
      [found, pairs]
    );

    return e(
      'div',
      { className: 'memory-app-container' },
      showSetup
        ? e(GameSetup, {
            playerName: playerName,
            setPlayerName: setPlayerName,
            playerCount: playerCount,
            setPlayerCount: setPlayerCount,
            pairs: pairs,
            setPairs: setPairs,
            onStart: startGame
          })
        : e(
            'div',
            { className: 'game-container' },
            e(
              'h1',
              { className: 'mb-3 text-center' },
              'Memory – React SPA'
            ),
            message &&
              e(
                'div',
                { className: 'alert alert-info text-center mb-3' },
                message
              ),
            e(GameStatusBar, { 
              playerName: playerName,
              playerCount: playerCount,
              pairs: pairs,
              moves: moves,
              found: found
            }),
            e(MemoryBoard, {
              cards: cards,
              onFlip: flipCard
            }),
            e(
              'div',
              { className: 'text-center mt-3' },
              e(
                'button',
                {
                  className: 'btn btn-outline-light me-2',
                  onClick: function() {
                    setShowSetup(true);
                  }
                },
                'Neues React-Spiel konfigurieren'
              ),
              allMatched &&
                e(
                  'span',
                  { className: 'ms-2 text-success fw-bold' },
                  '🎉 Alle Paare gefunden!'
                )
            )
          )
    );
  }

  // React-App mounten
  document.addEventListener('DOMContentLoaded', function() {
    const rootEl = document.getElementById('react-root');
    if (!rootEl) return;
    const root = ReactDOM.createRoot(rootEl);
    root.render(e(MemoryApp));
  });
})();
