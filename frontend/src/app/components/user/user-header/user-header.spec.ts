import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { UserHeader } from './user-header';
import { AuthService } from '../../../services/auth';

describe('UserHeader', () => {
  let component: UserHeader;
  let fixture: ComponentFixture<UserHeader>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserHeader],
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
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserHeader);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('obtiene la inicial del nombre del usuario', () => {
    component.usuario = 'Ana Torres';
    component.email = 'ana@test.com';

    expect(component.inicialUsuario).toBe('A');
  });

  it('muestra accesos para comprar entradas y consultar mis boletos', () => {
    const nodos = fixture.nativeElement.querySelectorAll('.subnav-item') as NodeListOf<HTMLAnchorElement>;
    const enlaces = Array.from(nodos);
    const textos = enlaces.map(enlace => enlace.textContent?.trim());

    expect(textos).toContain('Comprar entradas');
    expect(textos).toContain('Mis boletos');
    expect(enlaces.some(enlace => enlace.getAttribute('href')?.includes('#comprar-entradas'))).toBe(true);
    expect(enlaces.some(enlace => enlace.getAttribute('href')?.includes('#mis-boletos'))).toBe(true);
  });
});
