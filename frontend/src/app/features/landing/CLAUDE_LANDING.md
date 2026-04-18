# CLAUDE.md — Vantan Landing Page

Instrucciones para implementar la landing page de Vantan en Angular 19 + Tailwind CSS 4.
Lee este archivo completo antes de escribir una sola línea de código.

---

## Ubicación del trabajo

Todo el código va dentro de:

```
frontend/src/app/features/landing/
```

No toques nada fuera de esta carpeta salvo `styles.scss` para los tokens y `index.html` para las fuentes.

---

## Estructura de archivos a crear

```
landing/
  components/
    navbar/
      navbar.component.ts
      navbar.component.html
      navbar.component.scss
    hero/
      hero.component.ts
      hero.component.html
    collection/
      collection.component.ts
      collection.component.html
    marquee/
      marquee.component.ts
      marquee.component.html
      marquee.component.scss
    lookbook/
      lookbook.component.ts
      lookbook.component.html
    footer/
      footer.component.ts
      footer.component.html
  models/
    landing.models.ts
  landing.component.ts
  landing.component.html
  landing.routes.ts
```

---

## Reglas no negociables

- **Nunca `any`** en TypeScript. Tipado completo siempre.
- **Standalone Components** únicamente. Cero NgModules.
- **Solo clases Tailwind 4**. Sin estilos inline en plantillas HTML.
- **SCSS únicamente** para animaciones que Tailwind no puede expresar (marquee, scroll-line).
- **Signals** para todo estado reactivo: `scrolled`, `visible`, `menuOpen`.
- `LandingComponent` es compositor puro — cero lógica propia, solo importa y ensambla hijos.
- Datos hardcodeados en cada componente. No hay servicios en esta fase.

---

## Design tokens — añadir en styles.scss

```scss
@import "tailwindcss";

@theme {
  --color-black:   #0A0A0A;
  --color-surface: #1A1A1A;
  --color-cream:   #F5F0E8;
  --color-gold:    #C9A96E;
  --color-border:  #333333;

  --font-display: "Playfair Display", Georgia, serif;
  --font-body:    "Inter", system-ui, sans-serif;
  --font-mono:    "JetBrains Mono", monospace;

  --spacing-section: 7rem;
}
```

Fuentes en `index.html`:

```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,500;1,400;1,500&family=Inter:wght@300;400&family=JetBrains+Mono:wght@400&display=swap" rel="stylesheet">
```

---

## Interfaces — models/landing.models.ts

```typescript
export interface NavLink {
  label: string;
  href: string;
}

export interface ProductCard {
  id: string;
  index: string;       // "001 / 003"
  badge?: string;      // "EDICION LIMITADA" | "NUEVO" | "HECHO A MANO"
  name: string;
  price: number;
  material: string;
  color: string;
  slug: string;
}

export interface MarqueeItem {
  text: string;
}

export interface LookbookFrame {
  id: string;
  label: string;       // "[ FRAME 001 ]"
  size: 'large' | 'small';
}
```

---

## Paleta de referencia rápida

| Token | Hex | Uso |
|---|---|---|
| `black` | `#0A0A0A` | Fondo global |
| `surface` | `#1A1A1A` | Cards, placeholders de imagen |
| `cream` | `#F5F0E8` | Texto principal, logo |
| `gold` | `#C9A96E` | Acento, badges, separadores |
| `border` | `#333333` | Bordes sutiles |

Opacidades frecuentes: `cream/70`, `cream/50`, `cream/40`, `cream/30`, `cream/20`, `gold/40`, `gold/30`.

---

## Componente 1 — LandingComponent (raíz)

**landing.component.ts** — solo imports y template:

```typescript
@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [NavbarComponent, HeroComponent, CollectionComponent,
            MarqueeComponent, LookbookComponent, FooterComponent],
  templateUrl: './landing.component.html'
})
export class LandingComponent {}
```

**landing.component.html**:

```html
<app-navbar />
<main>
  <app-hero />
  <app-collection />
  <app-marquee />
  <app-lookbook />
</main>
<app-footer />
```

---

## Componente 2 — NavbarComponent

**Comportamiento:**
- `position: fixed`, `top: 0`, `z-50`, ancho completo.
- Señal `scrolled`: `false` por defecto → `true` cuando `window.scrollY > 20`.
- Cuando `scrolled` es `true`: fondo `bg-black/95 backdrop-blur-md`.
- Cuando `scrolled` es `false`: fondo `bg-transparent`.
- Transición: `transition-all duration-500` siempre aplicada.
- En mobile: botón hamburguesa animado que activa señal `menuOpen`.

**Layout escritorio** — tres columnas en flex:

```
[VANTAN]        [COL · LOOK · ARCH · HIST · CONT]        [login · registrarse]
```

**Estilos nav links:**
```
font-body font-light text-xs tracking-[0.2em] text-cream/70 uppercase
hover:text-cream transition-colors duration-300
```

**Botón "registrarse":**
```
border border-gold text-gold text-xs tracking-[0.2em] px-4 py-2 uppercase font-mono
hover:bg-gold hover:text-black transition-all duration-300
```

**Logo:**
```
font-display tracking-[0.3em] text-cream text-sm uppercase
```

**Lógica TypeScript:**

```typescript
scrolled = signal<boolean>(false);
menuOpen = signal<boolean>(false);

navLinks: NavLink[] = [
  { label: 'COLECCION', href: '/coleccion' },
  { label: 'LOOKBOOK',  href: '/lookbook' },
  { label: 'ARCHIVO',   href: '/archivo' },
  { label: 'HISTORIA',  href: '/historia' },
  { label: 'CONTACTO',  href: '/contacto' },
];

@HostListener('window:scroll')
onScroll(): void {
  this.scrolled.set(window.scrollY > 20);
}
```

---

## Componente 3 — HeroComponent

**Layout:** `min-h-screen bg-black flex flex-col items-center justify-center relative overflow-hidden`

**Fondo radial (CSS inline en el host o clase custom):**
```css
background: radial-gradient(ellipse 60% 60% at 50% 60%, rgba(201,169,110,0.08) 0%, transparent 70%), #0A0A0A;
```

**Elementos de arriba a abajo:**

### Eyebrow
```html
<div class="flex items-center gap-4">
  <div class="w-12 h-px bg-gold"></div>
  <span class="font-mono text-xs tracking-[0.3em] text-gold uppercase">NUEVA COLECCION 2025</span>
  <div class="w-12 h-px bg-gold"></div>
</div>
```

### Título principal
```html
<h1 class="font-display text-6xl md:text-8xl text-center leading-tight">
  <span class="text-cream">Diseñado para los</span><br>
  <span class="text-cream">que se mueven </span><em class="italic text-gold">con</em><br>
  <em class="italic text-gold">intención</em>
</h1>
```
⚠️ "con intención" va siempre en itálica dorada. "Diseñado para los que se mueven" en cream normal.

### Subtítulo
```
text-cream/50 font-body font-light text-sm text-center max-w-md mx-auto leading-relaxed
```
Texto: *"Piezas fabricadas a mano en series limitadas. Cada par es una declaración silenciosa — de forma, de materia, de propósito."*

### CTAs
```html
<div class="flex gap-4 justify-center">
  <a class="bg-cream text-black px-8 py-4 text-xs tracking-[0.2em] font-mono uppercase
            hover:bg-gold transition-all duration-300 cursor-pointer">
    VER COLECCION →
  </a>
  <a class="border border-cream/30 text-cream px-8 py-4 text-xs tracking-[0.2em] font-mono uppercase
            hover:border-cream/60 transition-all duration-300 cursor-pointer">
    LOOKBOOK AW25
  </a>
</div>
```

### Scroll indicator (absolute, bottom-8, centrado)
```html
<div class="absolute bottom-8 left-1/2 -translate-x-1/2 flex flex-col items-center gap-3">
  <span class="font-mono text-xs tracking-[0.3em] text-cream/30">SCROLL</span>
  <div class="scroll-line w-px h-12 bg-gold/50"></div>
</div>
```

Animación en `styles.scss`:
```scss
@keyframes pulse-line {
  0%, 100% { opacity: 0.2; transform: scaleY(0.5); }
  50%       { opacity: 0.8; transform: scaleY(1); }
}
.scroll-line {
  animation: pulse-line 2s ease-in-out infinite;
  transform-origin: top;
}
```

### Label editorial (absolute, bottom-8, right-8)
```html
<span class="absolute bottom-8 right-8 font-mono text-xs text-cream/20 tracking-[0.2em]">
  [ EDITORIAL • AW25 • FRAME 01 ]
</span>
```

---

## Componente 4 — CollectionComponent

**Header de sección:**
- Eyebrow: `EDICION 01 • OTOÑO 25`
- Título: `<span class="text-cream">Tres siluetas, </span><em class="italic text-gold">una intención</em>`

**Grid:** `grid grid-cols-1 md:grid-cols-3 gap-8`

**Datos hardcodeados:**

```typescript
products: ProductCard[] = [
  { id: '1', index: '001 / 003', badge: 'EDICION LIMITADA',
    name: 'Altea Low',   price: 420, material: 'NAPA',     color: 'CREMA',    slug: 'altea-low' },
  { id: '2', index: '002 / 003', badge: 'NUEVO',
    name: 'Moro Oxford', price: 510, material: 'BOX CALF', color: 'OBSIDIANA', slug: 'moro-oxford' },
  { id: '3', index: '003 / 003', badge: 'HECHO A MANO',
    name: 'Sable Boot',  price: 680, material: 'SUEDE',    color: 'TABACO',   slug: 'sable-boot' },
];
```

**Anatomía de cada card:**

```html
<!-- Imagen placeholder -->
<div class="aspect-[3/4] bg-surface relative overflow-hidden group">
  <!-- Gradiente interno simulando luz -->
  <div class="absolute inset-0"
       style="background: radial-gradient(ellipse at center, rgba(201,169,110,0.15) 0%, transparent 70%)">
  </div>
  <!-- Zoom en hover -->
  <div class="absolute inset-0 transition-transform duration-700 group-hover:scale-105 bg-surface"></div>

  <!-- Número edición — esquina superior izquierda -->
  <span class="absolute top-4 left-4 font-mono text-xs text-cream/40 tracking-[0.2em]">
    {{ p.index }}
  </span>

  <!-- Badge — esquina superior derecha (solo si existe) -->
  @if (p.badge) {
    <span class="absolute top-4 right-4 border border-gold/60 text-gold
                 text-xs font-mono tracking-[0.15em] px-2 py-1 uppercase">
      {{ p.badge }}
    </span>
  }

  <!-- Label producto — esquina inferior izquierda -->
  <span class="absolute bottom-4 left-4 font-mono text-xs text-cream/30 tracking-[0.2em]">
    [ PRODUCT • {{ p.name | uppercase }} ]
  </span>
</div>

<!-- Info -->
<div class="pt-4">
  <div class="flex justify-between items-start">
    <h3 class="font-display text-xl text-cream">{{ p.name }}</h3>
    <span class="font-body font-light text-cream/70 text-sm">€ {{ p.price }},00</span>
  </div>
  <div class="flex justify-end mt-1">
    <span class="font-mono text-xs text-cream/30 tracking-[0.15em]">
      {{ p.material }} • {{ p.color }}
    </span>
  </div>
  <a class="inline-flex items-center gap-2 mt-4
            font-mono text-xs text-cream/60 tracking-[0.2em]
            border-b border-cream/20 pb-px
            hover:text-gold hover:border-gold transition-colors duration-300 cursor-pointer">
    VER MAS →
  </a>
</div>
```

---

## Componente 5 — MarqueeComponent

**Items:**

```typescript
items: MarqueeItem[] = [
  { text: 'ALTEA LOW' }, { text: '—' },
  { text: 'MORO OXFORD' }, { text: '—' },
  { text: 'SABLE BOOT' }, { text: '—' },
  { text: 'OTOÑO / INVIERNO 25' }, { text: '—' },
  { text: 'HECHO A MANO' }, { text: '—' },
  { text: 'EDICION LIMITADA' }, { text: '—' },
];
// Duplicar el array para loop continuo: [...items, ...items]
```

**Template:**

```html
<div class="marquee-container relative overflow-hidden py-6 border-y border-border">
  <div class="marquee-track">
    @for (item of doubledItems; track $index) {
      <span [class]="item.text === '—'
        ? 'font-mono text-xs text-gold/40 px-8 whitespace-nowrap'
        : 'font-mono text-xs tracking-[0.3em] text-cream/30 uppercase px-8 whitespace-nowrap'">
        {{ item.text }}
      </span>
    }
  </div>
</div>
```

**marquee.component.scss:**

```scss
.marquee-container {
  &::before, &::after {
    content: '';
    position: absolute;
    top: 0; bottom: 0;
    width: 10%;
    z-index: 2;
    pointer-events: none;
  }
  &::before {
    left: 0;
    background: linear-gradient(to right, #0A0A0A, transparent);
  }
  &::after {
    right: 0;
    background: linear-gradient(to left, #0A0A0A, transparent);
  }
}

.marquee-track {
  display: flex;
  width: max-content;
  animation: marquee 30s linear infinite;

  &:hover { animation-play-state: paused; }
}

@keyframes marquee {
  from { transform: translateX(0); }
  to   { transform: translateX(-50%); }
}
```

---

## Componente 6 — LookbookComponent

**Header:**
- Eyebrow: `LOOKBOOK • AW25`
- Título: `<span class="text-cream">Forma, </span><em class="italic text-gold">luz</em><span class="text-cream">, reposo</span>`

**Datos:**

```typescript
frames: LookbookFrame[] = [
  { id: '01', label: '[ LOOKBOOK • PLATE 01 • EDITORIAL ]', size: 'large' },
  { id: '02', label: '[ LOOKBOOK • PLATE 02 ]',             size: 'small' },
  { id: '03', label: '[ LOOKBOOK • PLATE 03 ]',             size: 'small' },
];
```

**Grid asimétrico:**

```html
<div class="grid grid-cols-12 gap-4">
  <!-- Frame grande: 7 columnas -->
  <div class="col-span-7">
    <div class="aspect-[3/4] bg-surface relative overflow-hidden group">
      <!-- mismo gradiente de luz que las cards -->
      <span class="absolute top-4 left-4 font-mono text-xs text-cream/30 p-0">#01</span>
      <span class="absolute bottom-4 left-4 font-mono text-xs text-cream/25 tracking-[0.15em]">
        [ LOOKBOOK • PLATE 01 • EDITORIAL ]
      </span>
    </div>
  </div>

  <!-- Columna derecha: 5 columnas, 2 frames apilados -->
  <div class="col-span-5 flex flex-col gap-4">
    @for (frame of smallFrames; track frame.id) {
      <div class="flex-1 bg-surface relative overflow-hidden group min-h-48">
        <span class="absolute top-4 left-4 font-mono text-xs text-cream/30">#{{ frame.id }}</span>
        <span class="absolute bottom-4 left-4 font-mono text-xs text-cream/25 tracking-[0.15em]">
          {{ frame.label }}
        </span>
      </div>
    }
  </div>
</div>
```

`smallFrames` = `frames.filter(f => f.size === 'small')`

---

## Componente 7 — FooterComponent

**Contenedor:** `bg-black border-t border-gold/30 py-16 px-8 md:px-16`

**Grid 4 columnas:** `grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12`

### Columna 1 — Marca
```html
<div>
  <p class="font-display text-cream text-lg tracking-[0.3em]">VANTAN</p>
  <p class="font-body font-light text-xs text-cream/40 leading-relaxed max-w-xs mt-4">
    Calzado de autor, fabricado en series cortas en talleres de Alicante y Porto.
    Un par por pie, una historia por par.
  </p>
  <p class="font-mono text-xs text-cream/20 mt-6">— DESDE 2017 · ES · PT</p>
</div>
```

### Columnas 2, 3, 4 — Links

Título de columna: `font-mono text-xs text-gold tracking-[0.25em] mb-4 uppercase`

Links: `font-body font-light text-xs text-cream/40 hover:text-cream transition-colors duration-300 block mb-2`

| Columna | Links |
|---|---|
| COLECCION | Otoño / Invierno 25 · Primavera 25 · Archivo · Ediciones limitadas · Custom |
| EMPRESA | Historia · Artesanos · Materiales · Boutiques · Prensa |
| LEGAL | Términos · Privacidad · Cookies · Envíos y devoluciones · Cuidado |

### Barra inferior
```html
<div class="border-t border-border mt-12 pt-8 flex justify-between items-center">
  <span class="font-mono text-xs text-cream/20">© 2026 Vantan Maison · Todos los derechos reservados</span>
  <span class="font-mono text-xs text-cream/20">Hecho con intención</span>
</div>
```

---

## Fade-in al entrar en viewport (patrón reutilizable)

Aplica en `CollectionComponent`, `LookbookComponent` y `FooterComponent`:

```typescript
// En el componente:
sectionRef = viewChild<ElementRef>('sectionRef');
visible = signal<boolean>(false);

ngAfterViewInit(): void {
  const obs = new IntersectionObserver(
    ([entry]) => { if (entry.isIntersecting) this.visible.set(true); },
    { threshold: 0.1 }
  );
  obs.observe(this.sectionRef()!.nativeElement);
}
```

```html
<!-- En la plantilla: -->
<section #sectionRef
  class="transition-all duration-700"
  [class.opacity-0]="!visible()"
  [class.translate-y-8]="!visible()">
  ...
</section>
```

---

## Ruta lazy — landing.routes.ts

```typescript
import { Routes } from '@angular/router';

export const LANDING_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./landing.component').then(m => m.LandingComponent)
  }
];
```

En `app.routes.ts` asegúrate de tener:
```typescript
{ path: '', loadChildren: () => import('./features/landing/landing.routes').then(m => m.LANDING_ROUTES) }
```

---

## Checklist antes de terminar

- [ ] Cero usos de `any` en TypeScript
- [ ] Todas las interfaces en `landing.models.ts`
- [ ] Tokens `@theme` en `styles.scss`
- [ ] Fuentes importadas en `index.html`
- [ ] `LandingComponent` sin lógica propia
- [ ] Navbar cambia de fondo al scroll con señal
- [ ] Hero: "con intención" en itálica dorada
- [ ] Scroll indicator con animación pulse-line
- [ ] Collection: 3 cards con badge, precio y material
- [ ] Marquee: scroll infinito, pausa en hover, fades laterales
- [ ] Lookbook: grid `col-span-7` / `col-span-5`
- [ ] Footer: 4 columnas + barra inferior
- [ ] Fade-in con IntersectionObserver en collection, lookbook y footer
- [ ] Ruta lazy configurada
