import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';
import { API_BASE_URL } from '../config/api.config';
import { Boleto, ComprarBoletoRequest, DisponibilidadAsientos } from '../models/boleto.models';

@Injectable({ providedIn: 'root' })
export class BoletoService {
  private readonly url = `${API_BASE_URL}/boletos`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` });
  }

  listar(): Observable<Boleto[]> {
    return this.http.get<Boleto[]>(this.url, { headers: this.headers() });
  }

  listarMios(): Observable<Boleto[]> {
    return this.http.get<Boleto[]>(`${this.url}/mios`, { headers: this.headers() });
  }

  consultarAsientos(funcionId: number): Observable<DisponibilidadAsientos> {
    return this.http.get<DisponibilidadAsientos>(
      `${this.url}/funciones/${funcionId}/asientos`,
      { headers: this.headers() }
    );
  }

  comprar(request: ComprarBoletoRequest): Observable<Boleto> {
    return this.http.post<Boleto>(this.url, request, { headers: this.headers() });
  }

  cancelar(id: number): Observable<Boleto> {
    return this.http.patch<Boleto>(
      `${this.url}/${id}/cancelar`,
      {},
      { headers: this.headers() }
    );
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`, { headers: this.headers() });
  }
}
