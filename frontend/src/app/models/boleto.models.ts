import { Funcion } from './funcion.models';

export interface UsuarioResumen {
  id: number;
  nombre: string;
  email: string;
}

export interface Boleto {
  id: number;
  precio: number;
  estado: string;
  asiento: number;
  usuario: UsuarioResumen;
  funcion: Funcion;
}

export interface ComprarBoletoRequest {
  funcionId: number | null;
  asiento: number | null;
}
