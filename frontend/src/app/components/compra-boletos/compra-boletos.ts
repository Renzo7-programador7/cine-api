import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { combineLatest, Subscription } from 'rxjs';

import { AuthService } from '../../services/auth';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';
import { Boleto, ComprarBoletoRequest } from '../../models/boleto.models';
import { Funcion } from '../../models/funcion.models';
import { ConfirmDialog } from '../shared/confirm-dialog/confirm-dialog';
import { ToastNotification } from '../shared/toast-notification/toast-notification';
import { UserLayout } from '../user/user-layout/user-layout';

interface FilaAsientos {
  etiqueta: string;
  asientos: number[];
}

@Component({
  selector: 'app-compra-boletos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, UserLayout, ConfirmDialog, ToastNotification],
  templateUrl: './compra-boletos.html',
  styleUrl: './compra-boletos.css'
})
export class CompraBoletos implements OnInit, OnDestroy {
  funciones: Funcion[] = [];
  compra: ComprarBoletoRequest = { funcionId: null, asiento: null };
  filas: FilaAsientos[] = [];
  asientosOcupados = new Set<number>();
  cargandoFunciones = true;
  cargandoAsientos = false;
  enviando = false;
  confirmandoCompra = false;
  compraExitosa = false;
  error = '';
  exito = '';
  errorAsientos = '';
  private suscripcionInicial?: Subscription;

  constructor(
    private auth: AuthService,
    private boletoService: BoletoService,
    private funcionService: FuncionService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url }
      });
      return;
    }

    this.suscripcionInicial = combineLatest([
      this.funcionService.listarPublicas(),
      this.route.queryParamMap
    ]).subscribe({
      next: ([funciones, parametros]) => {
        this.funciones = funciones;
        this.cargandoFunciones = false;
        this.aplicarFuncionSolicitada(parametros.get('funcionId'));
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargandoFunciones = false;
        this.error = 'No se pudieron cargar las funciones disponibles.';
        this.cdr.detectChanges();
      }
    });
  }

  ngOnDestroy(): void {
    this.suscripcionInicial?.unsubscribe();
  }

  cambiarFuncion(funcionId: number | null): void {
    this.compra.funcionId = funcionId;
    this.compraExitosa = false;
    this.cargarAsientos(funcionId);
  }

  cargarAsientos(funcionId: number | null): void {
    this.compra.asiento = null;
    this.filas = [];
    this.asientosOcupados.clear();
    this.errorAsientos = '';
    this.cargandoAsientos = false;

    if (funcionId === null) {
      return;
    }

    this.cargandoAsientos = true;
    this.boletoService.consultarAsientos(funcionId).subscribe({
      next: (disponibilidad) => {
        if (this.compra.funcionId !== funcionId) {
          return;
        }
        this.asientosOcupados = new Set(disponibilidad.asientosOcupados);
        this.filas = this.construirFilas(disponibilidad.capacidad);
        this.cargandoAsientos = false;
        this.cdr.detectChanges();
      },
      error: (e) => {
        if (this.compra.funcionId !== funcionId) {
          return;
        }
        this.cargandoAsientos = false;
        this.errorAsientos = e.status === 401
          ? 'Tu sesión venció. Inicia sesión nuevamente para consultar los asientos.'
          : e.error?.message || 'No se pudo consultar la disponibilidad de asientos.';
        this.cdr.detectChanges();
      }
    });
  }

  reintentarAsientos(): void {
    this.cargarAsientos(this.compra.funcionId);
  }

  seleccionarAsiento(asiento: number): void {
    if (!this.asientosOcupados.has(asiento)) {
      this.compra.asiento = asiento;
      this.compraExitosa = false;
    }
  }

  asientoOcupado(asiento: number): boolean {
    return this.asientosOcupados.has(asiento);
  }

  solicitarCompra(): void {
    if (this.enviando || this.compra.funcionId === null || this.compra.asiento === null) {
      return;
    }
    this.confirmandoCompra = true;
  }

  cancelarConfirmacion(): void {
    this.confirmandoCompra = false;
  }

  comprar(): void {
    if (this.enviando || this.compra.funcionId === null || this.compra.asiento === null) {
      return;
    }

    const funcionId = this.compra.funcionId;
    this.confirmandoCompra = false;
    this.enviando = true;
    this.error = '';
    this.exito = '';

    this.boletoService.comprar(this.compra).subscribe({
      next: (boleto: Boleto) => {
        this.exito = `Boleto comprado correctamente. Asiento ${boleto.asiento}.`;
        this.compraExitosa = true;
        this.enviando = false;
        this.cargarAsientos(funcionId);
      },
      error: (e) => {
        this.error = e.error?.message || 'No se pudo comprar el boleto.';
        this.enviando = false;
        this.cargarAsientos(funcionId);
      }
    });
  }

  cerrarNotificacion(): void {
    this.error = '';
    this.exito = '';
  }

  get funcionSeleccionada(): Funcion | undefined {
    return this.funciones.find(funcion => funcion.id === this.compra.funcionId);
  }

  get totalAsientos(): number {
    return this.filas.reduce((total, fila) => total + fila.asientos.length, 0);
  }

  get asientosDisponibles(): number {
    return this.totalAsientos - this.asientosOcupados.size;
  }

  get resumenCompra(): string {
    const funcion = this.funcionSeleccionada;
    return funcion
      ? `Comprarás el asiento ${this.compra.asiento} para "${funcion.pelicula.titulo}" ` +
        `el ${funcion.fecha} a las ${funcion.hora}, por S/ ${funcion.precio.toFixed(2)}.`
      : '';
  }

  private aplicarFuncionSolicitada(parametro: string | null): void {
    if (parametro === null) {
      return;
    }

    const funcionId = Number(parametro);
    const disponible = Number.isInteger(funcionId) && funcionId > 0 &&
      this.funciones.some(funcion => funcion.id === funcionId);

    if (!disponible) {
      this.error = 'La función seleccionada ya no está disponible.';
      this.compra.funcionId = null;
      return;
    }

    if (this.compra.funcionId === funcionId && this.filas.length > 0) {
      return;
    }

    this.compra.funcionId = funcionId;
    this.cargarAsientos(funcionId);
  }

  private construirFilas(capacidad: number): FilaAsientos[] {
    const filas: FilaAsientos[] = [];
    for (let inicio = 1, indice = 0; inicio <= capacidad; inicio += 10, indice++) {
      const fin = Math.min(inicio + 9, capacidad);
      filas.push({
        etiqueta: this.etiquetaFila(indice),
        asientos: Array.from({ length: fin - inicio + 1 }, (_, posicion) => inicio + posicion)
      });
    }
    return filas;
  }

  private etiquetaFila(indice: number): string {
    let etiqueta = '';
    let valor = indice;
    do {
      etiqueta = String.fromCharCode(65 + (valor % 26)) + etiqueta;
      valor = Math.floor(valor / 26) - 1;
    } while (valor >= 0);
    return etiqueta;
  }
}
