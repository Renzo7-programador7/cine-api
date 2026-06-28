import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-hero',
  imports: [],
  templateUrl: './hero.html',
  styleUrl: './hero.css',
})
export class Hero {
    @Input() titulo = '';
    @Input() description = 'Descubre las películas en cartelera y elige tu función favorita';
}
