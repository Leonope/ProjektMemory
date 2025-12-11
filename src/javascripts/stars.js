(function () {
  const canvas = document.getElementById('stars-canvas');
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  let dpr = Math.max(1, Math.min(2, window.devicePixelRatio || 1)); // limit DPR für Performance
  let width = 0, height = 0;

  // Konfiguration
  const STAR_COUNT = 140;         // normale Sterne
  const SHOOTING_CHANCE = 0.012;  // Spawnchance pro Frame (~1.2%)
  const STAR_SPEED_MIN = 0.02;    // Parallax-Geschwindigkeit
  const STAR_SPEED_MAX = 0.12;
  const SHOOTING_SPEED = 1.6;     // Pixel/ms (wird mit DPR skaliert)
  const SHOOTING_TTL = 1200;      // Lebenszeit einer Schnuppe in ms
  const BG_FADE = 0.18;           // „Trails“: je kleiner desto längere Spuren

  const stars = [];
  const shooting = []; // aktive Sternschnuppen

  // Utilities
  const rand = (a, b) => a + Math.random() * (b - a);
  const clamp = (x, a, b) => Math.max(a, Math.min(b, x));

  function resize() {
    width  = Math.floor(window.innerWidth * dpr);
    height = Math.floor(window.innerHeight * dpr);
    canvas.width = width;
    canvas.height = height;
    canvas.style.width = `${Math.floor(width / dpr)}px`;
    canvas.style.height = `${Math.floor(height / dpr)}px`;
  }

  function initStars() {
    stars.length = 0;
    for (let i = 0; i < STAR_COUNT; i++) {
      stars.push({
        x: Math.random() * width,
        y: Math.random() * height,
        r: rand(0.4, 1.6) * dpr,
        a: rand(0.5, 1), // alpha
        s: rand(STAR_SPEED_MIN, STAR_SPEED_MAX) * dpr, // speed
      });
    }
  }

  function spawnShootingStar() {
    // Start am oberen/ linken Rand mit schrägem Flug nach rechts unten
    const fromTop = Math.random() < 0.5;
    const margin = 40 * dpr;

    const x = fromTop ? rand(margin, width * 0.7) : -margin;
    const y = fromTop ? -margin : rand(margin, height * 0.4);

    const angle = rand(Math.PI * 0.15, Math.PI * 0.35); // 27°–63°
    const speed = SHOOTING_SPEED * dpr;
    const vx = Math.cos(angle) * speed;
    const vy = Math.sin(angle) * speed;

    shooting.push({
      x, y, vx, vy,
      r: rand(1.1, 2.2) * dpr,
      born: performance.now(),
      ttl: SHOOTING_TTL,
      hue: rand(200, 60) // leicht bläulich/gelblich Variation
    });
  }

  let last = performance.now();
  function frame(now) {
    const dt = now - last;
    last = now;

    // Hintergrund leicht abdunkeln für trails
    ctx.globalCompositeOperation = 'source-over';
    ctx.fillStyle = `rgba(0,0,0,${BG_FADE})`;
    ctx.fillRect(0, 0, width, height);

    // Sterne parallax scrollen
    ctx.globalCompositeOperation = 'lighter';
    for (const s of stars) {
      s.x -= s.s * dt;                 // langsame Drift nach links
      if (s.x < -s.r) s.x = width + s.r;
      // leichtes Flimmern
      const twinkle = 0.02 * Math.sin(now * 0.002 + s.y * 0.01);
      const alpha = clamp(s.a + twinkle, 0.2, 1);

      ctx.beginPath();
      ctx.fillStyle = `rgba(255,255,255,${alpha})`;
      ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2);
      ctx.fill();
    }

    // Evtl. neue Sternschnuppe
    if (Math.random() < SHOOTING_CHANCE) spawnShootingStar();

    // Sternschnuppen updaten/zeichnen
    for (let i = shooting.length - 1; i >= 0; i--) {
      const sh = shooting[i];
      const life = now - sh.born;
      if (life > sh.ttl) { shooting.splice(i, 1); continue; }

      sh.x += sh.vx * dt;
      sh.y += sh.vy * dt;

      // Farb-/Alpha-Verlauf
      const t = clamp(life / sh.ttl, 0, 1);
      const alpha = 1 - t;
      const len = 140 * dpr * (1 - t); // Schweif wird kürzer

      // Schweif
      const tx = sh.x - sh.vx * len * 0.04;
      const ty = sh.y - sh.vy * len * 0.04;

      const grad = ctx.createLinearGradient(tx, ty, sh.x, sh.y);
      grad.addColorStop(0, `rgba(255,255,255,0)`);
      grad.addColorStop(0.4, `rgba(255,255,255,${0.35 * alpha})`);
      grad.addColorStop(1, `rgba(255,255,255,${0.9 * alpha})`);

      ctx.strokeStyle = grad;
      ctx.lineWidth = sh.r * 1.6;
      ctx.lineCap = 'round';
      ctx.beginPath();
      ctx.moveTo(tx, ty);
      ctx.lineTo(sh.x, sh.y);
      ctx.stroke();

      // Kopf
      ctx.beginPath();
      ctx.fillStyle = `rgba(255,255,255,${alpha})`;
      ctx.arc(sh.x, sh.y, sh.r, 0, Math.PI * 2);
      ctx.fill();
    }

    requestAnimationFrame(frame);
  }

  function onResize() {
    const wasW = width, wasH = height;
    resize();
    if (wasW === 0 || Math.abs(wasW - width) > 2 || Math.abs(wasH - height) > 2) {
      initStars();
      // Fläche nach Resize komplett schwärzen (sonst Trails-Artefakte)
      ctx.fillStyle = 'rgba(0,0,0,1)';
      ctx.fillRect(0, 0, width, height);
    }
  }

  // Init
  function init() {
    onResize();
    initStars();
    // Start „klarer“ Frame
    ctx.fillStyle = 'rgba(0,0,0,1)';
    ctx.fillRect(0, 0, width, height);
    last = performance.now();
    requestAnimationFrame(frame);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

  window.addEventListener('resize', onResize);
})();
