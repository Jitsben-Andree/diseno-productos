import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);
  
  // ¡AQUÍ ESTÁ LA CORRECCIÓN! Apuntamos a /api/empleados
  private apiUrl = `${environment.apiUrl}/empleados`;

  listarUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  crearUsuario(empleado: any): Observable<any> {
    return this.http.post(this.apiUrl, empleado);
  }
}