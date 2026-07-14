import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
  
  private apiUrl = `${environment.apiUrl}/catalogo`;

  constructor(private http: HttpClient) { }

  // ==========================================
  // EXÁMENES
  // ==========================================
  listarExamenes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/examenes`);
  }

  crearExamen(examen: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/examenes`, examen);
  }

  // ==========================================
  // PARÁMETROS
  // ==========================================
  obtenerParametrosDeExamen(idExamen: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/examenes/${idExamen}/parametros`);
  }

  agregarParametroAExamen(idExamen: number, parametro: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/examenes/${idExamen}/parametros`, parametro);
  }

  actualizarParametro(idParametro: number, parametro: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/parametros/${idParametro}`, parametro);
  }

  eliminarParametro(idParametro: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/parametros/${idParametro}`);
  }
}