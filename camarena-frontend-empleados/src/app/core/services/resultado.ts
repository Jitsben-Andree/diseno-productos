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

  listarTubosPendientes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pendientes`);
  }

  obtenerParametrosDeExamen(idDetalle: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/examen/${idDetalle}`);
  }

  ingresarValorAnalitico(request: { idDetalleOrden: string, idParametro: number, valorObtenido: number }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/ingresar`, request);
  }

  
  aprobarYGenerarPdf(idOrden: string): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/aprobar/${idOrden}`, {}, { responseType: 'blob' });
  }
}