import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserLayout } from '../user/user-layout/user-layout';

@Component({
  selector: 'app-proximamente',
  standalone: true,
  imports: [CommonModule, RouterModule, UserLayout],
  templateUrl: './proximamente.html',
  styleUrl: './proximamente.css'
})
export class Proximamente {}