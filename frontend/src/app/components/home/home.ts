import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { PeliculaService } from '../../services/pelicula';
import { FuncionService } from '../../services/funcion';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  peliculas: any[] = [];
  funciones: any[] = [];
  cargando = true;
  error = '';
  peliculaSeleccionada: any = null;

  constructor(
    private peliculaService: PeliculaService,
    private funcionService: FuncionService,
    public auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    this.cargando = true;
    this.peliculaService.listarPublicas().subscribe({
      next: (data) => {
        this.peliculas = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las películas. Verifica que el servidor esté activo.';
        this.cargando = false;
      }
    });

    this.funcionService.listarPublicas().subscribe({
      next: (data) => this.funciones = data
    });
  }

  funcionesDePelicula(id: number) {
    return this.funciones.filter(f => f.pelicula?.id === id);
  }

  seleccionarPelicula(p: any) {
    this.peliculaSeleccionada = this.peliculaSeleccionada?.id === p.id ? null : p;
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

  irALogin() {
    this.router.navigate(['/login']);
  }
}
