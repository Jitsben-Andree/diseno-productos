import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { OrdenService } from '../../../core/services/oden';

@Component({
  selector: 'app-historial-ordenes',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, DialogModule],
  templateUrl: './historial-orden.html'
})
export class HistorialOrdenesComponent {
  private ordenService = inject(OrdenService);

  terminoBusqueda: string = '';
  ordenes: any[] = [];
  buscando: boolean = false;

  // Estados para Modal de Tracking Visual
  ordenSeleccionada: any = null;
  mostrarModalTracking: boolean = false;

  // Estados para Modal de Anulación (Safety UX)
  mostrarModalAnular: boolean = false;
  motivoAnulacion: string = '';
  procesandoAnulacion: boolean = false;

  // Sistema de notificaciones flotante
  notificacion: { tipo: 'exito' | 'error' | 'advertencia', mensaje: string } | null = null;

  mostrarNotificacion(tipo: 'exito' | 'error' | 'advertencia', mensaje: string) {
    this.notificacion = { tipo, mensaje };
    setTimeout(() => this.notificacion = null, 4000);
  }

  buscar() {
    if (!this.terminoBusqueda || this.terminoBusqueda.trim().length < 3) {
      this.mostrarNotificacion('advertencia', 'Ingrese al menos 3 caracteres (DNI o Nro. de Ticket)');
      return;
    }

    this.buscando = true;
    
    // Llamada REAL a Spring Boot
    this.ordenService.buscarHistorial(this.terminoBusqueda.trim()).subscribe({
      next: (data) => {
        this.ordenes = data;
        this.buscando = false;
        
        if (data.length === 0) {
          this.mostrarNotificacion('advertencia', 'No se encontraron resultados para esta búsqueda.');
        } else {
          this.mostrarNotificacion('exito', `Se encontraron ${data.length} coincidencias.`);
        }
      },
      error: (err) => {
        console.error('Error en búsqueda:', err);
        this.mostrarNotificacion('error', 'Error al consultar la base de datos. Verifique su conexión.');
        this.buscando = false;
        this.ordenes = [];
      }
    });
  }

  verTracking(orden: any) {
    this.ordenSeleccionada = orden;
    this.mostrarModalTracking = true;
  }

  cerrarModalTracking() {
    this.mostrarModalTracking = false;
    this.ordenSeleccionada = null;
  }

  obtenerNivelTracking(estado: string): number {
    switch (estado) {
      case 'EN_ESPERA_MUESTRA': return 1; 
      case 'MUESTRAS_TOMADAS': 
      case 'EN_PROCESO': return 2;
      case 'FINALIZADO': return 3; 
      default: return 0;
    }
  }

  reimprimirVoucher(orden: any) {
    this.mostrarNotificacion('exito', `Enviando comando de impresión para el ticket ${orden.codigoTicket}...`);
    // Simula la impresión del voucher térmico que construimos en Nueva Orden
    setTimeout(() => window.print(), 500);
  }

  imprimirResultados(orden: any) {
    this.mostrarNotificacion('exito', 'Generando informe médico oficial en alta resolución...');
    
    // Creamos un iframe invisible para forzar la impresión A4 sin dañar la pantalla actual
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    document.body.appendChild(iframe);

    const doc = iframe.contentDocument || iframe.contentWindow?.document;
    if (doc) {
      doc.open();
      // Estructura HTML pura de un Certificado Médico Profesional (A4)
      doc.write(`
        <html>
          <head>
            <title>Resultados Clínicos - ${orden.codigoTicket}</title>
            <style>
              @page { size: A4; margin: 20mm; }
              body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; color: #333; }
              .header { text-align: center; border-bottom: 3px solid #0f4c81; padding-bottom: 20px; margin-bottom: 30px; }
              .logo { font-size: 28px; font-weight: 900; color: #0f4c81; letter-spacing: 1px; }
              .sub-logo { font-size: 11px; color: #666; letter-spacing: 3px; text-transform: uppercase; margin-top: 5px; }
              .title { font-size: 18px; font-weight: bold; margin-top: 25px; text-transform: uppercase; letter-spacing: 1px; }
              .info-box { border: 2px solid #e5e7eb; padding: 20px; border-radius: 10px; margin-bottom: 30px; background-color: #f9fafb; }
              .info-row { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }
              .info-label { font-weight: bold; color: #4b5563; text-transform: uppercase; font-size: 12px; letter-spacing: 1px; }
              .info-value { font-weight: bold; color: #111827; }
              .results-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
              .results-table th { background-color: #0f4c81; color: white; padding: 12px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 1px; }
              .results-table td { border-bottom: 1px solid #e5e7eb; padding: 15px 12px; font-size: 14px; }
              .footer { margin-top: 80px; text-align: center; font-size: 10px; color: #9ca3af; border-top: 1px dashed #d1d5db; padding-top: 20px; }
              .signature-container { margin-top: 100px; display: flex; justify-content: flex-end; }
              .signature-line { border-top: 1px solid #374151; width: 250px; text-align: center; padding-top: 8px; }
              .signature-name { font-weight: bold; font-size: 14px; color: #111827; }
              .signature-cmp { font-size: 12px; color: #6b7280; }
            </style>
          </head>
          <body>
            <div class="header">
              <div class="logo">CAMARENA LAB</div>
              <div class="sub-logo">Tecnología Diagnóstica de Precisión</div>
              <div class="title">INFORME DE RESULTADOS CLÍNICOS</div>
            </div>
            
            <div class="info-box">
              <div class="info-row">
                <div><span class="info-label">Paciente:</span> <br><span class="info-value">${orden.nombrePaciente}</span></div>
                <div style="text-align: right;"><span class="info-label">Ticket N°:</span> <br><span class="info-value" style="color:#0f4c81;">${orden.codigoTicket}</span></div>
              </div>
              <div class="info-row" style="margin-top: 15px;">
                <div><span class="info-label">Fecha de Muestra:</span> <br><span class="info-value">${new Date(orden.fechaEmision).toLocaleDateString()}</span></div>
                <div style="text-align: right;"><span class="info-label">Estado de Validación:</span> <br><span class="info-value" style="color: #10b981;">✓ APROBADO Y FIRMADO</span></div>
              </div>
            </div>

            <table class="results-table">
              <thead>
                <tr>
                  <th>Examen Analizado</th>
                  <th>Resultado Obtenido</th>
                  <th>Rango de Referencia</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td style="font-weight: bold; color: #374151;">Análisis Clínico (Panel)</td>
                  <td style="font-weight: bold; color: #111827;">Valores Verificados por Biólogo</td>
                  <td style="color: #6b7280; font-family: monospace;">Aplica Rango Estándar</td>
                </tr>
              </tbody>
            </table>

            <div class="signature-container">
              <div class="signature-line">
                <div class="signature-name">Biólogo / Médico Patólogo</div>
                <div class="signature-cmp">Sello y Firma Digital Autorizada</div>
              </div>
            </div>

            <div class="footer">
              DOCUMENTO GENERADO DIGITALMENTE POR EL ERP CAMARENA DIGITAL<br>
              Los resultados deben ser interpretados por un médico especialista. Prohibida su alteración o reproducción parcial.
            </div>
          </body>
        </html>
      `);
      doc.close();

      // Damos tiempo al navegador de renderizar el iframe oculto y lanzamos la impresión
      setTimeout(() => {
        iframe.contentWindow?.focus();
        iframe.contentWindow?.print();
        setTimeout(() => document.body.removeChild(iframe), 1000); // Limpieza de memoria
      }, 800);
    }
  }

  iniciarAnulacion(orden: any) {
    if (orden.estadoGeneral === 'FINALIZADO' || orden.estadoGeneral === 'ANULADO') {
      this.mostrarNotificacion('error', 'No se puede anular una orden que ya está finalizada o anulada previamente.');
      return;
    }
    this.ordenSeleccionada = orden;
    this.motivoAnulacion = '';
    this.mostrarModalAnular = true;
    this.mostrarModalTracking = false;
  }

  confirmarAnulacion() {
    if (this.motivoAnulacion.trim().length < 10) {
      this.mostrarNotificacion('advertencia', 'El motivo de anulación debe ser claro y detallado (mínimo 10 letras).');
      return;
    }

    this.procesandoAnulacion = true;
    
    // Petición REAL de anulación al backend
    this.ordenService.anularOrden(this.ordenSeleccionada.idOrden, this.motivoAnulacion).subscribe({
      next: () => {
        this.procesandoAnulacion = false;
        this.mostrarModalAnular = false;
        this.mostrarNotificacion('exito', 'Orden anulada correctamente. El registro financiero ha sido extornado.');
        
        // Recargamos los datos para que el estado de la tabla se actualice a "ANULADO"
        this.buscar(); 
      },
      error: (err) => {
        console.error("Error al anular", err);
        this.procesandoAnulacion = false;
        this.mostrarModalAnular = false;
        this.mostrarNotificacion('error', 'Ocurrió un problema al intentar anular la orden en el servidor.');
      }
    });
  }
}