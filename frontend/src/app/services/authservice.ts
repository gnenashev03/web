import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../environments/environment';

export interface AuthResponse {
  id: number;
  username: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private authUrl = `${environment.apiUrl}/auth`;
  private currentUserId: number | null = null;
  private currentUsername: string | null = null;
  private isRestoring = false;

  constructor(private http: HttpClient) {
    this.restoreSession();
  }

  private restoreSession(): void {
    if (this.isRestoring) return;
    this.isRestoring = true;

    const userId = sessionStorage.getItem('userId');
    const username = sessionStorage.getItem('username');
    const token = sessionStorage.getItem('auth');
    
    if (userId && username && token) {
      this.currentUserId = +userId;
      this.currentUsername = username;
      console.log(' Session restored from storage:', username);
    }
  }

  login(username: string, password: string): Observable<AuthResponse> {
    const credentials = btoa(`${username}:${password}`);
    
    //  сохраняем токен в sessionStorage до запроса
    sessionStorage.setItem('auth', credentials);
    sessionStorage.setItem('userId', '0');
    sessionStorage.setItem('username', username);
    
    return this.http.get<AuthResponse>(`${this.authUrl}/me`).pipe(
      tap((response) => {
        sessionStorage.setItem('userId', response.id.toString());
        sessionStorage.setItem('username', response.username);
        
        this.currentUserId = response.id;
        this.currentUsername = response.username;
        console.log(' Auth successful, userId:', response.id);
      }),
      catchError((error: HttpErrorResponse) => {
        this.logout();
        return throwError(() => error);
      })
    );
  }

  /*logout(): void {
    sessionStorage.removeItem('auth');
    sessionStorage.removeItem('userId');
    sessionStorage.removeItem('username');
    this.currentUserId = null;
    this.currentUsername = null;
    this.isRestoring = false;
  }*/

  logout(): void {
  sessionStorage.clear();
  
  this.currentUserId = null;
  this.currentUsername = null;
  this.isRestoring = false;
  window.location.href = '/login';
}
  getCurrentUserId(): number | null {
    return this.currentUserId;
  }

  getCurrentUsername(): string | null {
    return this.currentUsername;
  }

  isAuthenticated(): boolean {
    return !!sessionStorage.getItem('auth');
  }
}