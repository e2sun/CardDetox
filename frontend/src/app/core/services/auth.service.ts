import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, User } from '../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSubject = new BehaviorSubject<User | null>(this.loadUser());
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  get currentUser(): User | null {
    return this.userSubject.value;
  }

  get isLoggedIn(): boolean {
    return !!this.currentUser;
  }

  register(data: { username: string; email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, data).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  login(data: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, data).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  refreshUser(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${environment.apiUrl}/auth/me`).pipe(
      tap(res => {
        const user: User = { ...res };
        this.userSubject.next(user);
        localStorage.setItem('cd_user', JSON.stringify(user));
      })
    );
  }

  logout(): void {
    localStorage.removeItem('cd_token');
    localStorage.removeItem('cd_user');
    this.userSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('cd_token');
  }

  updateLocalUser(updates: Partial<User>): void {
    if (this.currentUser) {
      const updated = { ...this.currentUser, ...updates };
      this.userSubject.next(updated);
      localStorage.setItem('cd_user', JSON.stringify(updated));
    }
  }

  private handleAuth(res: AuthResponse): void {
    localStorage.setItem('cd_token', res.token);
    const user: User = {
      userId: res.userId,
      username: res.username,
      email: res.email,
      detoxTokens: res.detoxTokens,
      spinAvailable: res.spinAvailable,
      totalOrders: res.totalOrders,
      totalSaved: res.totalSaved
    };
    this.userSubject.next(user);
    localStorage.setItem('cd_user', JSON.stringify(user));
  }

  private loadUser(): User | null {
    const stored = localStorage.getItem('cd_user');
    return stored ? JSON.parse(stored) : null;
  }
}
