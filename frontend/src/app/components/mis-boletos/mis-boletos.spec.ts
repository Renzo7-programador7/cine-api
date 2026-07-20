import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { MisBoletos } from './mis-boletos';
import { AuthService } from '../../services/auth';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';

describe('MisBoletos', () => {
  let component: MisBoletos;
  let fixture: ComponentFixture<MisBoletos>;
  const listarMios = vi.fn();
  const cancelar = vi.fn(() => of({}));
  const listarFunciones = vi.fn();

  const boleto = {
    id: 9,
    asiento: 4,
    precio: 20,
    estado: 'ACTIVO',
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
    listarMios.mockReset();
    listarMios.mockReturnValue(of([boleto]));
    cancelar.mockClear();
    listarFunciones.mockClear();

    await TestBed.configureTestingModule({
      imports: [MisBoletos],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isLoggedIn: () => true,
            getEmail: () => 'cliente@test.com',
            getRol: () => 'USER',
            getUsuario: () => 'Cliente',
            logout: () => undefined
          }
        },
        { provide: BoletoService, useValue: { listarMios, cancelar } },
        { provide: FuncionService, useValue: { listarPublicas: listarFunciones } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MisBoletos);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('consulta solamente los boletos del usuario', () => {
    expect(listarMios).toHaveBeenCalledTimes(1);
    expect(listarFunciones).not.toHaveBeenCalled();
    expect(component.boletos).toHaveLength(1);
    expect(fixture.nativeElement.textContent).toContain('Pelicula de prueba');
  });

  it('mantiene la confirmacion antes de cancelar', () => {
    component.solicitarCancelacion(boleto);
    expect(component.boletoPendiente?.id).toBe(9);
    expect(cancelar).not.toHaveBeenCalled();

    component.cancelar();
    expect(cancelar).toHaveBeenCalledWith(9);
  });
});
