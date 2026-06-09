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

  // Sistema de Notificaciones Premium (Reemplaza los alerts ruidosos)
  notificacion: any = null;

  ngOnInit() {
    this.cargarCatalogo();
  }

  // Muestra una elegante notificación flotante que se desvanece sola
  mostrarNotificacion(mensaje: string, tipo: 'exito' | 'error' = 'exito') {
    this.notificacion = { mensaje, tipo };
    setTimeout(() => {
      this.notificacion = null;
    }, 4000);
  }

  // --- 1. CATÁLOGO ---
  cargarCatalogo() {
    this.catalogoService.listarExamenes().subscribe({
      next: (data) => this.catalogoExamenes = data,
      error: (err) => {
        console.error("Error cargando catálogo", err);
        this.mostrarNotificacion("Error al conectar con el catálogo de exámenes.", "error");
      }
    });
  }

  // --- 2. PACIENTE ---
  buscarPaciente() {
    if (!this.dniBusqueda || this.dniBusqueda.length < 8) {
      this.mostrarNotificacion("Por favor, ingrese un DNI válido de 8 dígitos.", "error");
      return;
    }
    
    this.buscandoPaciente = true;
    this.errorPaciente = '';
    this.pacienteActual = null;

    this.pacienteService.buscarPorDni(this.dniBusqueda).subscribe({
      next: (paciente) => {
        this.pacienteActual = paciente;
        this.buscandoPaciente = false;
        this.mostrarNotificacion(`Paciente ${paciente.nombres} identificado.`, "exito");
      },
      error: (err) => {
        this.errorPaciente = 'Paciente no encontrado en el sistema.';
        this.mostrarNotificacion("No se encontró ningún paciente con el DNI ingresado.", "error");
        this.buscandoPaciente = false;
      }
    });
  }

  limpiarPaciente() {
    this.pacienteActual = null;
    this.dniBusqueda = '';
    this.errorPaciente = '';
  }

  // --- 3. CARRITO DE COMPRAS ---
  agregarAlCarrito(examen: any) {
    if (!this.pacienteActual) {
      this.mostrarNotificacion("Primero busque y seleccione un paciente activo.", "error");
      return;
    }
    
    // Evitamos duplicados básicos
    const yaExiste = this.carrito.find(item => item.idExamen === examen.idExamen);
    if (!yaExiste) {
      this.carrito.push(examen);
      this.recalcularTotal();
      this.mostrarNotificacion(`${examen.descripcion} agregado a la orden.`, "exito");
    } else {
      this.mostrarNotificacion("Este examen ya se encuentra agregado en la orden.", "error");
    }
  }

  quitarDelCarrito(index: number) {
    const itemEliminado = this.carrito[index];
    this.carrito.splice(index, 1);
    this.recalcularTotal();
    this.mostrarNotificacion(`Se removió ${itemEliminado.descripcion} de la orden.`, "exito");
  }

  recalcularTotal() {
    this.totalCarrito = this.carrito.reduce((acc, item) => acc + item.precioBase, 0);
  }

  // --- 4. FACTURACIÓN (SPRING BOOT) ---
  generarOrden() {
    if (!this.pacienteActual || this.carrito.length === 0) return;
    
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
        this.mostrarNotificacion("Orden generada con éxito. Proceda al cobro.", "exito");
      },
      error: (err) => {
        console.error("Error al crear la orden", err);
        this.mostrarNotificacion("Ocurrió un error al intentar generar la orden.", "error");
        this.procesando = false;
      }
    });
  }

  registrarPago() {
    if (!this.ordenGenerada) return;
    
    this.procesando = true;

    const pagoReq = {
      montoTotal: this.totalCarrito,
      metodoPago: this.metodoPago
    };

    this.ordenService.registrarPago(this.ordenGenerada.idOrden, pagoReq).subscribe({
      next: (res) => {
        this.pasoActual = 3; // Mostrar Ticket final
        this.procesando = false;
        this.mostrarNotificacion("Pago registrado. Ticket de atención emitido.", "exito");
      },
      error: (err) => {
        console.error("Error en pago", err);
        this.mostrarNotificacion("No se pudo completar el registro del pago.", "error");
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
    this.mostrarNotificacion("Módulo de ventas restablecido.");
  }
}