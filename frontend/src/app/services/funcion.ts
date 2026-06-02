import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({ providedIn: 'root' })
export class FuncionService {
  private url = 'http://localhost:8080/api/funciones';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` });
  }

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.url, { headers: this.headers() });
  }

  crear(funcion: any): Observable<any> {
    return this.http.post(this.url, funcion, { headers: this.headers() });
  }

  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.url}/${id}`, { headers: this.headers() });
  }
}