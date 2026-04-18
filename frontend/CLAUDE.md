# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Commands

```bash
npm start          # Dev server at http://localhost:4200
npm run build      # Production build → dist/frontend/
npm test           # Karma + Jasmine unit tests
npm run watch      # Dev build with watch mode
```

The backend API must be running at `http://localhost:8080/api` for auth flows to work locally.

---

## Architecture

Angular 19 SPA — **standalone components only, zero NgModules**. Strict TypeScript (`noImplicitAny`, `strictTemplates`). Signals for all reactive state.

```
src/app/
├── core/
│   ├── guards/          # authGuard (requires auth), guestGuard (blocks authenticated users)
│   ├── interceptors/    # jwtInterceptor — attaches Bearer token, auto-refreshes on 401
│   └── services/        # AuthService — signals-based: isAuthenticated, user, token
├── shared/
│   └── models/          # ApiResponse<T>, AuthResponse, LoginRequest, UserDto, UserResponse
└── features/
    ├── auth/            # LoginComponent, RegisterComponent (lazy, guestGuard)
    └── landing/         # Full landing page — current focus (Phase 1 Frontend)
```

**Routing:** All routes lazy-loaded via `loadComponent()` / `loadChildren()`. Wildcard redirects to `''`.

**Auth token strategy:**
- Access token: stored in memory via Angular signal, never `localStorage`.
- Refresh token: HttpOnly cookie (backend-managed, `{ withCredentials: true }`).
- On 401: interceptor calls `/auth/refresh`, retries original request, on failure clears session and navigates to `/login`.

---

## Design System

Tailwind CSS 4 with custom `@theme` tokens defined in `src/styles.scss`:

| Token | Hex | Usage |
|---|---|---|
| `black` | `#0A0A0A` | Global background |
| `surface` | `#1A1A1A` | Cards, image placeholders |
| `cream` | `#F5F0E8` | Primary text, logo |
| `gold` | `#C9A96E` | Accents, badges, separators |
| `border` | `#333333` | Subtle borders |

Common opacity variants: `cream/70`, `cream/50`, `cream/40`, `cream/30`, `cream/20`, `gold/40`, `gold/30`.

Fonts (loaded from Google Fonts):
- `font-display` → Playfair Display (headings, editorial)
- `font-body` → Inter (body text)
- `font-mono` → JetBrains Mono (labels, captions, CTAs)

**CSS rules:** Use only Tailwind classes in templates. Use SCSS only for animations that Tailwind cannot express (e.g., `marquee`, `pulse-line`).

---

## Landing Page (active work)

Location: `src/app/features/landing/`

The `LandingComponent` is a **pure compositor** — zero logic of its own, only imports and assembles children:

```
landing/
  components/
    navbar/      # Fixed header, scrolled signal (scrollY > 20), mobile menuOpen signal
    hero/        # min-h-screen, radial gold gradient, scroll indicator with pulse-line animation
    collection/  # 3-card grid, IntersectionObserver fade-in
    marquee/     # Infinite scroll text, pause on hover, CSS gradient edge fades
    lookbook/    # Asymmetric grid col-span-7 / col-span-5, IntersectionObserver fade-in
    footer/      # 4-column grid + bottom bar, IntersectionObserver fade-in
  models/
    landing.models.ts   # NavLink, ProductCard, MarqueeItem, LookbookFrame interfaces
  landing.routes.ts     # LANDING_ROUTES for lazy loadChildren
```

Data is hardcoded per component — no services in this phase.

**Fade-in pattern** (used in collection, lookbook, footer):
```typescript
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

---

## Project Rules

1. **Never use `any`** — fully typed TypeScript everywhere.
2. **Standalone components only** — no NgModules ever.
3. **Signals for all reactive state** — `signal()`, `computed()`, never BehaviorSubject for UI state.
4. **Zero business logic in frontend** — backend is the single source of truth for prices, stock, roles, and validation.
5. **Backend validates everything** — frontend only presents data.

---

## Phase Status

| Phase | Description | Status |
|---|---|---|
| 0 | Docker, base structure | ✅ |
| 1 | Auth + JWT (backend done, frontend in progress) | ✅ BE / ⚠️ FE |
| 2 | Product + Category (CRUD, filters, Redis cache) | ⬜ |
| 3 | Cart (per user, stock validation, backend totals) | ⬜ |
| 4 | Order + Payment (MercadoPago sandbox, webhook) | ⬜ |
| 5 | Admin Panel | ⬜ |
| 7 | Full Angular frontend | ⚠️ in progress |

---

## Environments

- **Dev:** `http://localhost:8080/api` (`src/environments/environment.ts`)
- **Prod:** `https://api.vantan.com/api` (`src/environments/environment.production.ts`)
