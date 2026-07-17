import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { Register } from './register';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [provideHttpClient(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('rechaza contraseñas que no coinciden', () => {
    component.usuario = { nombre: 'Ana', email: 'ana@test.com', password: '123456' };
    component.confirmarPassword = '654321';

    expect(component.validar()).toBeFalse();
    expect(component.fieldErrors['confirmarPassword']).toBeTruthy();
  });
});
