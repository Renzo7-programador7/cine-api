import { Component, Input } from '@angular/core';

import { UserHeader } from '../user-header/user-header';
import { UserFooter } from "../user-footer/user-footer";
import { Hero } from "../hero/hero";

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [UserHeader, UserFooter, Hero],
  templateUrl: './user-layout.html',
  styleUrl: './user-layout.css',
})
export class UserLayout {
  @Input() banner: boolean = false;
}
