import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { Peliculas } from './components/peliculas/peliculas';
import { Funciones } from './components/funciones/funciones';
import { Boletos } from './components/boletos/boletos';
import { Usuarios } from './components/usuarios/usuarios';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'peliculas', component: Peliculas },
  { path: 'funciones', component: Funciones },
  { path: 'boletos', component: Boletos },
  { path: 'usuarios', component: Usuarios }
];