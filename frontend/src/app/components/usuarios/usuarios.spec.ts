import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { Usuarios } from './usuarios';
import { AuthService } from '../../services/auth';

describe('Usuarios', () => {
  let component: Usuarios;
  let fixture: ComponentFixture<Usuarios>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Usuarios],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: {
            getToken: () => 'token-de-prueba',
            getEmail: () => 'admin@test.com',
            getRol: () => 'ADMIN',
            getUsuario: () => 'Administrador',
            logout: () => undefined
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Usuarios);
    component = fixture.componentInstance;
    httpTesting = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    httpTesting.expectOne('http://localhost:8080/api/usuarios').flush([]);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('abre el modal de eliminacion sin usar confirmacion del navegador', () => {
    const usuario = { id: 7, nombre: 'Cliente', email: 'cliente@test.com', rol: 'USER' };

    component.confirmarEliminar(usuario);

    expect(component.usuarioPendienteEliminar).toEqual(usuario);
  });
});
