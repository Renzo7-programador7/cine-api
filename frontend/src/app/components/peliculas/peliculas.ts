import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { PeliculaService } from '../../services/pelicula';
import { AdminLayout } from "../admin/admin-layout/admin-layout";
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-peliculas',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule, AdminLayout],
  templateUrl: './peliculas.html',
  styleUrl: './peliculas.css'
})
export class Peliculas implements OnInit {
  peliculas: any[] = [];
  nueva = { titulo: '', duracion: null as number | null, clasificacion: '', genero: '' };
  editando: any = null;
  error = '';
  exito = '';
  enviando = false;
  actualizando = false;
  eliminandoId: number | null = null;

  constructor(
    private peliculaService: PeliculaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.listar();
  }

  listar() {
    this.peliculaService.listar().subscribe({
      next: (data) => {
        console.log('Películas:', data);
        this.peliculas = data;
        this.cdr.detectChanges();
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  crear() {
    if (this.enviando) {
      return;
    }
    this.limpiarMensajes();
    this.enviando = true;
    this.peliculaService.crear(this.nueva).subscribe({
      next: (pelicula) => {
        this.enviando = false;
        this.exito = `Película "${pelicula.titulo}" creada correctamente.`;
        this.listar();
        this.nueva = { titulo: '', duracion: null, clasificacion: '', genero: '' };
        this.cdr.detectChanges();
      },
      error: (e: HttpErrorResponse) => {
        this.enviando = false;
        this.error = e.error?.message || 'No se pudo crear la película.';
        this.cdr.detectChanges();
      }
    });
  }

  editar(p: any) {
    this.limpiarMensajes();
    this.editando = { ...p };
  }

  actualizar() {
    if (!this.editando || this.actualizando) {
      return;
    }
    this.limpiarMensajes();
    this.actualizando = true;
    this.peliculaService.actualizar(this.editando.id, this.editando).subscribe({
      next: (pelicula) => {
        this.actualizando = false;
        this.exito = `Película "${pelicula.titulo}" actualizada correctamente.`;
        this.listar();
        this.editando = null;
        this.cdr.detectChanges();
      },
      error: (e: HttpErrorResponse) => {
        this.actualizando = false;
        this.error = e.error?.message || 'No se pudo actualizar la película.';
        this.cdr.detectChanges();
      }
    });
  }

  confirmarEliminar(pelicula: any): void {
    const confirmar = confirm(`¿Está seguro de eliminar "${pelicula.titulo}"?`);

    if (confirmar) {
      this.eliminar(pelicula.id, pelicula.titulo);
    }
  }

  eliminar(id: number, titulo: string) {
    if (this.eliminandoId !== null) {
      return;
    }
    this.limpiarMensajes();
    this.eliminandoId = id;
    this.peliculaService.eliminar(id).subscribe({
      next: () => {
        this.eliminandoId = null;
        this.exito = `Película "${titulo}" eliminada correctamente.`;
        this.listar();
        this.cdr.detectChanges();
      },
      error: (e: HttpErrorResponse) => {
        this.eliminandoId = null;
        this.error = e.error?.message || 'No se pudo eliminar la película.';
        this.cdr.detectChanges();
      }
    });
  }

  cancelarEdicion(): void {
    if (!this.actualizando) {
      this.editando = null;
    }
  }

  private limpiarMensajes(): void {
    this.error = '';
    this.exito = '';
  }
}
