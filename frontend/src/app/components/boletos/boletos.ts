import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';
import { AdminLayout } from "../admin-layout/admin-layout";

@Component({
  selector: 'app-boletos',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule, AdminLayout],
  templateUrl: './boletos.html',
  styleUrl: './boletos.css'
})
export class Boletos implements OnInit {
  boletos: any[] = [];
  funciones: any[] = [];
  nuevo = { precio: "", estado: 'ACTIVO', asiento: "", funcion: { id: 0 }, usuario: { id: "" } };
  error = '';

  constructor(
    private boletoService: BoletoService,
    private funcionService: FuncionService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.listar();
    this.funcionService.listar().subscribe(data => {
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
    this.boletoService.crear(this.nuevo).subscribe({
      next: () => {
        this.listar();
        this.nuevo = { precio: "", estado: 'ACTIVO', asiento: "", funcion: { id: 0 }, usuario: { id: "" } };
      },
      error: (e) => this.error = e.error?.message || 'Error al crear'
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
