import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { FuncionService } from './funcion';
import { ProgramarFuncionRequest } from '../models/funcion.models';

describe('FuncionService', () => {
  let service: FuncionService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(FuncionService);
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

  it('envia los datos permitidos para programar una funcion', () => {
    const request: ProgramarFuncionRequest = {
      fecha: '2026-07-25',
      hora: '19:30',
      precio: 20,
      capacidad: 120,
      peliculaId: 7
    };

    service.programar(request).subscribe();

    const httpRequest = httpTesting.expectOne('http://localhost:8080/api/funciones');
    expect(httpRequest.request.method).toBe('POST');
    expect(httpRequest.request.body).toEqual(request);
    expect(httpRequest.request.headers.get('Authorization')).toBe('Bearer token-de-prueba');
    httpRequest.flush({
      id: 1,
      ...request,
      pelicula: { id: 7, titulo: 'Pelicula de prueba' }
    });
  });
});
