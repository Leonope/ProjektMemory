document.addEventListener('DOMContentLoaded', function () {
    Vue.createApp({
        data() {
            return {
                count: 5
            }
        }
    }).mount('#counter');

    const app = Vue.createApp({});
    app.component('preset-selection', {
        template: `
            <div class="d-flex align-items-center gap-2">
                <span class="small text-secondary">Presets:</span>
                <select id="presetSelect" class="form-select form-select-sm" style="min-width: 210px;">
                    <option value="">– None –</option>
                    <option value="easy">Easy (4 Paare)</option>
                    <option value="standard">Standard (8 Paare)</option>
                    <option value="pro">Pro (12 Paare)</option>
                </select>
            </div>
        `
    });

    app.component('select-player-count', {
        template: `
            <select id="playerCount" name="playerCount" class="form-select">
                <option value="1" @if(playerCount == 1){selected}>1</option>
                <option value="2" @if(playerCount == 2){selected}>2</option>
                <option value="3" @if(playerCount == 3){selected}>3</option>
                <option value="4" @if(playerCount == 4){selected}>4</option>
            </select>
        `
    });

    app.mount('#game-vue')
});