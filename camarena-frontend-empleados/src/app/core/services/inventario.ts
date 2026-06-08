import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/inventario`;

  // Trae todo el stock desde PostgreSQL
  listarInsumos(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Ingresa una nueva caja de insumos al almacén
  agregarStock(insumo: { codigoLote: string, nombreInsumo: string, stockAgregar: number, stockMinimo: number }): Observable<any> {
    return this.http.post<any>(this.apiUrl, insumo);
  }
}