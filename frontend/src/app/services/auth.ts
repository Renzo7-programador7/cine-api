import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { AuthResponse, LoginRequest, RegisterRequest, RegisterResponse } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly url = `${API_BASE_URL}/auth`;

  constructor(private http: HttpClient) { }

  login(email: string, password: string): Observable<AuthResponse> {
    const request: LoginRequest = {
      email: email.trim().toLowerCase(),
      password
    };
    return this.http.post<AuthResponse>(`${this.url}/login`, request);
  }

  register(usuario: RegisterRequest): Observable<RegisterResponse> {
    const request: RegisterRequest = {
      nombre: usuario.nombre.trim(),
      email: usuario.email.trim().toLowerCase(),
      password: usuario.password
    };
    return this.http.post<RegisterResponse>(`${this.url}/register`, request);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('rol');
    localStorage.removeItem('usuario');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token || this.tokenExpirado(token)) {
      this.logout();
      return false;
    }
    return true;
  }

  getEmail(): string {
    return localStorage.getItem('email') || '';
  }

  getRol(): string {
    return localStorage.getItem('rol') || '';
  }

  getUsuario(): string {
    return localStorage.getItem('usuario') || '';
  }

  guardarSesion(data: AuthResponse) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('email', data.email);
    localStorage.setItem('rol', data.rol);
    localStorage.setItem('usuario', data.usuario);
  }

  private tokenExpirado(token: string): boolean {
    try {
      const payload = token.split('.')[1];
      if (!payload) return true;

      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const normalizado = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
      const claims = JSON.parse(atob(normalizado)) as { exp?: number };
      return !claims.exp || claims.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
