// public/javascripts/angular-memory.js
(function() {
  'use strict';

  function buildDeck(pairs) {
    const base = Array.from({ length: pairs }, function(_, i) {
      return String(i + 1);
    });
    const doubled = base.concat(base);
    for (let i = doubled.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      const tmp = doubled[i];
      doubled[i] = doubled[j];
      doubled[j] = tmp;
    }
    return doubled;
  }

  angular.module('memoryApp', [])
    .controller('MemoryController', ['$timeout', function($timeout) {
      var vm = this;

      vm.showSetup   = true;
      vm.playerName  = '';
      vm.playerCount = 1;
      vm.pairs       = 4;

      vm.moves       = 0;
      vm.found       = 0;
      vm.cards       = [];
      vm.message     = 'Willkommen bei der Angular-Version von Memory!';

      vm.firstPick   = null; // { index, symbol }
      vm.secondPick  = null;
      vm.lock        = false;

      function initGame() {
        const deck = buildDeck(parseInt(vm.pairs || 2, 10));
        vm.cards = deck.map(function(sym, idx) {
          return {
            id: idx,
            symbol: sym,
            state: 'facedown'
          };
        });
        vm.moves      = 0;
        vm.found      = 0;
        vm.firstPick  = null;
        vm.secondPick = null;
        vm.lock       = false;
      }

      vm.startGame = function() {
        const pairsInt  = parseInt(vm.pairs || 2, 10);
        vm.pairs = isNaN(pairsInt) ? 2 : pairsInt;

        const name = (vm.playerName && vm.playerName.trim().length > 0)
          ? vm.playerName.trim()
          : 'Player';

        initGame();
        vm.showSetup = false;
        vm.message = 'Angular-Spiel für ' + name + ' mit ' + vm.pairs + ' Paar(en) gestartet.';
      };

      vm.flipCard = function(index) {
        if (vm.lock) return;
        var card = vm.cards[index];
        if (!card || card.state !== 'facedown') return;

        card.state = 'faceup';

        if (!vm.firstPick) {
          vm.firstPick = { index: index, symbol: card.symbol };
          return;
        }

        vm.secondPick = { index: index, symbol: card.symbol };
        vm.lock = true;
        vm.moves += 1;

        if (vm.firstPick.symbol === vm.secondPick.symbol) {
          // Match
          $timeout(function() {
            vm.cards[vm.firstPick.index].state  = 'matched';
            vm.cards[vm.secondPick.index].state = 'matched';

            vm.found += 1;
            vm.firstPick  = null;
            vm.secondPick = null;
            vm.lock       = false;
          }, 250);
        } else {
          // Kein Match
          $timeout(function() {
            vm.cards[vm.firstPick.index].state  = 'facedown';
            vm.cards[vm.secondPick.index].state = 'facedown';
          }, 600);

          $timeout(function() {
            vm.firstPick  = null;
            vm.secondPick = null;
            vm.lock       = false;
          }, 650);
        }
      };

      vm.allMatched = function() {
        return vm.pairs > 0 && vm.found === vm.pairs;
      };
    }]);
})();
