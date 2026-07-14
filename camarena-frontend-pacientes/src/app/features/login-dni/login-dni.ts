import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthDniService } from '../../core/auth-dni/auth-dni';

@Component({
  selector: 'app-login-dni',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-dni.html'
})
export class LoginDniComponent {
  private authService = inject(AuthDniService);
  private router = inject(Router);

  dni: string = '';
  ticket: string = '';
  
  // Estados de UX
  cargando: boolean = false;
  mensajeError: string | null = null;

  ingresar() {
    this.mensajeError = null;

    // Validación UX Front-end
    if (!this.dni || this.dni.trim().length !== 8) {
      this.mensajeError = 'El DNI debe tener exactamente 8 dígitos.';
      return;
    }
    if (!this.ticket || this.ticket.trim().length < 5) {
      this.mensajeError = 'Ingrese un número de ticket válido.';
      return;
    }

    this.cargando = true;

    this.authService.login(this.dni.trim(), this.ticket.trim()).subscribe({
      next: () => {
        this.cargando = false;
        // Redirigir al layout principal que contiene el dashboard
        this.router.navigate(['/dashboard-paciente']);
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error de autenticación', err);
        // Mensajes amigables para el usuario
        if (err.status === 401 || err.status === 403) {
          this.mensajeError = 'DNI o Ticket incorrecto. Verifique su comprobante.';
        } else {
          this.mensajeError = 'Error de conexión. Intente nuevamente en unos segundos.';
        }
      }
    });
  }
}