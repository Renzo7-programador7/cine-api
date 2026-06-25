import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule, Location } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email = '';
  password = '';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private location: Location
  ) { }

  login() {
    this.auth.login(this.email, this.password).subscribe({
      next: (res) => {
        this.auth.guardarToken(res.token);
        this.router.navigate(['/peliculas']);
      },
      error: () => {
        this.error = 'Credenciales incorrectas';
      }
    });
  }

  irARegistro() {
    this.router.navigate(['/register']);
  }

  volver() {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}
