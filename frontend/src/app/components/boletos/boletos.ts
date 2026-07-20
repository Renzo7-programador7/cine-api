import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';
import { AdminLayout } from "../admin/admin-layout/admin-layout";
import { UserLayout } from '../user/user-layout/user-layout';
import { AuthService } from '../../services/auth';
import { Boleto, ComprarBoletoRequest } from '../../models/boleto.models';
import { Funcion } from '../../models/funcion.models';

@Component({
  selector: 'app-boletos',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule, AdminLayout, UserLayout],
  templateUrl: './boletos.html',
  styleUrl: './boletos.css'
})
export class Boletos implements OnInit {
  boletos: Boleto[] = [];
  funciones: Funcion[] = [];
  compra: ComprarBoletoRequest = {
    funcionId: null,
    asiento: null
  };
  esAdmin = false;
  enviando = false;
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

    this.funcionService.listarPublicas().subscribe({
      next: (data) => {
        this.funciones = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'No se pudieron cargar las funciones disponibles.';
        this.cdr.detectChanges();
      }
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

  comprar() {
    if (this.enviando || this.compra.funcionId === null || this.compra.asiento === null) {
      return;
    }

    this.error = '';
    this.exito = '';
    this.enviando = true;
    this.boletoService.comprar(this.compra).subscribe({
      next: (boleto) => {
        if (this.esAdmin) {
          this.listar();
        }
        this.exito = `Boleto comprado correctamente. Asiento ${boleto.asiento}.`;
        this.compra = {
          funcionId: null,
          asiento: null
        };
        this.enviando = false;
        this.cdr.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.message || 'No se pudo comprar el boleto';
        this.enviando = false;
        this.cdr.detectChanges();
      }
    });
  }

  get funcionSeleccionada(): Funcion | undefined {
    return this.funciones.find(funcion => funcion.id === this.compra.funcionId);
  }

  get capacidadMaxima(): number {
    return this.funcionSeleccionada?.capacidad ?? 1000;
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
