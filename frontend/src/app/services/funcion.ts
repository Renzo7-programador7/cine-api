import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';
import { API_BASE_URL } from '../config/api.config';
import { Funcion, ProgramarFuncionRequest } from '../models/funcion.models';

@Injectable({ providedIn: 'root' })
export class FuncionService {
  private readonly url = `${API_BASE_URL}/funciones`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` });
  }

  listarPublicas(): Observable<Funcion[]> {
    return this.http.get<Funcion[]>(`${this.url}/publicas`);
  }

  listar(): Observable<Funcion[]> {
    return this.http.get<Funcion[]>(this.url, { headers: this.headers() });
  }

  programar(request: ProgramarFuncionRequest): Observable<Funcion> {
    return this.http.post<Funcion>(this.url, request, { headers: this.headers() });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`, { headers: this.headers() });
  }
}
