import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Peliculas } from './peliculas';
import { PeliculaService } from '../../services/pelicula';
import { AuthService } from '../../services/auth';

describe('Peliculas', () => {
  let component: Peliculas;
  let fixture: ComponentFixture<Peliculas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Peliculas],
      providers: [
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
          useValue: {
            listar: () => of([]),
            crear: (pelicula: any) => of({ id: 1, ...pelicula }),
            actualizar: (id: number, pelicula: any) => of({ id, ...pelicula }),
            eliminar: () => of(undefined)
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Peliculas);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('confirma visualmente la creacion de una pelicula', () => {
    component.nueva = {
      titulo: 'Spider-Man: Brand New Day',
      duracion: 135,
      clasificacion: 'PG-13',
      genero: 'Acción'
    };

    component.crear();

    expect(component.exito).toContain('creada correctamente');
    expect(component.enviando).toBe(false);
  });
});
