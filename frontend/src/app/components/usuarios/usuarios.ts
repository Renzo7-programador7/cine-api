import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../services/auth';
import { AdminLayout } from "../admin/admin-layout/admin-layout";
import { ConfirmDialog } from '../shared/confirm-dialog/confirm-dialog';
import { ToastNotification } from '../shared/toast-notification/toast-notification';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminLayout, ConfirmDialog, ToastNotification],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {
  usuarios: any[] = [];
  usuarioPendienteEliminar: any = null;
  eliminandoId: number | null = null;
  error = '';
  exito = '';

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

  confirmarEliminar(usuario: any): void {
    this.usuarioPendienteEliminar = usuario;
  }

  cancelarEliminacion(): void {
    this.usuarioPendienteEliminar = null;
  }

  ejecutarEliminacion(): void {
    const usuario = this.usuarioPendienteEliminar;
    if (!usuario) {
      return;
    }
    this.usuarioPendienteEliminar = null;
    this.eliminar(usuario.id, usuario.nombre);
  }

  eliminar(id: number, nombre: string) {
    this.error = '';
    this.exito = '';
    this.eliminandoId = id;
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` });
    this.http.delete(`http://localhost:8080/api/usuarios/${id}`, { headers }).subscribe({
      next: () => {
        this.eliminandoId = null;
        this.exito = `Usuario "${nombre}" eliminado correctamente.`;
        this.listar();
        this.cdr.detectChanges();
      },
      error: (e) => {
        this.eliminandoId = null;
        this.error = e.error?.message || 'No se pudo eliminar el usuario.';
        this.cdr.detectChanges();
      }
    });
  }

  cerrarNotificacion(): void {
    this.error = '';
    this.exito = '';
  }
}
