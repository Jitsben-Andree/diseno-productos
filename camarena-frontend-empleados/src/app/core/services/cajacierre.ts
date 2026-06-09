import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CajaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/ordenes`;

  // Trae las transacciones de una fecha específica
  obtenerCierreDiario(fecha: string): Observable<any[]> {
    // Aquí iría tu llamada HTTP real: return this.http.get<any[]>(`${this.apiUrl}/cierre?fecha=${fecha}`);
    
    // SIMULACIÓN PARA EL FRONTEND (Para que veas cómo funciona el Excel de inmediato)
    return of([
      { ticket: 'ORD-2026-001', paciente: 'Ricardo Mendoza', dni: '09876543', fecha: `${fecha} 07:15 AM`, metodoPago: 'EFECTIVO', monto: 45.00, cajero: 'Ana Recepción' },
      { ticket: 'ORD-2026-002', paciente: 'Valeria Castro', dni: '76543210', fecha: `${fecha} 08:30 AM`, metodoPago: 'TARJETA', monto: 120.00, cajero: 'Ana Recepción' },
      { ticket: 'ORD-2026-003', paciente: 'Luis Torres', dni: '45678901', fecha: `${fecha} 09:45 AM`, metodoPago: 'YAPE', monto: 35.50, cajero: 'Ana Recepción' },
      { ticket: 'ORD-2026-004', paciente: 'Carmen Ruiz', dni: '12345678', fecha: `${fecha} 10:20 AM`, metodoPago: 'EFECTIVO', monto: 80.00, cajero: 'Ana Recepción' }
    ]);
  }
}