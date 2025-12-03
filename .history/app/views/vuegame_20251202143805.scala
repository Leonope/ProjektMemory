@(implicit request: RequestHeader)

@main_bootstrap("Memory – Vue Version") {

  <!-- Karte-/Board-Styling wiederverwenden -->
  <link rel="stylesheet" href='@routes.Assets.versioned("stylesheets/memory.css")'>

  <!-- Root-Container für Vue-App -->
  <div id="vue-app">
    <memory-app></memory-app>
  </div>

  <!-- Vue 3 (global build) -->
  <script src="https://unpkg.com/vue@3/dist/vue.global.prod.js"></script>

  <!-- Deine Vue-Logik -->
  <script src='@routes.Assets.versioned("javascripts/vue-memory.js")'></script>
}

