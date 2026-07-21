import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../services/auth';
import { BoletoService } from '../../services/boleto';
import { Boleto } from '../../models/boleto.models';
import { ConfirmDialog } from '../shared/confirm-dialog/confirm-dialog';
import { ToastNotification } from '../shared/toast-notification/toast-notification';
import { UserLayout } from '../user/user-layout/user-layout';

@Component({
  selector: 'app-mis-boletos',
  standalone: true,
  imports: [CommonModule, RouterModule, UserLayout, ConfirmDialog, ToastNotification],
  templateUrl: './mis-boletos.html',
  styleUrl: './mis-boletos.css'
})
export class MisBoletos implements OnInit {
  boletos: Boleto[] = [];
  cargando = true;
  cancelandoId: number | null = null;
  boletoPendiente: Boleto | null = null;
  error = '';
  exito = '';

  constructor(
    private auth: AuthService,
    private boletoService: BoletoService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: '/boletos/mios' }
      });
      return;
    }
    this.cargarBoletos();
  }

  cargarBoletos(): void {
    this.cargando = true;
    this.error = '';
    this.boletoService.listarMios().subscribe({
      next: (boletos) => {
        this.boletos = boletos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (e) => {
        this.cargando = false;
        this.error = e.status === 401
          ? 'Tu sesión venció. Inicia sesión nuevamente.'
          : 'No se pudieron cargar tus boletos.';
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
}
