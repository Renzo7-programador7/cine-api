import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Funciones } from './funciones';

describe('Funciones', () => {
  let component: Funciones;
  let fixture: ComponentFixture<Funciones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Funciones],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Funciones);
    component = fixture.componentInstance;
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
});
