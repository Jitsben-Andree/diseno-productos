import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrdenService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/ordenes`;

  crearOrden(ordenRequest: { dniPaciente: string, idsExamenes: number[] }): Observable<any> {
    return this.http.post<any>(this.apiUrl, ordenRequest);
  }

  registrarPago(idOrden: string, pagoRequest: { montoTotal: number, metodoPago: string, nroComprobante?: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/${idOrden}/pagos`, pagoRequest, { responseType: 'text' });
  }

  
  // Busca por Ticket o DNI
  buscarHistorial(filtro: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/historial?buscar=${filtro}`);
  }

  // Anula una orden y exige un motivo para la auditoría
  anularOrden(idOrden: string, motivo: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${idOrden}/anular`, { motivo }, { responseType: 'text' });
  }

  // Obtiene los datos crudos de una orden para reimprimir su voucher
  obtenerTicketParaReimpresion(idOrden: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${idOrden}/ticket`);
  }
}