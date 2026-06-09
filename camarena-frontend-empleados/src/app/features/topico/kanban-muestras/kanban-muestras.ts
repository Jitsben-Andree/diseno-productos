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
  private router = inject(Router);

  // Estados del Tablero
  ordenesPendientes: any[] = [];
  pacientesCompletados: any[] = [];
  
  // Estado del Paciente Activo (Columna 2)
  pacienteEnAtencion: any = null;
  muestrasGeneradas: any[] = [];
  procesando = false;

  // Sistema de Notificaciones Flotantes Premium
  notificacion: any = null;

  ngOnInit() {
    this.cargarPendientes();
  }

  // Despliega una elegante notificación en pantalla
  mostrarNotificacion(mensaje: string, tipo: 'exito' | 'error' = 'exito') {
    this.notificacion = { mensaje, tipo };
    setTimeout(() => {
      this.notificacion = null;
    }, 4000);
  }

  cargarPendientes() {
    // Validación de seguridad frontal
    const token = localStorage.getItem('jwt_camarena');
    if (!token) {
      this.mostrarNotificacion("Su sesión ha expirado. Redirigiendo...", "error");
      setTimeout(() => this.router.navigate(['/login']), 2000);
      return;
    }

    // Llamada al backend para traer las órdenes pagadas
    this.muestraService.obtenerOrdenesPendientes().subscribe({
      next: (data) => {
        this.ordenesPendientes = data;
      },
      error: (err) => {
        console.error('Error al cargar órdenes pendientes', err);
        
        // Manejo específico del error 403 (Acceso Denegado / Forbidden)
        if (err.status === 403) {
          this.mostrarNotificacion("Acceso Denegado: Rol insuficiente o sesión expirada.", "error");
          setTimeout(() => this.router.navigate(['/login']), 2500);
        } else {
          this.mostrarNotificacion("Error al conectar con el servidor.", "error");
        }
        
        this.ordenesPendientes = [];
      }
    });
  }

  // Pasa al paciente de la columna 1 a la columna 2 y llama al backend
  atenderPaciente(orden: any) {
    this.procesando = true;
    this.pacienteEnAtencion = orden;
    
    // Llamada al backend para generar códigos de barras físicos en la BD
    this.muestraService.generarCodigos(orden.idOrden || orden.id).subscribe({
      next: (muestras) => {
        this.muestrasGeneradas = muestras;
        
        // Quitamos al paciente de la lista de pendientes visualmente
        this.ordenesPendientes = this.ordenesPendientes.filter(o => 
          (o.idOrden || o.id) !== (orden.idOrden || orden.id)
        );
        this.procesando = false;
        this.mostrarNotificacion(`Paciente ${orden.nombrePaciente} ingresado a box de extracción.`, "exito");
      },
      error: (err) => {
        console.error('Error al generar códigos de barras', err);
        this.mostrarNotificacion("Error al generar las etiquetas de códigos de barras.", "error");
        this.pacienteEnAtencion = null;
        this.procesando = false;
      }
    });
  }

  // Marca un tubo específico como extraído llamando al backend
  marcarTomada(muestra: any) {
    this.procesando = true;
    
    // Llamada al backend: Marca como tomada y descuenta del inventario
    this.muestraService.marcarTomada(muestra.idMuestra || muestra.id).subscribe({
      next: (muestraActualizada) => {
        muestra.estadoMuestra = 'TOMADA';
        this.procesando = false;
        this.mostrarNotificacion(`Tubo ${muestra.tipoTuboRequerido} de ${muestra.nombreExamen} tomado correctamente.`, "exito");
      },
      error: (err) => {
        console.error('Error al marcar muestra como tomada', err);
        this.mostrarNotificacion("Falta de insumos: Verifique stock de tubos en inventario.", "error");
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
    this.mostrarNotificacion(`Atención de ${this.pacienteEnAtencion.nombrePaciente} finalizada. Muestras listas para envío.`, "exito");
    this.pacienteEnAtencion = null; // Limpiamos la caja central
    this.muestrasGeneradas = [];
  }
}