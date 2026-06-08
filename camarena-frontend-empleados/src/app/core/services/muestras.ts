import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MuestraService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/muestras`;

  // Asumimos que tienes un endpoint para listar las órdenes que ya fueron pagadas
  obtenerOrdenesPendientes(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/ordenes/pendientes-topico`);
  }

  // Genera los códigos de barras físicos en la BD
  generarCodigos(idOrden: string): Observable<any[]> {
    return this.http.post<any[]>(`${this.apiUrl}/generar/${idOrden}`, {});
  }

  // Marca la extracción como finalizada (Esto descuenta el inventario en Spring Boot)
  marcarTomada(idMuestra: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${idMuestra}/tomada`, {});
  }
}