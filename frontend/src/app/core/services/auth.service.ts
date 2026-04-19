import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ApiResponse } from '../../shared/models/api-responde.model';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/models/auth.model';
import { UserResponse } from '../../shared/models/user.model';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'vantan_token';
const USER_KEY  = 'vantan_user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private accessToken = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  private currentUser = signal<UserResponse | null>(
    JSON.parse(localStorage.getItem(USER_KEY) ?? 'null')
  );

  readonly isAuthenticated = computed(() => this.accessToken() !== null);
  readonly user = computed(() => this.currentUser());
  readonly token = computed(() => this.accessToken());

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${environment.apiUrl}/auth/register`, request
    ).pipe(
      tap(response => {
        if (response.success && response.data) this.setSession(response.data);
      })
    );
  }

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${environment.apiUrl}/auth/login`, request
    ).pipe(
      tap(response => {
        if (response.success && response.data) this.setSession(response.data);
      })
    );
  }

  logout(): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${environment.apiUrl}/auth/logout`, {}
    ).pipe(
      tap(() => this.clearSession())
    );
  }

  getProfile(): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(
      `${environment.apiUrl}/users/me`
    );
  }

  getAccessToken(): string | null {
    return this.accessToken();
  }

  private setSession(auth: AuthResponse): void {
    const user: UserResponse = {
      id: auth.user.id,
      email: auth.user.email,
      firstName: auth.user.firstName,
      lastName: auth.user.lastName,
      role: auth.user.role,
      active: true,
      createdAt: new Date().toISOString()
    };

    localStorage.setItem(TOKEN_KEY, auth.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(user));

    this.accessToken.set(auth.accessToken);
    this.currentUser.set(user);
  }

  clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.accessToken.set(null);
    this.currentUser.set(null);
  }
}