import { Component, Input, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-admin-layout',
  imports: [RouterModule],
  standalone: true,
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout implements OnInit {
  email = '';
  rol = '';
  usuario = '';
  constructor(
    private auth: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.email = this.auth.getEmail();
    this.rol = this.auth.getRol();
    this.usuario = this.auth.getUsuario();
  }
  @Input() titulo = '';

  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
