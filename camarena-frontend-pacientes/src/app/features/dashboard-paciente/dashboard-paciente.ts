import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { DashboardPacienteService } from '../../core/service/dashboardpaciente';

@Component({
  selector: 'app-dashboard-paciente',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-paciente.html'
  
})
export class DashboardPacienteComponent implements OnInit {
  private dashboardService = inject(DashboardPacienteService);
  private router = inject(Router);

  cargando: boolean = true;
  datosOrden: any = null;

  ngOnInit() {
    // Fricción Cero: Leemos las llaves maestras que se guardaron en el Login
    const dni = localStorage.getItem('paciente_dni');
    const ticket = localStorage.getItem('paciente_ticket');

    if (!dni || !ticket) {
      // Si no hay sesión válida, lo regresamos al login
      this.router.navigate(['/login-dni']);
      return;
    }

    this.cargarDatos(dni, ticket);
  }

  cargarDatos(dni: string, ticket: string) {
    this.dashboardService.obtenerEstadoOrden(dni, ticket).subscribe({
      next: (data) => {
        this.datosOrden = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error("Error al obtener datos del portal", err);
        alert("Tu sesión expiró o el ticket no es válido.");
        this.router.navigate(['/login-dni']);
      }
    });
  }
}
