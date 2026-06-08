import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  private http = inject(HttpClient);
  // Apunta al endpoint de pacientes de tu Spring Boot
  private apiUrl = `${environment.apiUrl}/pacientes`; 

  // Obtiene la lista desde PostgreSQL
  listarPacientes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Envía un nuevo paciente a la base de datos
  crearPaciente(paciente: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, paciente);
  }

  // ESTE ES EL MÉTODO QUE FALTABA Y CAUSABA EL ERROR EN ROJO
  buscarPorDni(dni: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/buscar/${dni}`);
  }
}