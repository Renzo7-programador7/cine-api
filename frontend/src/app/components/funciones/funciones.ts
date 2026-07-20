import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FuncionService } from '../../services/funcion';
import { PeliculaService } from '../../services/pelicula';
import { AdminLayout } from "../admin/admin-layout/admin-layout";
import { HttpErrorResponse } from '@angular/common/http';
import { Funcion, PeliculaResumen, ProgramarFuncionRequest } from '../../models/funcion.models';

@Component({
  selector: 'app-funciones',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule, AdminLayout],
  templateUrl: './funciones.html',
  styleUrl: './funciones.css'
})
export class Funciones implements OnInit {
  funciones: Funcion[] = [];
  peliculas: PeliculaResumen[] = [];
  nueva: ProgramarFuncionRequest = this.formularioVacio();
  error = '';
  exito = '';
  enviando = false;
  eliminandoId: number | null = null;
  hoy = this.fechaLocalActual();

  constructor(
    private funcionService: FuncionService,
    private peliculaService: PeliculaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.listar();
    this.peliculaService.listar().subscribe(data => {
      this.peliculas = data;
      this.cdr.detectChanges();
    });
  }

  listar() {
    this.funcionService.listar().subscribe({
      next: (data) => {
        this.funciones = data;
        this.cdr.detectChanges();
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  programar() {
    if (this.enviando) {
      return;
    }

    const pelicula = this.peliculas.find(item => item.id === this.nueva.peliculaId);
    const confirmar = confirm(
      `¿Publicar la función de "${pelicula?.titulo ?? 'la película seleccionada'}" ` +
      `el ${this.nueva.fecha} a las ${this.nueva.hora}?`
    );
    if (!confirmar) {
      return;
    }

    this.error = '';
    this.exito = '';
    this.enviando = true;

    this.funcionService.programar(this.nueva).subscribe({
      next: () => {
        this.enviando = false;
        this.exito = 'Función programada y publicada en cartelera correctamente.';
        this.listar();
        this.nueva = this.formularioVacio();
        this.cdr.detectChanges();
      },
      error: (e: HttpErrorResponse) => {
        this.enviando = false;
        this.error = e.error?.message || 'No se pudo programar la funcion';
        this.cdr.detectChanges();
      }
    });
  }

  confirmarEliminar(funcion: Funcion): void {
    const confirmar = confirm(
      `¿Está seguro de eliminar la función de "${funcion.pelicula.titulo}" ` +
      `del ${funcion.fecha} a las ${funcion.hora}?`
    );
    if (confirmar) {
      this.eliminar(funcion.id);
    }
  }

  eliminar(id: number) {
    if (this.eliminandoId !== null) {
      return;
    }
    this.error = '';
    this.exito = '';
    this.eliminandoId = id;
    this.funcionService.eliminar(id).subscribe({
      next: () => {
        this.eliminandoId = null;
        this.exito = 'Función eliminada correctamente.';
        this.listar();
        this.cdr.detectChanges();
      },
      error: (e: HttpErrorResponse) => {
        this.eliminandoId = null;
        this.error = e.error?.message || 'No se pudo eliminar la función.';
        this.cdr.detectChanges();
      }
    });
  }

  private formularioVacio(): ProgramarFuncionRequest {
    return {
      fecha: '',
      hora: '',
      precio: null,
      capacidad: null,
      peliculaId: null
    };
  }

  private fechaLocalActual(): string {
    const ahora = new Date();
    const zonaLocal = new Date(ahora.getTime() - ahora.getTimezoneOffset() * 60_000);
    return zonaLocal.toISOString().split('T')[0];
  }
}
