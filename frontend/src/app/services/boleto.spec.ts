import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { BoletoService } from './boleto';
import { ComprarBoletoRequest } from '../models/boleto.models';

describe('BoletoService', () => {
  let service: BoletoService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(BoletoService);
    httpTesting = TestBed.inject(HttpTestingController);
    localStorage.setItem('token', 'token-de-prueba');
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('envia solamente la funcion y el asiento al comprar', () => {
    const request: ComprarBoletoRequest = {
      funcionId: 7,
      asiento: 25
    };

    service.comprar(request).subscribe();

    const httpRequest = httpTesting.expectOne('http://localhost:8080/api/boletos');
    expect(httpRequest.request.method).toBe('POST');
    expect(httpRequest.request.body).toEqual(request);
    expect(httpRequest.request.headers.get('Authorization')).toBe('Bearer token-de-prueba');
    httpRequest.flush({
      id: 1,
      precio: 20,
      estado: 'ACTIVO',
      asiento: 25,
      usuario: { id: 2, nombre: 'Cliente', email: 'cliente@test.com' },
      funcion: {
        id: 7,
        fecha: '2026-07-25',
        hora: '19:30',
        precio: 20,
        capacidad: 100,
        pelicula: { id: 3, titulo: 'Pelicula de prueba' }
      }
    });
  });

  it('consulta los boletos del usuario autenticado', () => {
    service.listarMios().subscribe();

    const httpRequest = httpTesting.expectOne('http://localhost:8080/api/boletos/mios');
    expect(httpRequest.request.method).toBe('GET');
    expect(httpRequest.request.headers.get('Authorization')).toBe('Bearer token-de-prueba');
    httpRequest.flush([]);
  });

  it('solicita la cancelacion del boleto seleccionado', () => {
    service.cancelar(9).subscribe();

    const httpRequest = httpTesting.expectOne('http://localhost:8080/api/boletos/9/cancelar');
    expect(httpRequest.request.method).toBe('PATCH');
    expect(httpRequest.request.body).toEqual({});
    httpRequest.flush({});
  });
});
