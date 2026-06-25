import { Component, Input, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-admin-layout',
  imports: [RouterModule],
  standalone: true,
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout implements OnInit {
  constructor(
    private auth: AuthService,
    private router: Router
  ) { }


  ngOnInit(): void { }
  @Input() titulo = '';


  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
