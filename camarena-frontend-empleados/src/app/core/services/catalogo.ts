import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/catalogo`;

  // Trae todos los exámenes activos con sus precios
  listarExamenes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/examenes`);
  }

  // Crea un nuevo examen en PostgreSQL
  crearExamen(examen: { codigo: string, descripcion: string, tipoTuboDefecto: string, precioBase: number }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/examenes`, examen);
  }

  // Métodos para la inteligencia clínica (Parámetros y Rangos)
  agregarParametro(idExamen: number, parametro: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/examenes/${idExamen}/parametros`, parametro, { responseType: 'text' });
  }
}