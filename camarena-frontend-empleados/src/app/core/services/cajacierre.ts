import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CajaService {
  private http = inject(HttpClient);
  // Apuntamos al controlador de Caja en Spring Boot (/api/caja)
  private apiUrl = `${environment.apiUrl}/caja`;

  // Conexión real: Espera un array de objetos desde tu endpoint de Spring Boot
  obtenerCierreDiario(fecha: string): Observable<any[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<any[]>(`${this.apiUrl}/cierre`, { params });
  }
}