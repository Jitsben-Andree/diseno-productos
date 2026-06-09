import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CajaService } from '../../../core/services/cajacierre';

// Interfaz para tipado fuerte con los datos que envía Spring Boot
interface Transaccion {
  ticket: string;
  paciente: string;
  dni: string;
  fecha: string;
  metodoPago: string;
  monto: number;
  cajero: string;
}

@Component({
  selector: 'app-cierre-caja',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cierre-caja.html'
})
export class CierreCajaComponent implements OnInit {
  private cajaService = inject(CajaService);

  fechaFiltro: string = '';
  transacciones: Transaccion[] = [];
  cargando: boolean = false; // Estado de sincronización con BD
  
  // Contadores
  totalEfectivo: number = 0;
  totalDigital: number = 0;
  totalGeneral: number = 0;

  // Sistema de Notificaciones Flotante
  notificacion: any = null;

  ngOnInit() {
    // Establecer la fecha de hoy por defecto (Formato YYYY-MM-DD)
    const hoy = new Date();
    this.fechaFiltro = hoy.toISOString().split('T')[0];
    this.cargarDatos();
  }

  // Método unificado para alertas elegantes sin bloquear la UI
  mostrarNotificacion(mensaje: string, tipo: 'exito' | 'error' | 'advertencia' = 'exito') {
    this.notificacion = { mensaje, tipo };
    setTimeout(() => {
      this.notificacion = null;
    }, 4000);
  }

  cambiarFecha(nuevaFecha: string) {
    this.fechaFiltro = nuevaFecha;
    this.cargarDatos();
  }

  cargarDatos() {
    this.cargando = true; // Mostramos el spinner
    this.cajaService.obtenerCierreDiario(this.fechaFiltro).subscribe({
      next: (data: Transaccion[]) => {
        this.transacciones = data;
        this.calcularTotales();
        this.cargando = false; // Ocultamos el spinner
      },
      error: (err) => {
        console.error("Error al cargar cierre de caja", err);
        this.mostrarNotificacion("Error al recuperar el historial de transacciones del servidor.", "error");
        this.transacciones = [];
        this.calcularTotales();
        this.cargando = false;
      }
    });
  }

  calcularTotales() {
    this.totalEfectivo = 0;
    this.totalDigital = 0;
    this.totalGeneral = 0;

    this.transacciones.forEach(t => {
      this.totalGeneral += t.monto;
      if (t.metodoPago === 'EFECTIVO') {
        this.totalEfectivo += t.monto;
      } else {
        this.totalDigital += t.monto; // Tarjeta, Yape, Plin
      }
    });
  }

  // ALGORITMO NATIVO PARA EXPORTAR A EXCEL (CSV)
  exportarAExcel() {
    if (this.transacciones.length === 0) {
      this.mostrarNotificacion("Operación cancelada: No hay datos registrados en esta fecha para exportar.", "advertencia");
      return;
    }

    try {
      // 1. Crear las cabeceras del Excel
      let csvContent = "Fecha/Hora,Ticket,DNI,Paciente,Cajero,Metodo de Pago,Monto (S/)\n";

      // 2. Llenar las filas con los datos de las transacciones reales
      this.transacciones.forEach(t => {
        // Reemplazamos comas en los nombres por espacios para no romper el CSV
        const pacienteLimpio = t.paciente ? t.paciente.replace(/,/g, ' ') : 'Desconocido'; 
        csvContent += `"${t.fecha}","${t.ticket}","${t.dni}","${pacienteLimpio}","${t.cajero}","${t.metodoPago}",${t.monto}\n`;
      });

      // 3. Añadir la fila de totales al final
      csvContent += `\n,,,,,"TOTAL EFECTIVO",${this.totalEfectivo}\n`;
      csvContent += `,,,,,"TOTAL DIGITAL",${this.totalDigital}\n`;
      csvContent += `,,,,,"TOTAL GENERAL",${this.totalGeneral}\n`;

      // 4. Crear el archivo físico (Blob) y forzar la descarga
      const blob = new Blob(["\uFEFF" + csvContent], { type: 'text/csv;charset=utf-8;' }); // \uFEFF asegura soporte para tildes
      const url = URL.createObjectURL(blob);
      
      const link = document.createElement("a");
      link.setAttribute("href", url);
      link.setAttribute("download", `Cierre_Caja_${this.fechaFiltro}.csv`);
      
      // Simular un clic silencioso para descargar
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      this.mostrarNotificacion("Archivo Excel exportado y descargado exitosamente.", "exito");
    } catch (error) {
      this.mostrarNotificacion("Surgió un problema inesperado al generar el archivo Excel.", "error");
    }
  }
}