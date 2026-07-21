export interface PeliculaResumen {
  id: number;
  titulo: string;
}

export interface Funcion {
  id: number;
  fecha: string;
  hora: string;
  precio: number;
  capacidad: number;
  pelicula: PeliculaResumen;
}

export interface ProgramarFuncionRequest {
  fecha: string;
  hora: string;
  precio: number | null;
  capacidad: number | null;
  peliculaId: number | null;
}
