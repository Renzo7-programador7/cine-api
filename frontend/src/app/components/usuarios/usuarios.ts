import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../services/auth';
import { AdminLayout } from "../admin/admin-layout/admin-layout";

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminLayout],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {
  usuarios: any[] = [];

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.listar();
  }

  listar() {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` });
    this.http.get<any[]>('http://localhost:8080/api/usuarios', { headers }).subscribe({
      next: (data) => {
        this.usuarios = data;
        this.cdr.detectChanges();
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  confirmarEliminar(id: number): void {
    const confirmar = confirm('¿Está seguro de eliminar este usuario?');

    if (confirmar) {
      this.eliminar(id);
    }
  }

  eliminar(id: number) {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` });
    this.http.delete(`http://localhost:8080/api/usuarios/${id}`, { headers }).subscribe({
      next: () => {
        this.listar();
        this.cdr.detectChanges();
      }
    });
  }
}
