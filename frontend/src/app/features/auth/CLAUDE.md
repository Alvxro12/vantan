Implementa las páginas Login y Register en el proyecto Angular 19 de Vantan.
Los componentes ya existen en features/auth/. Solo debes completarlos.

══════════════════════════════════════════
CONTEXTO TÉCNICO
══════════════════════════════════════════
- Angular 19, Tailwind CSS 4, TypeScript estricto — NUNCA usar `any`
- Rutas: /login → LoginComponent, /register → RegisterComponent
- GuestGuard ya existe — aplicarlo a ambas rutas en app.routes.ts

ARCHIVOS QUE YA EXISTEN (no recrear, solo importar):
- core/services/auth.service.ts → AuthService
- shared/models/auth.model.ts → LoginRequest, RegisterRequest, AuthResponse
- shared/models/api-response.model.ts → ApiResponse<T>
- shared/models/user.model.ts → UserResponse
- core/guards/auth.guard.ts → authGuard, guestGuard

AUTHSERVICE — métodos disponibles:
- login(req: LoginRequest): Observable<ApiResponse<AuthResponse>>
  → en el tap interno ya guarda el token en signal
- register(req: RegisterRequest): Observable<ApiResponse<AuthResponse>>
  → en el tap interno ya guarda el token en signal
Ambos usan withCredentials: true (refresh token viene en cookie HttpOnly)

══════════════════════════════════════════
FLUJOS
══════════════════════════════════════════
Login exitoso    → navegar a /shop
Login fallido    → mostrar error inline (nunca alert ni console.log)
Register exitoso → navegar a /shop (el backend hace auto-login y devuelve token)
Register fallido → mostrar error inline

══════════════════════════════════════════
MANEJO DE ERRORES HTTP (por status code)
══════════════════════════════════════════
401 → "Email o contraseña incorrectos"
409 → "Este email ya está registrado"
429 → "Demasiados intentos. Espera 15 minutos"
cualquier otro → "Error de conexión. Intenta de nuevo"

El mensaje se muestra bajo el formulario, no en modal ni alert.

══════════════════════════════════════════
REGLAS OBLIGATORIAS
══════════════════════════════════════════
1. ReactiveForms con FormBuilder y Validators — nunca template-driven
2. Tipado estricto en todo — cero `any`
3. Estado loading: booleano o signal — deshabilitar botón mientras carga
4. Toggle show/hide password con variable booleana por campo
5. Register: validador custom en el FormGroup que verifique que
   confirmPassword === password. El backend no recibe confirmPassword.
6. Toda la lógica en el .ts — el template solo renderiza
7. Suscripciones con takeUntilDestroyed() — no unsubscribe manual
8. El RegisterRequest que se envía al backend NO incluye confirmPassword

══════════════════════════════════════════
DISEÑO (adjunto el HTML generado)
══════════════════════════════════════════
Implementa el diseño exacto del HTML adjunto.
Variables CSS del sistema:
  --color-black:   #0A0A0A
  --color-surface: #1A1A1A
  --color-cream:   #F5F0E8
  --color-gold:    #C9A96E
  --color-border:  #333333

Layout split screen:
- Izquierdo (40%): imagen editorial + logo en Playfair Display
- Derecho (60%): formulario sobre #0A0A0A
- Mobile: una columna, logo arriba, formulario abajo

══════════════════════════════════════════
ENTREGABLES ESPERADOS
══════════════════════════════════════════
1. login.component.ts + login.component.html
2. register.component.ts + register.component.html
3. app.routes.ts actualizado con guestGuard en /login y /register