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

  ngOnInit() {
    this.cargarBandejaEntrada();
  }

  cargarBandejaEntrada() {
    // Petición REAL al backend (Spring Boot) para traer los tubos pendientes
    this.resultadosService.obtenerParametrosPendientes().subscribe({
      next: (data) => {
        this.tubosPendientes = data;
      },
      error: (err) => {
        console.error('Error al cargar la bandeja de entrada:', err);
        this.tubosPendientes = [];
      }
    });
  }

  seleccionarTubo(tubo: any) {
    this.tuboSeleccionado = tubo;
    
    // Petición REAL al backend para traer los parámetros de ESE examen
    // Asumimos que el tubo tiene la propiedad idDetalleOrden enviada desde Spring Boot
    const idDetalle = tubo.idDetalleOrden || tubo.idMuestra; 
    
    this.resultadosService.obtenerResultadosDeExamen(idDetalle).subscribe({
      next: (parametros) => {
        this.parametrosClinicos = parametros;
      },
      error: (err) => {
        console.error('Error al cargar los parámetros clínicos:', err);
        this.parametrosClinicos = [];
      }
    });
  }

  aprobarResultados() {
    // 1. Validar que no haya campos vacíos
    const faltanDatos = this.parametrosClinicos.some(p => p.valorObtenido === null || p.valorObtenido === undefined);
    if (faltanDatos) {
      alert("Por favor, ingrese todos los valores antes de aprobar el examen.");
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
            // Quitamos el tubo de la bandeja porque ya fue procesado
            this.tubosPendientes = this.tubosPendientes.filter(t => t.idMuestra !== this.tuboSeleccionado.idMuestra);
            
            // Limpiamos la pantalla derecha
            this.tuboSeleccionado = null;
            this.parametrosClinicos = [];
            this.procesando = false;
            
            alert("¡Resultados aprobados exitosamente! El PDF ha sido generado y el paciente ha sido notificado.");
          },
          error: (err) => {
            console.error("Error al aprobar la orden", err);
            alert("Los valores se guardaron, pero hubo un error al generar el PDF.");
            this.procesando = false;
          }
        });
      },
      error: (err) => {
        console.error("Error al guardar valores individuales", err);
        alert("Ocurrió un error al guardar los resultados en la base de datos.");
        this.procesando = false;
      }
    });
  }
}