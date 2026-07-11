import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html'
})
export class SidebarComponent implements OnInit {
  private router = inject(Router);

  correoActual: string = 'Usuario';
  nombreCorto: string = 'Usuario'; // Añadido para la UX
  rolActual: string = '';

  ngOnInit() {
    this.decodificarToken();
    this.rolActual = localStorage.getItem('rol_camarena') || ''; 
  }

  decodificarToken() {
    const token = localStorage.getItem('jwt_camarena');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.correoActual = payload.sub || payload.email || 'Usuario';
        
        // UX: Extraer solo la parte antes del @ para un saludo más amigable
        this.nombreCorto = this.correoActual.split('@')[0];
      } catch (e) {
        console.error('Error al decodificar token', e);
      }
    }
  }

  // Reglas de Permisos
  get isAdmin(): boolean { return this.rolActual.includes('ROLE_ADMIN'); }
  get isRecepcion(): boolean { return this.rolActual.includes('ROLE_RECEPCION') || this.isAdmin; }
  get isBiologo(): boolean { return this.rolActual.includes('ROLE_BIOLOGO') || this.isAdmin; }

  // Nombre amigable del Rol para la Interfaz
  get nombreRolMostrar(): string {
    if (this.isAdmin) return 'Administrador';
    if (this.rolActual.includes('ROLE_BIOLOGO')) return 'Biólogo/Médico';
    if (this.rolActual.includes('ROLE_RECEPCION')) return 'Recepción';
    return 'Staff';
  }

  cerrarSesion() {
    localStorage.removeItem('jwt_camarena'); 
    localStorage.removeItem('rol_camarena'); 
    this.router.navigate(['/login']);        
  }
}