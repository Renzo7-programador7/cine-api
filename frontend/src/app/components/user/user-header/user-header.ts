import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './user-header.html',
  styleUrl: './user-header.css',
})
export class UserHeader implements OnInit {
  autenticado = false;
  email = '';
  rol = '';
  usuario = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.autenticado = this.auth.isLoggedIn();
    if (this.autenticado) {
      this.email = this.auth.getEmail();
      this.rol = this.auth.getRol();
      this.usuario = this.auth.getUsuario();
    }
    this.cdr.detectChanges();
  }

  logout() {
    this.auth.logout();
    this.autenticado = false;
    this.email = '';
    this.rol = '';
    this.usuario = '';
    this.router.navigate(['/']);
  }

  get inicialUsuario(): string {
    const identificador = this.usuario.trim() || this.email.trim();
    return identificador.charAt(0).toUpperCase();
  }

}
