import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { PeliculaService } from '../../services/pelicula';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-peliculas',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './peliculas.html',
  styleUrl: './peliculas.css'
})
export class Peliculas implements OnInit {
  peliculas: any[] = [];
  nueva = { titulo: '', duracion: 0, clasificacion: '', genero: '' };
  editando: any = null;
  error = '';

  constructor(
    private peliculaService: PeliculaService,
    private auth: AuthService,
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
    this.peliculaService.crear(this.nueva).subscribe({
      next: () => {
        this.listar();
        this.nueva = { titulo: '', duracion: 0, clasificacion: '', genero: '' };
      },
      error: (e) => this.error = e.error?.message || 'Error al crear'
    });
  }

  eliminar(id: number) {
    this.peliculaService.eliminar(id).subscribe({
      next: () => this.listar()
    });
  }

  editar(p: any) {
    this.editando = { ...p };
  }

  actualizar() {
    this.peliculaService.actualizar(this.editando.id, this.editando).subscribe({
      next: () => {
        this.listar();
        this.editando = null;
      }
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
