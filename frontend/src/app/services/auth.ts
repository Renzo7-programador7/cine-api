import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private url = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.url}/login`, { email, password });
  }

  register(usuario: any): Observable<any> {
    return this.http.post(`${this.url}/register`, usuario);
  }

  guardarToken(token: string) {
    localStorage.setItem('token', token);
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
    return !!this.getToken();
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

  guardarSesion(data: any) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('email', data.email);
    localStorage.setItem('rol', data.rol);
    localStorage.setItem('usuario', data.usuario);
  }
}
