import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { UserHeader } from './user-header';

describe('UserHeader', () => {
  let component: UserHeader;
  let fixture: ComponentFixture<UserHeader>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserHeader],
      providers: [provideHttpClient(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserHeader);
    component = fixture.componentInstance;
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
});
