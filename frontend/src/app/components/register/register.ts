import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  usuario = { nombre: '', email: '', password: '', rol: 'USER' };
  error = '';
  exito = '';

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    this.auth.register(this.usuario).subscribe({
      next: (res) => {
        this.auth.guardarToken(res.token);
        this.router.navigate(['/peliculas']);
      },
      error: () => {
        this.error = 'Error al registrar usuario';
      }
    });
  }
}