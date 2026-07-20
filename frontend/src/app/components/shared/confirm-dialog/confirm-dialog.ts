import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css'
})
export class ConfirmDialog {
  @Input() visible = false;
  @Input() titulo = 'Confirmar operación';
  @Input() mensaje = '';
  @Input() textoConfirmar = 'Confirmar';
  @Input() procesando = false;
  @Input() peligro = false;

  @Output() confirmado = new EventEmitter<void>();
  @Output() cancelado = new EventEmitter<void>();

  confirmar(): void {
    if (!this.procesando) {
      this.confirmado.emit();
    }
  }

  cancelar(): void {
    if (!this.procesando) {
      this.cancelado.emit();
    }
  }

  cerrarDesdeFondo(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.cancelar();
    }
  }

  @HostListener('document:keydown.escape')
  cerrarConEscape(): void {
    if (this.visible) {
      this.cancelar();
    }
  }
}
