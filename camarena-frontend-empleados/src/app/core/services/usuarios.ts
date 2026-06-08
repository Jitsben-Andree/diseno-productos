import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/usuarios`;

  listarUsuarios() {
    return this.http.get<any[]>(this.apiUrl);
  }

  crearUsuario(usuario: any) {
    return this.http.post(this.apiUrl, usuario);
  }
}