import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { PeliculaService } from '../../services/pelicula';
import { FuncionService } from '../../services/funcion';
import { AuthService } from '../../services/auth';
import { UserLayout } from '../user/user-layout/user-layout';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, UserLayout],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  peliculas: any[] = [];
  funciones: any[] = [];
  funcionesPorPelicula: Record<number, any[]> = {};
  cargando = true;
  error = '';
  peliculaSeleccionada: any = null;

  constructor(
    private peliculaService: PeliculaService,
    private funcionService: FuncionService,
    public auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    this.cargando = true;

    this.peliculaService.listarPublicas().subscribe({
      next: (data) => {

        this.peliculas = data;
        this.cargando = false;
        this.cdr.detectChanges();

      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudieron cargar las películas.';
        this.cargando = false;
      }
    });

    this.funcionService.listarPublicas().subscribe({
      next: (data) => {
        this.funciones = data;
        this.funcionesPorPelicula = {};
        this.funciones.forEach(funcion => {
          const peliculaId = funcion.pelicula?.id;
          if (peliculaId) {
            if (!this.funcionesPorPelicula[peliculaId]) {
              this.funcionesPorPelicula[peliculaId] = [];
            }
            this.funcionesPorPelicula[peliculaId].push(funcion);
          }
        });

        this.peliculas = this.peliculas.filter(
          pelicula => this.funcionesPorPelicula[pelicula.id]
        );
        this.cdr.detectChanges();
      }
    });
  }

  seleccionarPelicula(p: any) {
    this.peliculaSeleccionada = this.peliculaSeleccionada?.id === p.id ? null : p;
  }

  comprarFuncion(funcionId: number): void {
    const destino = `/boletos/comprar?funcionId=${funcionId}`;

    if (this.auth.isLoggedIn()) {
      this.router.navigateByUrl(destino);
      return;
    }

    this.router.navigate(['/login'], {
      queryParams: { returnUrl: destino }
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
