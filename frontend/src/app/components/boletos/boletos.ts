import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';
import { AdminLayout } from "../admin/admin-layout/admin-layout";
import { UserLayout } from '../user/user-layout/user-layout';
import { AuthService } from '../../services/auth';
import { Boleto, ComprarBoletoRequest } from '../../models/boleto.models';
import { Funcion } from '../../models/funcion.models';
import { ConfirmDialog } from '../shared/confirm-dialog/confirm-dialog';
import { ToastNotification } from '../shared/toast-notification/toast-notification';

@Component({
  selector: 'app-boletos',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule, AdminLayout, UserLayout, ConfirmDialog, ToastNotification],
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
  cancelandoId: number | null = null;
  asientos: number[] = [];
  asientosOcupados = new Set<number>();
  cargandoAsientos = false;
  confirmandoCompra = false;
  boletoPendienteCancelar: Boleto | null = null;
  boletoPendienteEliminar: Boleto | null = null;
  error = '';
  exito = '';

  constructor(
    private boletoService: BoletoService,
    private funcionService: FuncionService,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url }
      });
      return;
    }

    this.esAdmin = this.auth.getRol() === 'ADMIN';
    if (!this.esAdmin) {
      this.router.navigate(['/boletos/comprar']);
      return;
    }
    this.cargarBoletos();

    this.funcionService.listarPublicas().subscribe({
      next: (data) => {
        this.funciones = data;
        this.preseleccionarFuncionSolicitada();
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

  solicitarCompra(): void {
    if (this.enviando || this.compra.funcionId === null || this.compra.asiento === null) {
      return;
    }
    this.confirmandoCompra = true;
  }

  private preseleccionarFuncionSolicitada(): void {
    const parametro = this.route.snapshot.queryParamMap.get('funcionId');
    if (parametro === null) {
      return;
    }

    const funcionId = Number(parametro);
    const funcionDisponible = Number.isInteger(funcionId) && funcionId > 0 &&
      this.funciones.some(funcion => funcion.id === funcionId);

    if (!funcionDisponible) {
      this.error = 'La función seleccionada ya no está disponible.';
      return;
    }

    this.compra.funcionId = funcionId;
    this.seleccionarFuncion(funcionId);
  }

  cancelarConfirmacionCompra(): void {
    this.confirmandoCompra = false;
  }

  comprar() {
    if (this.enviando || this.compra.funcionId === null || this.compra.asiento === null) {
      return;
    }
    this.confirmandoCompra = false;

    this.error = '';
    this.exito = '';
    this.enviando = true;
    this.boletoService.comprar(this.compra).subscribe({
      next: (boleto) => {
        this.cargarBoletos();
        this.exito = `Boleto comprado correctamente. Asiento ${boleto.asiento}.`;
        this.compra = {
          funcionId: null,
          asiento: null
        };
        this.asientos = [];
        this.asientosOcupados.clear();
        this.cargandoAsientos = false;
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

  listarMios() {
    this.boletoService.listarMios().subscribe({
      next: (data) => {
        this.boletos = data;
        this.cdr.detectChanges();
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  cargarBoletos() {
    if (this.esAdmin) {
      this.listar();
    } else {
      this.listarMios();
    }
  }

  seleccionarFuncion(funcionId: number | null): void {
    this.compra.asiento = null;
    this.asientos = [];
    this.asientosOcupados.clear();
    this.cargandoAsientos = false;
    this.error = '';

    if (funcionId === null) {
      return;
    }

    this.cargandoAsientos = true;
    this.boletoService.consultarAsientos(funcionId).subscribe({
      next: (disponibilidad) => {
        if (this.compra.funcionId !== funcionId) {
          return;
        }
        this.asientos = Array.from(
          { length: disponibilidad.capacidad },
          (_, indice) => indice + 1
        );
        this.asientosOcupados = new Set(disponibilidad.asientosOcupados);
        this.cargandoAsientos = false;
        this.cdr.detectChanges();
      },
      error: (e) => {
        if (this.compra.funcionId !== funcionId) {
          return;
        }
        this.cargandoAsientos = false;
        this.error = e.error?.message || 'No se pudo consultar la disponibilidad de asientos.';
        this.cdr.detectChanges();
      }
    });
  }

  seleccionarAsiento(asiento: number): void {
    if (!this.asientosOcupados.has(asiento)) {
      this.compra.asiento = asiento;
    }
  }

  asientoOcupado(asiento: number): boolean {
    return this.asientosOcupados.has(asiento);
  }

  get funcionSeleccionada(): Funcion | undefined {
    return this.funciones.find(funcion => funcion.id === this.compra.funcionId);
  }

  get capacidadMaxima(): number {
    return this.funcionSeleccionada?.capacidad ?? 1000;
  }

  get resumenCompra(): string {
    const funcion = this.funcionSeleccionada;
    return funcion
      ? `Comprarás el asiento ${this.compra.asiento} para "${funcion.pelicula.titulo}" ` +
        `el ${funcion.fecha} a las ${funcion.hora}, por S/ ${funcion.precio.toFixed(2)}.`
      : '';
  }

  puedeCancelar(boleto: Boleto): boolean {
    if (boleto.estado !== 'ACTIVO') {
      return false;
    }
    const inicio = new Date(`${boleto.funcion.fecha}T${boleto.funcion.hora}`);
    return inicio.getTime() > Date.now();
  }

  confirmarCancelar(boleto: Boleto): void {
    this.boletoPendienteCancelar = boleto;
  }

  cerrarConfirmacionCancelacion(): void {
    this.boletoPendienteCancelar = null;
  }

  ejecutarCancelacion(): void {
    const boleto = this.boletoPendienteCancelar;
    if (!boleto) {
      return;
    }
    this.boletoPendienteCancelar = null;
    this.cancelar(boleto.id, boleto.funcion.id);
  }

  cancelar(id: number, funcionId?: number): void {
    if (this.cancelandoId !== null) {
      return;
    }
    this.error = '';
    this.exito = '';
    this.cancelandoId = id;
    this.boletoService.cancelar(id).subscribe({
      next: () => {
        this.exito = 'El boleto fue cancelado correctamente.';
        this.cancelandoId = null;
        this.cargarBoletos();
        if (funcionId === this.compra.funcionId) {
          this.seleccionarFuncion(funcionId);
        }
      },
      error: (e) => {
        this.error = e.error?.message || 'No se pudo cancelar el boleto';
        this.cancelandoId = null;
        this.cdr.detectChanges();
      }
    });
  }

  confirmarEliminar(boleto: Boleto): void {
    this.boletoPendienteEliminar = boleto;
  }

  cerrarConfirmacionEliminacion(): void {
    this.boletoPendienteEliminar = null;
  }

  ejecutarEliminacion(): void {
    const boleto = this.boletoPendienteEliminar;
    if (!boleto) {
      return;
    }
    this.boletoPendienteEliminar = null;
    this.eliminar(boleto.id);
  }

  eliminar(id: number) {
    this.boletoService.eliminar(id).subscribe({
      next: () => this.cargarBoletos()
    });
  }

  cerrarNotificacion(): void {
    this.error = '';
    this.exito = '';
  }
}
