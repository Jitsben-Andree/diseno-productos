import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class AuthDniService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth/paciente`;

  login(dni: string, ticket: string): Observable<any> {
    return this.http.post<any>(this.apiUrl, { dni, ticket }).pipe(
      tap(response => {
        // Guardamos el token específico del portal del paciente
        if (response && response.token) {
          localStorage.setItem('jwt_camarena_paciente', response.token);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('jwt_camarena_paciente');
  }

  estaAutenticado(): boolean {
    return !!localStorage.getItem('jwt_camarena_paciente');
  }
}