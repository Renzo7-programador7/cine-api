import { ChangeDetectorRef, Component } from '@angular/core';
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

  login() {
    this.auth.login(this.email, this.password).subscribe({
      next: (res) => {
        this.auth.guardarSesion(res);

        if (this.auth.getRol() === 'ADMIN') {
          this.router.navigate(['/usuarios']);
        } else {
          this.router.navigate(['/']);
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Credenciales incorrectas';
        const apiError = err.error;
        if (apiError.errors) {
          this.fieldErrors = apiError.errors;
        }
        this.cdr.detectChanges();
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
