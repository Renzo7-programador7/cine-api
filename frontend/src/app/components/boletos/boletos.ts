import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { BoletoService } from '../../services/boleto';
import { FuncionService } from '../../services/funcion';

@Component({
  selector: 'app-boletos',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './boletos.html',
  styleUrl: './boletos.css'
})
export class Boletos implements OnInit {
  boletos: any[] = [];
  funciones: any[] = [];
  nuevo = { precio: 0, estado: 'ACTIVO', asiento: 0, funcion: { id: 0 }, usuario: { id: 0 } };
  error = '';

  constructor(private boletoService: BoletoService,
              private funcionService: FuncionService,
              private router: Router) {}

  ngOnInit() {
    this.listar();
    this.funcionService.listar().subscribe(data => this.funciones = data);
  }

  listar() {
    this.boletoService.listar().subscribe({
      next: (data) => this.boletos = data,
      error: () => this.router.navigate(['/login'])
    });
  }

  crear() {
    this.boletoService.crear(this.nuevo).subscribe({
      next: () => {
        this.listar();
        this.nuevo = { precio: 0, estado: 'ACTIVO', asiento: 0, funcion: { id: 0 }, usuario: { id: 0 } };
      },
      error: (e) => this.error = e.error?.message || 'Error al crear'
    });
  }

  eliminar(id: number) {
    this.boletoService.eliminar(id).subscribe({
      next: () => this.listar()
    });
  }
}