import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';

export type ToastType = 'success' | 'error';

@Component({
  selector: 'app-toast-notification',
  standalone: true,
  templateUrl: './toast-notification.html',
  styleUrl: './toast-notification.css'
})
export class ToastNotification implements OnChanges, OnDestroy {
  @Input() mensaje = '';
  @Input() tipo: ToastType = 'success';
  @Output() cerrado = new EventEmitter<void>();

  private temporizador?: ReturnType<typeof setTimeout>;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['mensaje']) {
      this.reiniciarTemporizador();
    }
  }

  cerrar(): void {
    this.limpiarTemporizador();
    this.cerrado.emit();
  }

  ngOnDestroy(): void {
    this.limpiarTemporizador();
  }

  private reiniciarTemporizador(): void {
    this.limpiarTemporizador();
    if (this.mensaje) {
      this.temporizador = setTimeout(() => this.cerrar(), 5000);
    }
  }

  private limpiarTemporizador(): void {
    if (this.temporizador) {
      clearTimeout(this.temporizador);
      this.temporizador = undefined;
    }
  }
}
