import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserLayout } from '../user/user-layout/user-layout';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-proximamente',
  standalone: true,
  imports: [CommonModule, RouterModule, UserLayout],
  templateUrl: './proximamente.html',
  styleUrl: './proximamente.css'
})
export class Proximamente implements OnInit {
  autenticado = false;

  constructor(private auth: AuthService) {}

  ngOnInit(): void {
    this.autenticado = this.auth.isLoggedIn();
  }
}
