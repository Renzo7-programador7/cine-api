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
  email = '';
  rol = '';
  usuario = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.email = this.auth.getEmail();
    this.rol = this.auth.getRol();
    this.usuario = this.auth.getUsuario();
    console.log(this.rol);
    this.cdr.detectChanges();
  }

  logout() {
    this.auth.logout();
    this.router.navigate([this.router.url]);
    window.location.reload();
  }

}
