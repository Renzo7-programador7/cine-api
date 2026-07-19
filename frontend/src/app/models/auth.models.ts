export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  message: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  rol: 'USER' | 'ADMIN';
  usuario: string;
}

export interface ApiError {
  code?: string;
  message?: string;
  errors?: Record<string, string[]>;
}
