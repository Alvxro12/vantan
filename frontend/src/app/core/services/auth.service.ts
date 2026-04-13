import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ApiResponse } from '../../shared/models/api-responde.model';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/models/auth.model';
import { UserResponse } from '../../shared/models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // Access token en memoria — nunca en localStorage
  private accessToken = signal<string | null>(null);
  private currentUser = signal<UserResponse | null>(null);

  // Signals públicos de solo lectura
  readonly isAuthenticated = computed(() => this.accessToken() !== null);
  readonly user = computed(() => this.currentUser());
  readonly token = computed(() => this.accessToken());

  constructor(private http: HttpClient) {}

  // ─── Auth ─────────────────────────────────────────────────────────────────

  register(request: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${environment.apiUrl}/auth/register`,
      request,
      { withCredentials: true }
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setSession(response.data);
        }
      })
    );
  }

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${environment.apiUrl}/auth/login`,
      request,
      { withCredentials: true }
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setSession(response.data);
        }
      })
    );
  }

  logout(): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${environment.apiUrl}/auth/logout`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(() => this.clearSession())
    );
  }

  refresh(): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${environment.apiUrl}/auth/refresh`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setSession(response.data);
        }
      })
    );
  }

  getProfile(): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(
      `${environment.apiUrl}/users/me`
    );
  }

  // ─── Token ────────────────────────────────────────────────────────────────

  setAccessToken(token: string): void {
    this.accessToken.set(token);
  }

  getAccessToken(): string | null {
    return this.accessToken();
  }

  // ─── Session ──────────────────────────────────────────────────────────────

  private setSession(auth: AuthResponse): void {
    this.accessToken.set(auth.accessToken);
    if (auth.user) {
      this.currentUser.set({
        id: auth.user.id,
        email: auth.user.email,
        firstName: auth.user.firstName,
        lastName: auth.user.lastName,
        role: auth.user.role,
        active: true,
        createdAt: new Date().toISOString()
      });
    }
  }

  clearSession(): void {
    this.accessToken.set(null);
    this.currentUser.set(null);
  }
}