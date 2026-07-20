import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { CompraBoletos } from './compra-boletos';
import { AuthService } from '../../services/auth';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';

describe('CompraBoletos', () => {
  let component: CompraBoletos;
  let fixture: ComponentFixture<CompraBoletos>;
  const listarMios = vi.fn();
  const consultarAsientos = vi.fn(() => of({
    funcionId: 7,
    capacidad: 23,
    asientosOcupados: [2, 5]
  }));

  const funcion = {
    id: 7,
    fecha: '2026-07-25',
    hora: '19:30',
    precio: 20,
    capacidad: 23,
    pelicula: { id: 3, titulo: 'Pelicula de prueba' }
  };

  beforeEach(async () => {
    listarMios.mockClear();
    consultarAsientos.mockClear();

    await TestBed.configureTestingModule({
      imports: [CompraBoletos],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isLoggedIn: () => true,
            getToken: () => 'token',
            getEmail: () => 'cliente@test.com',
            getRol: () => 'USER',
            getUsuario: () => 'Cliente',
            logout: () => undefined
          }
        },
        { provide: FuncionService, useValue: { listarPublicas: () => of([funcion]) } },
        {
          provide: BoletoService,
          useValue: {
            listarMios,
            consultarAsientos,
            comprar: () => of({
              id: 10,
              asiento: 3,
              precio: 20,
              estado: 'ACTIVO',
              usuario: { id: 2, nombre: 'Cliente', email: 'cliente@test.com' },
              funcion
            })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CompraBoletos);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('carga funciones sin consultar el historial', () => {
    expect(component.funciones).toHaveLength(1);
    expect(listarMios).not.toHaveBeenCalled();
  });

  it('organiza la capacidad en filas de diez con un pasillo y estados', () => {
    component.cambiarFuncion(7);
    fixture.detectChanges();

    expect(consultarAsientos).toHaveBeenCalledWith(7);
    expect(component.filas.map(fila => fila.asientos.length)).toEqual([10, 10, 3]);
    expect(component.filas.map(fila => fila.etiqueta)).toEqual(['A', 'B', 'C']);
    expect(component.asientosDisponibles).toBe(21);
    expect(component.asientoOcupado(2)).toBe(true);
    expect(fixture.nativeElement.querySelectorAll('.seat-row')).toHaveLength(3);
  });

  it('impide seleccionar un asiento ocupado', () => {
    component.cambiarFuncion(7);
    component.seleccionarAsiento(2);
    expect(component.compra.asiento).toBeNull();

    component.seleccionarAsiento(3);
    expect(component.compra.asiento).toBe(3);
  });
});
