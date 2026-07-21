import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';

import { UserLayout } from '../user/user-layout/user-layout';
import { AuthService } from '../../services/auth';
import { PeliculaService } from '../../services/pelicula';
import { FuncionService } from '../../services/funcion';

@Component({
  selector: 'app-proximamente',
  standalone: true,
  imports: [CommonModule, RouterModule, UserLayout],
  templateUrl: './proximamente.html',
  styleUrl: './proximamente.css'
})
export class Proximamente implements OnInit {

  autenticado = false;
  peliculas: any[] = [];
  cargando = true;

  constructor(
    private auth: AuthService,
    private peliculaService: PeliculaService,
    private funcionService: FuncionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.autenticado = this.auth.isLoggedIn();
    this.cargarPeliculasSinFuncion();
  }

  cargarPeliculasSinFuncion(): void {
    forkJoin({
      peliculas: this.peliculaService.listarPublicas(),
      funciones: this.funcionService.listarPublicas()
    }).subscribe({
      next: ({ peliculas, funciones }) => {

        const peliculasConFuncion = new Set(
          funciones.map(funcion => funcion.pelicula.id)
        );

        this.peliculas = peliculas.filter(
          pelicula => !peliculasConFuncion.has(pelicula.id)
        );

        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar las películas próximas:', error);
        this.cargando = false;
      }
    });
  }

  posterClass(genero: string): string {
    const g = (genero || '').toLowerCase();

    if (g.includes('acción') || g.includes('accion')) return 'poster-accion';
    if (g.includes('comedia')) return 'poster-comedia';
    if (g.includes('terror') || g.includes('horror')) return 'poster-terror';
    if (g.includes('drama')) return 'poster-drama';
    if (g.includes('anim') || g.includes('famil')) return 'poster-animacion';
    if (g.includes('ciencia') || g.includes('sci')) return 'poster-scifi';

    return 'poster-default';
  }

}
