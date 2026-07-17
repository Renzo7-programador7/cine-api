import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserLayout } from '../user/user-layout/user-layout';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-promociones',
  standalone: true,
  imports: [CommonModule, RouterModule, UserLayout],
  templateUrl: './promociones.html',
  styleUrl: './promociones.css'
})
export class Promociones implements OnInit {
  autenticado = false;

  constructor(private auth: AuthService) {}

  ngOnInit(): void {
    this.autenticado = this.auth.isLoggedIn();
  }
}
