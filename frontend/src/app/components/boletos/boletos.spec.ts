import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Boletos } from './boletos';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';
import { AuthService } from '../../services/auth';
import { ComprarBoletoRequest } from '../../models/boleto.models';

describe('Boletos', () => {
  let component: Boletos;
  let fixture: ComponentFixture<Boletos>;
  let solicitudEnviada: ComprarBoletoRequest | undefined;
  let cancelacionId: number | undefined;

  const funcion = {
    id: 7,
    fecha: '2026-07-25',
    hora: '19:30',
    precio: 20,
    capacidad: 100,
    pelicula: { id: 3, titulo: 'Pelicula de prueba' }
  };

  beforeEach(async () => {
    solicitudEnviada = undefined;
    cancelacionId = undefined;
    await TestBed.configureTestingModule({
      imports: [Boletos],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isLoggedIn: () => true,
            getRol: () => 'USER',
            getToken: () => 'token-de-prueba',
            getEmail: () => 'cliente@test.com',
            getUsuario: () => 'Cliente',
            logout: () => undefined
          }
        },
        {
          provide: FuncionService,
          useValue: { listarPublicas: () => of([funcion]) }
        },
        {
          provide: BoletoService,
          useValue: {
            listar: () => of([]),
            listarMios: () => of([]),
            comprar: (request: ComprarBoletoRequest) => {
              solicitudEnviada = request;
              return of({
                id: 1,
                precio: funcion.precio,
                estado: 'ACTIVO',
                asiento: request.asiento,
                usuario: { id: 2, nombre: 'Cliente', email: 'cliente@test.com' },
                funcion
              });
            },
            cancelar: (id: number) => {
              cancelacionId = id;
              return of({});
            },
            eliminar: () => of(undefined)
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Boletos);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('expone destinos para compra e historial desde la navegacion', () => {
    expect(fixture.nativeElement.querySelector('#comprar-entradas')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('#mis-boletos')).toBeTruthy();
  });

  it('calcula el resumen y envia el contrato plano de compra', () => {
    component.compra = { funcionId: 7, asiento: 25 };

    expect(component.funcionSeleccionada?.precio).toBe(20);
    expect(component.capacidadMaxima).toBe(100);

    component.comprar();

    expect(solicitudEnviada).toEqual({ funcionId: 7, asiento: 25 });
    expect(component.exito).toContain('Asiento 25');
    expect(component.compra).toEqual({ funcionId: null, asiento: null });
  });

  it('solicita confirmacion visual antes de comprar', () => {
    component.compra = { funcionId: 7, asiento: 25 };

    component.solicitarCompra();

    expect(component.confirmandoCompra).toBe(true);
    expect(solicitudEnviada).toBeUndefined();
    expect(component.resumenCompra).toContain('asiento 25');
  });

  it('permite cancelar un boleto propio activo y futuro', () => {
    const boleto = {
      id: 9,
      precio: 20,
      estado: 'ACTIVO',
      asiento: 25,
      usuario: { id: 2, nombre: 'Cliente', email: 'cliente@test.com' },
      funcion
    };

    expect(component.puedeCancelar(boleto)).toBe(true);

    component.cancelar(boleto.id);

    expect(cancelacionId).toBe(9);
    expect(component.exito).toContain('cancelado correctamente');
    expect(component.cancelandoId).toBeNull();
  });
});
