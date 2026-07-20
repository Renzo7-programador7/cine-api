import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Home } from './home';
import { AuthService } from '../../services/auth';
import { FuncionService } from '../../services/funcion';
import { PeliculaService } from '../../services/pelicula';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;
  let router: Router;
  let sesionIniciada: boolean;

  beforeEach(async () => {
    sesionIniciada = true;

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideRouter([]),
        {
          provide: PeliculaService,
          useValue: {
            listarPublicas: () => of([
              { id: 3, titulo: 'Pelicula de prueba', genero: 'Accion', duracion: 120, clasificacion: 'PG-13' }
            ])
          }
        },
        {
          provide: FuncionService,
          useValue: {
            listarPublicas: () => of([
              {
                id: 7,
                fecha: '2026-07-25',
                hora: '19:30',
                precio: 20,
                capacidad: 100,
                pelicula: { id: 3, titulo: 'Pelicula de prueba' }
              }
            ])
          }
        },
        {
          provide: AuthService,
          useValue: {
            isLoggedIn: () => sesionIniciada,
            getRol: () => 'USER',
            getUsuario: () => 'Cliente',
            getEmail: () => 'cliente@test.com',
            logout: () => undefined
          }
        }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('envia al usuario autenticado a la funcion seleccionada', () => {
    const navegar = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);

    component.comprarFuncion(7);

    expect(navegar).toHaveBeenCalledWith('/boletos/comprar?funcionId=7');
  });

  it('conserva la funcion seleccionada cuando solicita iniciar sesion', () => {
    sesionIniciada = false;
    const navegar = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.comprarFuncion(7);

    expect(navegar).toHaveBeenCalledWith(['/login'], {
      queryParams: { returnUrl: '/boletos/comprar?funcionId=7' }
    });
  });
});
