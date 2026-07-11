import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartModule } from 'primeng/chart'; 
import { DashboardService } from '../../../core/services/Dashboard';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-panel-principal',
  standalone: true,
  imports: [CommonModule, ChartModule], 
  templateUrl: './panel-principal.html'
})
export class PanelPrincipalComponent implements OnInit {
  
  // 1. Inyectamos el servicio que conecta con Spring Boot
  private dashboardService = inject(DashboardService);

  // 2. Variables de estado
  cargando = true;
  errorCarga = false;

  // 3. Variables para las tarjetas superiores (KPIs)
  stats = {
    ordenesHoy: 0,
    ingresosHoy: 0,
    muestrasPendientes: 0,
    resultadosListos: 0
  };

  // 4. Variables para el gráfico de PrimeNG
  datosGrafico: any;
  opcionesGrafico: any;

  ngOnInit() {
    this.configurarOpcionesGrafico();
    this.cargarEstadisticasReales();
  }

  // Llama al backend y espera la respuesta
  cargarEstadisticasReales() {
    this.cargando = true;
    this.errorCarga = false;

    this.dashboardService.obtenerEstadisticas().pipe(
      catchError(err => {
        console.error("Error al cargar el dashboard:", err);
        this.errorCarga = true;
        this.cargando = false;
        // Retornamos un objeto vacío para evitar fallos catastróficos en la UI
        return of(null);
      })
    ).subscribe(data => {
      if (data) {
        // Actualizamos los KPIs superiores
        this.stats.ordenesHoy = data.ordenesHoy;
        this.stats.ingresosHoy = data.ingresosHoy;
        this.stats.muestrasPendientes = data.muestrasPendientes;
        this.stats.resultadosListos = data.resultadosListos;

        // Actualizamos los datos del gráfico
        // NOTA: Para que PrimeNG detecte el cambio, debemos reasignar el objeto completo
        this.datosGrafico = {
          labels: data.etiquetasDias, // Ej: ['Lunes', 'Martes', ...] que vienen del Backend
          datasets: [
            {
              label: 'Ingresos Facturados (S/)',
              backgroundColor: '#10b981', // Verde Esmeralda (Éxito/Dinero)
              borderColor: '#10b981',
              hoverBackgroundColor: '#059669', // Verde más oscuro al pasar el mouse
              data: data.valoresIngresos, // Ej: [850, 1200, ...] calculados en PostgreSQL
              borderRadius: 6 
            }
          ]
        };
      }
      this.cargando = false;
    });
  }

  // Configuración de estilo, colores oscuros y formato de moneda
  configurarOpcionesGrafico() {
    this.opcionesGrafico = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: {
          labels: {
            color: '#6b7280', // text-gray-500
            font: { family: 'Inter, sans-serif', weight: 'bold' }
          }
        },
        tooltip: {
          callbacks: {
            // Formatear el tooltip para que muestre el símbolo de Soles
            label: function(context: any) {
              let label = context.dataset.label || '';
              if (label) {
                label += ': ';
              }
              if (context.parsed.y !== null) {
                label += new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(context.parsed.y);
              }
              return label;
            }
          }
        }
      },
      scales: {
        x: {
          ticks: { 
            color: '#6b7280',
            font: { family: 'Inter, sans-serif' }
          }, 
          grid: { display: false } 
        },
        y: {
          ticks: { 
            color: '#6b7280',
            font: { family: 'Inter, sans-serif' },
            // Formatear el eje Y para que muestre S/
            callback: function(value: any) {
               return 'S/ ' + value;
            }
          }, 
          grid: { 
            color: 'rgba(0, 0, 0, 0.05)', // Líneas divisorias muy tenues
            drawBorder: false
          } 
        }
      }
    };
  }
}