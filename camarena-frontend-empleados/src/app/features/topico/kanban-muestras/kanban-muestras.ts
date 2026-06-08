import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MuestraService } from '../../../core/services/muestras';

@Component({
  selector: 'app-kanban-muestras',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './kanban-muestras.html'
})
export class KanbanMuestrasComponent implements OnInit {
  private muestraService = inject(MuestraService);
  private router = inject(Router); // <-- Inyectamos el router para manejar el error 403

  // Estados del Tablero
  ordenesPendientes: any[] = [];
  pacientesCompletados: any[] = [];
  
  // Estado del Paciente Activo (Columna 2)
  pacienteEnAtencion: any = null;
  muestrasGeneradas: any[] = [];
  procesando = false;

  ngOnInit() {
    this.cargarPendientes();
  }

  cargarPendientes() {
    // Validación de seguridad frontal
    const token = localStorage.getItem('jwt_camarena');
    if (!token) {
      alert("Su sesión ha expirado. Por favor, inicie sesión nuevamente.");
      this.router.navigate(['/login']);
      return;
    }

    // Llamada REAL al backend para traer las órdenes pagadas
    this.muestraService.obtenerOrdenesPendientes().subscribe({
      next: (data) => {
        this.ordenesPendientes = data;
      },
      error: (err) => {
        console.error('Error al cargar órdenes pendientes', err);
        
        // Manejo específico del error 403 (Acceso Denegado / Forbidden)
        if (err.status === 403) {
          alert('Acceso Denegado: No tienes el rol necesario o tu sesión expiró.');
          this.router.navigate(['/login']);
        }
        
        this.ordenesPendientes = [];
      }
    });
  }

  // Pasa al paciente de la columna 1 a la columna 2 y llama al backend
  atenderPaciente(orden: any) {
    this.procesando = true;
    this.pacienteEnAtencion = orden;
    
    // Llamada REAL al backend para generar códigos de barras físicos en la BD
    this.muestraService.generarCodigos(orden.idOrden || orden.id).subscribe({
      next: (muestras) => {
        this.muestrasGeneradas = muestras;
        
        // Quitamos al paciente de la lista de pendientes visualmente
        this.ordenesPendientes = this.ordenesPendientes.filter(o => 
          (o.idOrden || o.id) !== (orden.idOrden || orden.id)
        );
        this.procesando = false;
      },
      error: (err) => {
        console.error('Error al generar códigos de barras', err);
        alert('Hubo un error al conectar con el servidor para generar los códigos.');
        this.pacienteEnAtencion = null;
        this.procesando = false;
      }
    });
  }

  // Marca un tubo específico como extraído llamando al backend
  marcarTomada(muestra: any) {
    this.procesando = true;
    
    // Llamada REAL al backend: Marca como tomada y DESCUENTA el inventario
    this.muestraService.marcarTomada(muestra.idMuestra || muestra.id).subscribe({
      next: (muestraActualizada) => {
        // Actualizamos el estado visualmente
        muestra.estadoMuestra = 'TOMADA';
        this.procesando = false;
      },
      error: (err) => {
        console.error('Error al marcar muestra como tomada', err);
        alert('Error al procesar la muestra. Verifique si hay suficiente stock en el inventario.');
        this.procesando = false;
      }
    });
  }

  // Getter para saber si el botón de "Finalizar Paciente" debe activarse
  get todasMuestrasTomadas(): boolean {
    return this.muestrasGeneradas.length > 0 && 
           this.muestrasGeneradas.every(m => m.estadoMuestra === 'TOMADA');
  }

  // Mueve al paciente a la columna 3 y limpia el área central
  finalizarAtencion() {
    this.pacientesCompletados.unshift(this.pacienteEnAtencion); // Lo agregamos a completados
    this.pacienteEnAtencion = null; // Limpiamos la caja central
    this.muestrasGeneradas = [];
  }
}