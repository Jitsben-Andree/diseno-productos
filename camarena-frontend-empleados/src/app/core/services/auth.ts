import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Inyección moderna de Angular (sin usar el constructor)
  private http = inject(HttpClient);
  
  // Usamos la URL base configurada en los environments
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'jwt_camarena';

  /**
   * Envia las credenciales al backend y, si es exitoso, guarda el token.
   */
  login(credenciales: { email: string; contrasena: string }): Observable<any> {
    // Nota: el backend de Spring Boot espera las propiedades "email" y "password"
    // Adaptamos el objeto credenciales para que coincida con el LoginRequest de Java
    const payload = {
      email: credenciales.email,
      password: credenciales.contrasena
    };

    return this.http.post<any>(`${this.apiUrl}/login`, payload).pipe(
      tap(response => {
        // Interceptamos la respuesta exitosa para guardar el token
        if (response && response.token) {
          localStorage.setItem(this.tokenKey, response.token);
        }
      })
    );
  }

  cerrarSesion(): void {
    localStorage.removeItem(this.tokenKey);
  }

  obtenerToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  estaAutenticado(): boolean {
    return !!this.obtenerToken();
  }
}