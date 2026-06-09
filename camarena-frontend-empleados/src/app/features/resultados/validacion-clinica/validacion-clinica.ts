import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResultadosService } from '../../../core/services/resultado';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-validacion-clinica',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './validacion-clinica.html'
})
export class ValidacionClinicaComponent implements OnInit {
  private resultadosService = inject(ResultadosService);

  // Estados de la vista
  tubosPendientes: any[] = [];
  tuboSeleccionado: any = null;
  parametrosClinicos: any[] = [];
  procesando = false;

  // Sistema de Notificaciones Flotante
  notificacion: any = null;

  ngOnInit() {
    this.cargarBandejaEntrada();
  }

  mostrarNotificacion(mensaje: string, tipo: 'exito' | 'error' | 'advertencia' = 'exito') {
    this.notificacion = { mensaje, tipo };
    setTimeout(() => {
      this.notificacion = null;
    }, 4500); // Se esconde tras 4.5 segundos
  }

  cargarBandejaEntrada() {
    // Petición al backend (Spring Boot) para traer los tubos pendientes
    this.resultadosService.obtenerParametrosPendientes().subscribe({
      next: (data) => {
        this.tubosPendientes = data;
      },
      error: (err) => {
        console.error('Error al cargar la bandeja de entrada:', err);
        this.mostrarNotificacion("Error al conectar con la bandeja de muestras del servidor.", "error");
        this.tubosPendientes = [];
      }
    });
  }

  seleccionarTubo(tubo: any) {
    this.tuboSeleccionado = tubo;
    this.parametrosClinicos = []; // Limpiamos la tabla anterior mientras carga
    
    const idDetalle = tubo.idDetalleOrden || tubo.idMuestra; 
    
    this.resultadosService.obtenerResultadosDeExamen(idDetalle).subscribe({
      next: (parametros) => {
        this.parametrosClinicos = parametros;
      },
      error: (err) => {
        console.error('Error al cargar los parámetros clínicos:', err);
        this.mostrarNotificacion("No se pudieron extraer los parámetros de este examen.", "error");
      }
    });
  }

  aprobarResultados() {
    // 1. Validar que no haya campos vacíos
    const faltanDatos = this.parametrosClinicos.some(p => p.valorObtenido === null || p.valorObtenido === undefined || p.valorObtenido === '');
    if (faltanDatos) {
      this.mostrarNotificacion("Operación denegada: Ingrese todos los valores antes de firmar el examen.", "advertencia");
      return;
    }

    this.procesando = true;

    // 2. Preparar todas las peticiones HTTP (una por cada parámetro)
    const peticionesGuardado = this.parametrosClinicos.map(p => {
      const request = {
        idDetalleOrden: this.tuboSeleccionado.idDetalleOrden || this.tuboSeleccionado.idMuestra,
        idParametro: p.idParametro || p.id,
        valorObtenido: p.valorObtenido
      };
      return this.resultadosService.ingresarValor(request);
    });

    // 3. Ejecutar todas las peticiones en paralelo (forkJoin)
    forkJoin(peticionesGuardado).subscribe({
      next: () => {
        // 4. Si todo se guardó bien, aprobamos la orden y generamos el PDF
        const idOrden = this.tuboSeleccionado.idOrdenReal || this.tuboSeleccionado.idOrden;
        
        this.resultadosService.aprobarYGenerarPdf(idOrden).subscribe({
          next: (res) => {
            // Quitamos el tubo de la bandeja
            this.tubosPendientes = this.tubosPendientes.filter(t => t.idMuestra !== this.tuboSeleccionado.idMuestra);
            
            this.mostrarNotificacion("Resultados firmados exitosamente. PDF generado en la nube.", "exito");
            
            // Limpiamos la pantalla derecha
            this.tuboSeleccionado = null;
            this.parametrosClinicos = [];
            this.procesando = false;
          },
          error: (err) => {
            console.error("Error al aprobar la orden", err);
            this.mostrarNotificacion("Valores guardados, pero el motor PDF no respondió.", "advertencia");
            this.procesando = false;
          }
        });
      },
      error: (err) => {
        console.error("Error al guardar valores individuales", err);
        this.mostrarNotificacion("Error crítico al intentar guardar los resultados en la base de datos.", "error");
        this.procesando = false;
      }
    });
  }
}