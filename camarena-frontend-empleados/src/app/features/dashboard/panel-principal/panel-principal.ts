import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartModule } from 'primeng/chart'; // <-- Importamos el componente de gráficos

@Component({
  selector: 'app-panel-principal',
  standalone: true,
  imports: [CommonModule, ChartModule], // <-- Lo agregamos a los imports
  templateUrl: './panel-principal.html'
})
export class PanelPrincipalComponent implements OnInit {
  
  // Variables para el gráfico de PrimeNG
  datosGrafico: any;
  opcionesGrafico: any;

  ngOnInit() {
    this.configurarGrafico();
  }

  configurarGrafico() {
    // 1. Datos del gráfico (Eje X: Días, Eje Y: Montos en Soles)
    this.datosGrafico = {
      labels: ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'],
      datasets: [
        {
          label: 'Ingresos (S/)',
          backgroundColor: '#0f4c81', // Nuestro azul-clinico
          borderColor: '#0f4c81',
          data: [850, 1200, 950, 1400, 1800, 2100, 600],
          borderRadius: 4 // Bordes redondeados en las barras
        }
      ]
    };

    // 2. Opciones de diseño del gráfico
    this.opcionesGrafico = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: {
          labels: {
            color: '#4b5563' // Color del texto de la leyenda (gris)
          }
        }
      },
      scales: {
        x: {
          ticks: { color: '#6b7280' },
          grid: { display: false } // Ocultar líneas verticales de la cuadrícula
        },
        y: {
          ticks: { color: '#6b7280' },
          grid: { color: '#e5e7eb' } // Líneas horizontales gris muy claro
        }
      }
    };
  }
}