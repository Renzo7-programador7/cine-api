import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
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
  fieldErrors: Record<string, string[]> = {};

  constructor(
    private auth: AuthService,
    private router: Router,
    private location: Location,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.cdr.detectChanges();
  }

  validar(): boolean {
    this.fieldErrors = {};
    let valido = true;

    if (!this.usuario.nombre || this.usuario.nombre.trim().length < 2) {
      this.fieldErrors['nombre'] = ['El nombre es obligatorio y debe tener al menos 2 caracteres'];
      valido = false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!this.usuario.email || !emailRegex.test(this.usuario.email)) {
      this.fieldErrors['email'] = ['Ingresa un email válido'];
      valido = false;
    }

    if (!this.usuario.password || this.usuario.password.length < 6) {
      this.fieldErrors['password'] = ['La contraseña debe tener al menos 6 caracteres'];
      valido = false;
    }

    return valido;
  }

  register() {
    this.error = '';
    this.fieldErrors = {};

    if (!this.validar()) {
      this.cdr.detectChanges();
      return;
    }

    this.auth.register(this.usuario).subscribe({
      next: (res) => {
        this.cdr.detectChanges();
        this.auth.guardarToken(res.token);
        this.router.navigate(['/peliculas']);
      },
      error: (err) => {
        const apiError = err.error;
        this.error = apiError.message ?? 'Error al registrar usuario';
        if (apiError.errors) {
          this.fieldErrors = apiError.errors;
        }
        this.cdr.detectChanges();
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