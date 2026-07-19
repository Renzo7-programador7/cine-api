import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule, Location } from '@angular/common';
import { ApiError } from '../../models/auth.models';

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
  mostrarPassword = false;
  enviando = false;
  error = '';
  mensaje = '';
  fieldErrors: Record<string, string[]> = {};

  constructor(
    private auth: AuthService,
    private router: Router,
    private location: Location,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    const navigationState = window.history.state as {
      registroExitoso?: string;
      emailRegistrado?: string;
    };
    this.mensaje = navigationState.registroExitoso ?? '';
    this.email = navigationState.emailRegistrado ?? this.email;
    this.cdr.detectChanges();
  }

  validar(): boolean {
    this.fieldErrors = {};
    let valido = true;

    const email = this.email.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailRegex.test(email)) {
      this.fieldErrors['email'] = ['Ingresa un email válido'];
      valido = false;
    }

    if (!this.password) {
      this.fieldErrors['password'] = ['La contraseña es obligatoria'];
      valido = false;
    }

    return valido;
  }

  login() {
    this.error = '';
    this.fieldErrors = {};

    if (!this.validar()) {
      this.cdr.detectChanges();
      return;
    }

    this.enviando = true;
    this.auth.login(this.email, this.password).subscribe({
      next: (res) => {
        this.enviando = false;
        this.auth.guardarSesion(res);

        if (this.auth.getRol() === 'ADMIN') {
          this.router.navigate(['/usuarios']);
        } else {
          this.router.navigate(['/']);
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.enviando = false;
        this.error = 'Credenciales incorrectas';
        const apiError = err.error as ApiError | undefined;
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
