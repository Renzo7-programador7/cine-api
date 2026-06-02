import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FuncionService } from '../../services/funcion';
import { PeliculaService } from '../../services/pelicula';

@Component({
  selector: 'app-funciones',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './funciones.html',
  styleUrl: './funciones.css'
})
export class Funciones implements OnInit {
  funciones: any[] = [];
  peliculas: any[] = [];
  nueva = { fecha: '', hora: '', precio: 0, capacidad: 0, pelicula: { id: 0 } };
  error = '';

  constructor(private funcionService: FuncionService,
              private peliculaService: PeliculaService,
              private router: Router) {}

  ngOnInit() {
    this.listar();
    this.peliculaService.listar().subscribe(data => this.peliculas = data);
  }

  listar() {
    this.funcionService.listar().subscribe({
      next: (data) => this.funciones = data,
      error: () => this.router.navigate(['/login'])
    });
  }

  crear() {
    this.funcionService.crear(this.nueva).subscribe({
      next: () => {
        this.listar();
        this.nueva = { fecha: '', hora: '', precio: 0, capacidad: 0, pelicula: { id: 0 } };
      },
      error: (e) => this.error = e.error?.message || 'Error al crear'
    });
  }

  eliminar(id: number) {
    this.funcionService.eliminar(id).subscribe({
      next: () => this.listar()
    });
  }
}