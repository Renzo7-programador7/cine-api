import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { Peliculas } from './components/peliculas/peliculas';
import { Funciones } from './components/funciones/funciones';
import { Boletos } from './components/boletos/boletos';
import { Usuarios } from './components/usuarios/usuarios';
import { Proximamente } from './components/proximamente/proximamente';
import { Promociones } from './components/promociones/promociones';
import { CompraBoletos } from './components/compra-boletos/compra-boletos';
import { MisBoletos } from './components/mis-boletos/mis-boletos';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'proximamente', component: Proximamente },
  { path: 'promociones', component: Promociones },
  { path: 'peliculas', component: Peliculas },
  { path: 'funciones', component: Funciones },
  { path: 'boletos/comprar', component: CompraBoletos },
  { path: 'boletos/mios', component: MisBoletos },
  { path: 'boletos', redirectTo: 'boletos/comprar', pathMatch: 'full' },
  { path: 'admin/boletos', component: Boletos },
  { path: 'usuarios', component: Usuarios }
];
