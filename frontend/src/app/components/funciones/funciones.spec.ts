import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Funciones } from './funciones';
import { FuncionService } from '../../services/funcion';
import { PeliculaService } from '../../services/pelicula';
import { AuthService } from '../../services/auth';

describe('Funciones', () => {
  let component: Funciones;
  let fixture: ComponentFixture<Funciones>;
  let programacionesEnviadas: number;

  beforeEach(async () => {
    programacionesEnviadas = 0;
    await TestBed.configureTestingModule({
      imports: [Funciones],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            getToken: () => 'token-de-prueba',
            getEmail: () => 'admin@test.com',
            getRol: () => 'ADMIN',
            getUsuario: () => 'Administrador',
            logout: () => undefined
          }
        },
        {
          provide: PeliculaService,
          useValue: { listar: () => of([{ id: 1, titulo: 'Pelicula de prueba' }]) }
        },
        {
          provide: FuncionService,
          useValue: {
            listar: () => of([]),
            programar: () => {
              programacionesEnviadas++;
              return of({});
            },
            eliminar: () => of(undefined)
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Funciones);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('inicia el formulario sin pelicula ni valores numericos', () => {
    expect(component.nueva).toEqual({
      fecha: '',
      hora: '',
      precio: null,
      capacidad: null,
      peliculaId: null
    });
  });

  it('no publica la funcion cuando el administrador cancela la confirmacion', () => {
    const confirmacion = vi.spyOn(window, 'confirm').mockReturnValue(false);
    component.nueva = {
      fecha: '2026-07-25',
      hora: '20:00',
      precio: 20,
      capacidad: 50,
      peliculaId: 1
    };

    component.programar();

    expect(programacionesEnviadas).toBe(0);
    expect(component.enviando).toBe(false);
    confirmacion.mockRestore();
  });
});
