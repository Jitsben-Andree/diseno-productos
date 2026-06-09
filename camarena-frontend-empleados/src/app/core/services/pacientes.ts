import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReniecResponse {
  success: boolean;
  dni: string;
  nombres: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
}

@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  private http = inject(HttpClient);
  
  // Endpoint principal del backend Spring Boot
  private apiUrl = `${environment.apiUrl}/pacientes`; 

  // Obtiene todos los pacientes desde PostgreSQL
  listarPacientes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Registra un nuevo paciente en la base de datos
  crearPaciente(paciente: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, paciente);
  }

  // Método de búsqueda interna local de pacientes registrados
  buscarPorDni(dni: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/buscar/${dni}`);
  }

  // MÉTODO PARA RENIEC: Ahora fuertemente tipado con ReniecResponse
  consultarReniec(dni: string): Observable<ReniecResponse> {
    return this.http.get<ReniecResponse>(`${this.apiUrl}/reniec/${dni}`);
  }
}