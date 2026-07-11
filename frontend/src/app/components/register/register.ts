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

  register() {

    this.error = '';
    this.fieldErrors = {};

    this.auth.register(this.usuario).subscribe({
      next: (res) => {
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
