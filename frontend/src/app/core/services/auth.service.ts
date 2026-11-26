import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  name: string;
  email: string;
  role: string;
}

export interface AccountProfile {
  name: string;
  email: string;
  favoriteGenre: string | null;
}

const TOKEN_KEY = 'estante-token';
const USER_KEY = 'estante-user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8081/api/auth';
  private readonly accountUrl = 'http://localhost:8081/api/account';

  constructor(private readonly http: HttpClient) {}

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, {
      name,
      email,
      password
    }).pipe(tap(response => this.saveSession(response)));
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, {
      email,
      password
    }).pipe(tap(response => this.saveSession(response)));
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  getRole(): string | null {
    try {
        const storedUser = localStorage.getItem(USER_KEY);
        if (!storedUser) {
        return null;
        }

        const user = JSON.parse(storedUser) as { role?: unknown };
        return typeof user.role === 'string' ? user.role : null;
    } catch {
        return null;
    }
    }

    isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
    }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify({
      name: response.name,
      email: response.email,
      role: response.role
    }));
  }

    getAccountProfile(): Observable<AccountProfile> {
        return this.http.get<AccountProfile>(this.accountUrl);
    }

    updateAccountProfile(profile: AccountProfile): Observable<AuthResponse> {
    return this.http.put<AuthResponse>(this.accountUrl, profile)
        .pipe(tap(response => this.saveSession(response)));
    }

    changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.http.put<void>(`${this.accountUrl}/password`, {
        currentPassword,
        newPassword
    });
    }
}