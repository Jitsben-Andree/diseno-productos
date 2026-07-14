import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { environment } from "../../environments/environment.prod";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DashboardPacienteService {
  private http = inject(HttpClient);
  // Apunta a nuestra ruta PÚBLICA (No requiere token JWT de empleados)
  private apiUrl = `${environment.apiUrl}/public`;

  obtenerEstadoOrden(dni: string, ticket: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/dashboard/${dni}/${ticket}`);
  }
}