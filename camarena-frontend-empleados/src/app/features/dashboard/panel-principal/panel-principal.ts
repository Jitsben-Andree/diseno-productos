import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartModule } from 'primeng/chart'; 

@Component({
  selector: 'app-panel-principal',
  standalone: true,
  imports: [CommonModule, ChartModule], 
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
          // Actualizado al naranja corporativo del login
          backgroundColor: '#f05435', 
          borderColor: '#f05435',
          data: [850, 1200, 950, 1400, 1800, 2100, 600],
          // Bordes redondeados más pronunciados para encajar con el diseño
          borderRadius: 6 
        }
      ]
    };

    // 2. Opciones de diseño del gráfico adaptadas a fondo oscuro
    this.opcionesGrafico = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: {
          labels: {
            color: '#9ca3af' // Gris claro (text-gray-400) para fondo oscuro
          }
        }
      },
      scales: {
        x: {
          ticks: { color: '#9ca3af' }, // Textos del eje X en gris claro
          grid: { display: false } 
        },
        y: {
          ticks: { color: '#9ca3af' }, // Textos del eje Y en gris claro
          grid: { 
            color: 'rgba(255, 255, 255, 0.05)', // Líneas divisorias tenues translúcidas
            drawBorder: false
          } 
        }
      }
    };
  }
}