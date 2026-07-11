import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  // Apunta al nuevo controlador que creamos en Spring Boot
  private apiUrl = `${environment.apiUrl}/dashboard`; 

  obtenerEstadisticas(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}