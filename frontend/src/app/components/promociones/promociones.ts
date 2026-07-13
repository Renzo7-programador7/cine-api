import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserLayout } from '../user/user-layout/user-layout';

@Component({
  selector: 'app-promociones',
  standalone: true,
  imports: [CommonModule, RouterModule, UserLayout],
  templateUrl: './promociones.html',
  styleUrl: './promociones.css'
})
export class Promociones {}