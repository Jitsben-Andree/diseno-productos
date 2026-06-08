import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrdenService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/ordenes`;

  // Envia el DNI del paciente y los IDs de los exámenes
  crearOrden(ordenRequest: { dniPaciente: string, idsExamenes: number[] }): Observable<any> {
    return this.http.post<any>(this.apiUrl, ordenRequest);
  }

  // Registra el pago en efectivo o tarjeta
  registrarPago(idOrden: string, pagoRequest: { montoTotal: number, metodoPago: string, nroComprobante?: string }): Observable<any> {
    // El backend espera texto simple de respuesta ("Pago registrado..."), por eso usamos responseType: 'text'
    return this.http.post(`${this.apiUrl}/${idOrden}/pagos`, pagoRequest, { responseType: 'text' });
  }
}