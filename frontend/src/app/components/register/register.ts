import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router,RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule, Location } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  usuario = { nombre: '', email: '', password: '', rol: 'USER' };
  error = '';
  exito = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private location: Location
  ) { }

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
  volver() {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}
