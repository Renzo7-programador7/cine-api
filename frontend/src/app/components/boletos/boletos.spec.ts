import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Boletos } from './boletos';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';
import { AuthService } from '../../services/auth';

describe('Boletos administrativos', () => {
  let component: Boletos;
  let fixture: ComponentFixture<Boletos>;
  const listar = vi.fn();
  const cancelar = vi.fn(() => of({}));
  const listarFunciones = vi.fn();

  const boleto = {
    id: 9,
    precio: 20,
    estado: 'ACTIVO',
    asiento: 25,
    usuario: { id: 2, nombre: 'Cliente', email: 'cliente@test.com' },
    funcion: {
      id: 7,
      fecha: '2099-07-25',
      hora: '19:30',
      precio: 20,
      capacidad: 100,
      pelicula: { id: 3, titulo: 'Pelicula de prueba' }
    }
  };

  beforeEach(async () => {
    listar.mockReset();
    listar.mockReturnValue(of([boleto]));
    cancelar.mockClear();
    listarFunciones.mockClear();

    await TestBed.configureTestingModule({
      imports: [Boletos],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isLoggedIn: () => true,
            getRol: () => 'ADMIN',
            getToken: () => 'token-de-prueba',
            getEmail: () => 'admin@test.com',
            getUsuario: () => 'Administrador',
            logout: () => undefined
          }
        },
        { provide: BoletoService, useValue: { listar, cancelar } },
        { provide: FuncionService, useValue: { listarPublicas: listarFunciones } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Boletos);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('consulta todos los boletos sin cargar funciones ni compra', () => {
    expect(listar).toHaveBeenCalledTimes(1);
    expect(listarFunciones).not.toHaveBeenCalled();
    expect(component.boletos).toHaveLength(1);
    expect(fixture.nativeElement.textContent).toContain('Pelicula de prueba');
    expect(fixture.nativeElement.textContent).not.toContain('Comprar boleto');
  });

  it('calcula el resumen por estado', () => {
    expect(component.boletosActivos).toBe(1);
    expect(component.boletosCancelados).toBe(0);
  });

  it('solicita confirmacion y cancela mediante la operacion de negocio', () => {
    component.solicitarCancelacion(boleto);
    expect(component.boletoPendiente?.id).toBe(9);
    expect(cancelar).not.toHaveBeenCalled();

    component.cancelar();

    expect(cancelar).toHaveBeenCalledWith(9);
    expect(listar).toHaveBeenCalledTimes(2);
    expect(component.exito).toContain('cancelado correctamente');
  });
});
