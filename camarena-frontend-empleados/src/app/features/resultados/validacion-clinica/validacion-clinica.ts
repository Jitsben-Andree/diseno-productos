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

  tubosPendientes: any[] = [];
  tuboSeleccionado: any = null;
  parametrosClinicos: any[] = [];
  procesando = false;
  notificacion: { tipo: 'exito' | 'error' | 'advertencia'; mensaje: string } | null = null;

  ngOnInit() {
    this.cargarBandejaEntrada();
  }

  mostrarNotificacion(tipo: 'exito' | 'error' | 'advertencia', mensaje: string) {
    this.notificacion = { tipo, mensaje };
    setTimeout(() => (this.notificacion = null), 4500);
  }

  cargarBandejaEntrada() {
    this.resultadosService.listarTubosPendientes().subscribe({
      next: (data) => (this.tubosPendientes = data),
      error: (err) => {
        console.error('Error en bandeja:', err);
        this.mostrarNotificacion('error', 'Error al conectar con la bandeja de muestras.');
      }
    });
  }

  seleccionarTubo(tubo: any) {
    this.tuboSeleccionado = tubo;
    this.parametrosClinicos = [];
    const idDetalle = tubo.idDetalleOrden || tubo.idMuestra;

    this.resultadosService.obtenerParametrosDeExamen(idDetalle).subscribe({
      next: (parametros) => (this.parametrosClinicos = parametros),
      error: (err) => this.mostrarNotificacion('error', 'No se pudieron extraer los parámetros.')
    });
  }

  esFueraDeRango(param: any): boolean {
    if (param.valorObtenido === null || param.valorObtenido === undefined || param.valorObtenido === '') return false;
    const valor = parseFloat(param.valorObtenido);
    return valor < parseFloat(param.rangoMin) || valor > parseFloat(param.rangoMax);
  }

  aprobarResultados() {
    const faltanDatos = this.parametrosClinicos.some(
      (p) => p.valorObtenido === null || p.valorObtenido === undefined || p.valorObtenido === ''
    );
    
    if (faltanDatos) {
      this.mostrarNotificacion('advertencia', 'Ingrese todos los valores antes de firmar.');
      return;
    }

    this.procesando = true;

    // Guardar los resultados ingresados
    const peticiones = this.parametrosClinicos.map((p) => {
      return this.resultadosService.ingresarValorAnalitico({
        idDetalleOrden: this.tuboSeleccionado.idDetalleOrden || this.tuboSeleccionado.idMuestra,
        idParametro: p.idParametro || p.id,
        valorObtenido: p.valorObtenido
      });
    });

    forkJoin(peticiones).subscribe({
      next: () => {
        const idOrden = this.tuboSeleccionado.idOrdenReal || this.tuboSeleccionado.idOrden;

        // 🔥 Ejecutar la aprobación y recibir el Blob del PDF
        this.resultadosService.aprobarYGenerarPdf(idOrden).subscribe({
          next: (pdfBlob: Blob) => {
            
            // 1. Crear URL temporal del archivo en la memoria del navegador
            const url = window.URL.createObjectURL(pdfBlob);
            
            // 2. Crear un enlace <a> oculto y hacer clic en él para descargar
            const a = document.createElement('a');
            a.href = url;
            a.download = `Resultados_${idOrden}.pdf`; // Nombre del archivo
            document.body.appendChild(a);
            a.click();
            
            // 3. Limpiar memoria y el DOM
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);

            // 4. Actualizar la vista (Bandeja limpia y notificación de éxito)
            this.tubosPendientes = this.tubosPendientes.filter(
              (t) => t.idMuestra !== this.tuboSeleccionado.idMuestra
            );
            this.mostrarNotificacion('exito', 'Resultados firmados. El PDF se ha descargado correctamente.');
            this.tuboSeleccionado = null;
            this.parametrosClinicos = [];
            this.procesando = false;
          },
          error: (err) => {
            console.error('Error al generar PDF:', err);
            this.mostrarNotificacion('error', 'Se guardaron los datos pero falló la generación del PDF.');
            this.procesando = false;
          }
        });
      },
      error: (err) => {
        console.error('Error al guardar valores', err);
        this.mostrarNotificacion('error', 'Error al guardar los resultados analíticos.');
        this.procesando = false;
      }
    });
  }
}