import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Vital para ngModel
import { PacienteService } from '../../../core/services/pacientes';
import { CatalogoService } from '../../../core/services/catalogo';
import { OrdenService } from '../../../core/services/oden';

@Component({
  selector: 'app-nueva-orden',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nueva-orden.html'
})
export class NuevaOrdenComponent implements OnInit {
  // Inyección de Servicios
  private pacienteService = inject(PacienteService);
  private catalogoService = inject(CatalogoService);
  private ordenService = inject(OrdenService);

  // Estados del Buscador de Paciente
  dniBusqueda = '';
  pacienteActual: any = null;
  buscandoPaciente = false;
  errorPaciente = '';

  // Estados del Catálogo y Carrito
  catalogoExamenes: any[] = [];
  carrito: any[] = [];
  totalCarrito = 0;

  // Estados del Flujo de Venta (1 = Armar, 2 = Pagar, 3 = Ticket)
  pasoActual = 1;
  procesando = false;
  ordenGenerada: any = null;
  metodoPago = 'EFECTIVO';

  ngOnInit() {
    this.cargarCatalogo();
  }

  // --- 1. CATÁLOGO ---
  cargarCatalogo() {
    this.catalogoService.listarExamenes().subscribe({
      next: (data) => this.catalogoExamenes = data,
      error: (err) => console.error("Error cargando catálogo", err)
    });
  }

  // --- 2. PACIENTE ---
  buscarPaciente() {
    if (!this.dniBusqueda || this.dniBusqueda.length < 8) return;
    
    this.buscandoPaciente = true;
    this.errorPaciente = '';
    this.pacienteActual = null;

    this.pacienteService.buscarPorDni(this.dniBusqueda).subscribe({
      next: (paciente) => {
        this.pacienteActual = paciente;
        this.buscandoPaciente = false;
      },
      error: (err) => {
        this.errorPaciente = 'Paciente no encontrado. Vaya a "Pacientes" para registrarlo.';
        this.buscandoPaciente = false;
      }
    });
  }

  limpiarPaciente() {
    this.pacienteActual = null;
    this.dniBusqueda = '';
  }

  // --- 3. CARRITO DE COMPRAS ---
  agregarAlCarrito(examen: any) {
    if (!this.pacienteActual) {
      alert("Primero busque y seleccione un paciente.");
      return;
    }
    // Evitamos duplicados básicos
    const yaExiste = this.carrito.find(item => item.idExamen === examen.idExamen);
    if (!yaExiste) {
      this.carrito.push(examen);
      this.recalcularTotal();
    }
  }

  quitarDelCarrito(index: number) {
    this.carrito.splice(index, 1);
    this.recalcularTotal();
  }

  recalcularTotal() {
    this.totalCarrito = this.carrito.reduce((acc, item) => acc + item.precioBase, 0);
  }

  // --- 4. FACTURACIÓN (SPRING BOOT) ---
  generarOrden() {
    this.procesando = true;
    
    // Extraemos solo los IDs de los exámenes que pide Spring Boot
    const ids = this.carrito.map(item => item.idExamen);
    
    const request = {
      dniPaciente: this.pacienteActual.dni,
      idsExamenes: ids
    };

    this.ordenService.crearOrden(request).subscribe({
      next: (response) => {
        this.ordenGenerada = response;
        this.pasoActual = 2; // Avanzamos al cobro
        this.procesando = false;
      },
      error: (err) => {
        console.error("Error al crear la orden", err);
        alert("Ocurrió un error al generar la orden.");
        this.procesando = false;
      }
    });
  }

  registrarPago() {
    this.procesando = true;

    const pagoReq = {
      montoTotal: this.totalCarrito,
      metodoPago: this.metodoPago
    };

    this.ordenService.registrarPago(this.ordenGenerada.idOrden, pagoReq).subscribe({
      next: (res) => {
        this.pasoActual = 3; // Mostrar Ticket final
        this.procesando = false;
      },
      error: (err) => {
        console.error("Error en pago", err);
        alert("No se pudo registrar el pago.");
        this.procesando = false;
      }
    });
  }

  reiniciarPuntoVenta() {
    this.limpiarPaciente();
    this.carrito = [];
    this.totalCarrito = 0;
    this.ordenGenerada = null;
    this.pasoActual = 1;
    this.metodoPago = 'EFECTIVO';
  }
}