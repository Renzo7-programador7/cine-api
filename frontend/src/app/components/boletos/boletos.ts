import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { AdminLayout } from '../admin/admin-layout/admin-layout';
import { ConfirmDialog } from '../shared/confirm-dialog/confirm-dialog';
import { ToastNotification } from '../shared/toast-notification/toast-notification';
import { Boleto } from '../../models/boleto.models';
import { AuthService } from '../../services/auth';
import { BoletoService } from '../../services/boleto';

@Component({
  selector: 'app-boletos',
  standalone: true,
  imports: [CommonModule, AdminLayout, ConfirmDialog, ToastNotification],
  templateUrl: './boletos.html',
  styleUrl: './boletos.css'
})
export class Boletos implements OnInit {
  boletos: Boleto[] = [];
  cargando = true;
  cancelandoId: number | null = null;
  boletoPendiente: Boleto | null = null;
  error = '';
  exito = '';

  constructor(
    private boletoService: BoletoService,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: '/admin/boletos' }
      });
      return;
    }

    if (this.auth.getRol() !== 'ADMIN') {
      this.router.navigate(['/boletos/comprar']);
      return;
    }

    this.cargarBoletos();
  }

  cargarBoletos(): void {
    this.cargando = true;
    this.error = '';
    this.boletoService.listar().subscribe({
      next: (boletos) => {
        this.boletos = boletos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (e) => {
        this.cargando = false;
        this.error = e.status === 403
          ? 'No tienes permisos para consultar todos los boletos.'
          : 'No se pudo cargar la lista de boletos.';
        this.cdr.detectChanges();
      }
    });
  }

  puedeCancelar(boleto: Boleto): boolean {
    if (boleto.estado !== 'ACTIVO') {
      return false;
    }
    const inicio = new Date(`${boleto.funcion.fecha}T${boleto.funcion.hora}`);
    return inicio.getTime() > Date.now();
  }

  solicitarCancelacion(boleto: Boleto): void {
    this.boletoPendiente = boleto;
  }

  cerrarConfirmacion(): void {
    this.boletoPendiente = null;
  }

  cancelar(): void {
    const boleto = this.boletoPendiente;
    if (!boleto || this.cancelandoId !== null) {
      return;
    }

    this.boletoPendiente = null;
    this.cancelandoId = boleto.id;
    this.error = '';
    this.exito = '';
    this.boletoService.cancelar(boleto.id).subscribe({
      next: () => {
        this.cancelandoId = null;
        this.exito = 'El boleto fue cancelado correctamente.';
        this.cargarBoletos();
      },
      error: (e) => {
        this.cancelandoId = null;
        this.error = e.error?.message || 'No se pudo cancelar el boleto.';
        this.cdr.detectChanges();
      }
    });
  }

  cerrarNotificacion(): void {
    this.error = '';
    this.exito = '';
  }

  get boletosActivos(): number {
    return this.boletos.filter(boleto => boleto.estado === 'ACTIVO').length;
  }

  get boletosCancelados(): number {
    return this.boletos.filter(boleto => boleto.estado === 'CANCELADO').length;
  }
}
