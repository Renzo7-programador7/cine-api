import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule, Location } from '@angular/common';
import { ApiError, RegisterRequest } from '../../models/auth.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  usuario = { nombre: '', email: '', password: '' };
  confirmarPassword = '';
  mostrarPassword = false;
  enviando = false;
  error = '';
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

    const nombre = this.usuario.nombre.trim();
    if (nombre.length < 2 || nombre.length > 100) {
      this.fieldErrors['nombre'] = ['El nombre debe tener entre 2 y 100 caracteres'];
      valido = false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const email = this.usuario.email.trim();
    if (!email || !emailRegex.test(email) || email.length > 254) {
      this.fieldErrors['email'] = ['Ingresa un email válido'];
      valido = false;
    }

    if (this.usuario.password.length < 6 || this.usuario.password.length > 72) {
      this.fieldErrors['password'] = ['La contraseña debe tener entre 6 y 72 caracteres'];
      valido = false;
    }

    if (this.confirmarPassword !== this.usuario.password) {
      this.fieldErrors['confirmarPassword'] = ['Las contraseñas no coinciden'];
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

    const request: RegisterRequest = { ...this.usuario };
    this.enviando = true;
    this.auth.register(request).subscribe({
      next: (res) => {
        this.enviando = false;
        this.auth.logout();
        this.router.navigate(['/login'], {
          state: {
            registroExitoso: res.message,
            emailRegistrado: res.email
          }
        });
      },
      error: (err) => {
        this.enviando = false;
        const apiError = err.error as ApiError | undefined;
        this.error = apiError?.message ?? 'No se pudo completar el registro';
        if (apiError?.errors) {
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
