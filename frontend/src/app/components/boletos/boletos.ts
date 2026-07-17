import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';
import { AdminLayout } from "../admin/admin-layout/admin-layout";
import { UserLayout } from '../user/user-layout/user-layout';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-boletos',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule, AdminLayout, UserLayout],
  templateUrl: './boletos.html',
  styleUrl: './boletos.css'
})
export class Boletos implements OnInit {
  boletos: any[] = [];
  funciones: any[] = [];
  nuevo = {
    precio: "",
    estado: 'ACTIVO',
    asiento: "",
    funcion: { id: null as number | null },
    usuario: { id: "" }
  };
  esAdmin = false;
  error = '';
  exito = '';

  constructor(
    private boletoService: BoletoService,
    private funcionService: FuncionService,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.esAdmin = this.auth.getRol() === 'ADMIN';
    if (this.esAdmin) {
      this.listar();
    }

    this.funcionService.listarPublicas().subscribe(data => {
      this.funciones = data;
      this.cdr.detectChanges();
    });
  }

  listar() {
    this.boletoService.listar().subscribe({
      next: (data) => {
        this.boletos = data;
        this.cdr.detectChanges();
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  crear() {
    this.error = '';
    this.exito = '';
    this.boletoService.crear(this.nuevo).subscribe({
      next: () => {
        if (this.esAdmin) {
          this.listar();
        } else {
          this.exito = 'Tu boleto fue comprado correctamente.';
        }
        this.nuevo = {
          precio: "",
          estado: 'ACTIVO',
          asiento: "",
          funcion: { id: null },
          usuario: { id: "" }
        };
        this.cdr.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.message || 'No se pudo comprar el boleto';
        this.cdr.detectChanges();
      }
    });
  }

  confirmarEliminar(id: number): void {
    const confirmar = confirm('¿Está seguro de eliminar este boleto?');

    if (confirmar) {
      this.eliminar(id);
    }
  }

  eliminar(id: number) {
    this.boletoService.eliminar(id).subscribe({
      next: () => this.listar()
    });
  }
}
