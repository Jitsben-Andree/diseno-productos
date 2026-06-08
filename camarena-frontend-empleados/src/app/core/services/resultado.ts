import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ResultadosService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/resultados`;

  // Guarda un valor individual (ej. Glucosa = 95)
  ingresarValor(request: { idDetalleOrden: string, idParametro: number, valorObtenido: number }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/ingresar`, request);
  }

  // El Biólogo firma y aprueba la orden completa para generar el PDF
  aprobarYGenerarPdf(idOrden: string): Observable<any> {
    // Retornamos texto porque el backend devuelve un String de éxito, no un JSON
    return this.http.post(`${this.apiUrl}/aprobar/${idOrden}`, {}, { responseType: 'text' });
  }

  // Obtiene los parámetros requeridos para un examen (Endpoint simulado para el Frontend)
  obtenerParametrosPendientes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pendientes`);
  }

  // NUEVO: Obtiene los resultados y parámetros de un examen en particular desde Spring Boot
  obtenerResultadosDeExamen(idDetalle: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/examen/${idDetalle}`);
  }
}