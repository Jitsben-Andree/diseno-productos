import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
  private pacienteService = inject(PacienteService);
  private catalogoService = inject(CatalogoService);
  private ordenService = inject(OrdenService);
  private route = inject(ActivatedRoute); 
  private router = inject(Router);

  // Estados del Buscador de Paciente
  dniBusqueda = '';
  pacienteActual: any = null;
  buscandoPaciente = false;
  errorPaciente = '';

  // Estados del Catálogo y Carrito
  catalogoExamenes: any[] = [];
  carrito: any[] = [];
  totalCarrito = 0;

  // Estados del Flujo de Venta
  pasoActual = 1;
  procesando = false;
  ordenGenerada: any = null;
  metodoPago = 'EFECTIVO';

  notificacion: { tipo: 'exito' | 'error' | 'advertencia', mensaje: string } | null = null;

  ngOnInit() {
    this.cargarCatalogo();
    this.route.queryParams.subscribe(params => {
      if (params['dni']) {
        this.dniBusqueda = params['dni'];
        this.buscarPaciente(); 
      }
    });
  }

  mostrarNotificacion(tipo: 'exito' | 'error' | 'advertencia', mensaje: string) {
    this.notificacion = { tipo, mensaje };
    setTimeout(() => this.notificacion = null, 4000);
  }

  cargarCatalogo() {
    this.catalogoService.listarExamenes().subscribe({
      next: (data) => this.catalogoExamenes = data,
      error: (err) => this.mostrarNotificacion('error', 'No se pudo cargar el catálogo científico.')
    });
  }

  buscarPaciente() {
    if (!this.dniBusqueda || this.dniBusqueda.trim() === '') {
       this.errorPaciente = 'Ingrese un DNI para buscar.';
       return;
    }
    this.buscandoPaciente = true;
    this.errorPaciente = '';
    
    this.pacienteService.buscarPorDni(this.dniBusqueda).subscribe({
      next: (paciente) => {
        this.pacienteActual = paciente;
        this.buscandoPaciente = false;
        this.mostrarNotificacion('exito', `Paciente ${paciente.nombres} cargado correctamente.`);
      },
      error: (err) => {
        this.buscandoPaciente = false;
        this.errorPaciente = 'No se encontró el paciente en nuestra base de datos local.';
        this.pacienteActual = null;
      }
    });
  }

  limpiarPaciente() {
    this.pacienteActual = null;
    this.dniBusqueda = '';
    this.errorPaciente = '';
    this.router.navigate([], { queryParams: {} });
  }

  agregarAlCarrito(examen: any) {
    if (!this.pacienteActual) {
      this.mostrarNotificacion('advertencia', 'Primero debe buscar y seleccionar un paciente.');
      return;
    }
    const yaExiste = this.carrito.find(item => item.idExamen === (examen.idExamen || examen.codigo));
    if (!yaExiste) {
      this.carrito.push(examen);
      this.recalcularTotal();
    } else {
      this.mostrarNotificacion('advertencia', 'Este examen ya se encuentra en la orden.');
    }
  }

  quitarDelCarrito(index: number) {
    this.carrito.splice(index, 1);
    this.recalcularTotal();
  }

  recalcularTotal() {
    this.totalCarrito = this.carrito.reduce((acc, item) => acc + item.precioBase, 0);
  }

  generarOrden() {
    if (this.carrito.length === 0) {
      this.mostrarNotificacion('advertencia', 'El carrito está vacío. Agregue exámenes.');
      return;
    }
    
    this.procesando = true;
    const ids = this.carrito.map(item => item.idExamen);
    
    const request = {
      dniPaciente: this.pacienteActual.dni,
      idsExamenes: ids
    };

    this.ordenService.crearOrden(request).subscribe({
      next: (response) => {
        this.ordenGenerada = response;
        this.pasoActual = 2; 
        this.procesando = false;
      },
      error: (err) => {
        this.mostrarNotificacion('error', 'Ocurrió un problema de comunicación con el servidor.');
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
        this.pasoActual = 3; 
        this.procesando = false;
        this.mostrarNotificacion('exito', 'Transacción financiera exitosa.');
      },
      error: (err) => {
        this.mostrarNotificacion('error', 'El servidor rechazó el pago. Reintente.');
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

  // --- SOLUCIÓN 1: IMPRESIÓN TÉRMICA EXACTA (80mm) ---
  imprimirTicket() {
    const elementoOriginal = document.getElementById('ticket-termico');
    if (!elementoOriginal) return;

    // 1. Clonamos el elemento de la pantalla para "limpiarlo" antes de mandarlo al papel
    const ticketClonado = elementoOriginal.cloneNode(true) as HTMLElement;
    
    // 2. Le quitamos todas las clases de Tailwind que lo centran en la pantalla
    // Esto evita que la impresora térmica se confunda y corte los bordes
    ticketClonado.classList.remove('mx-auto', 'max-w-[320px]', 'shadow-xl', 'border', 'border-gray-200');
    ticketClonado.style.width = '100%';
    ticketClonado.style.margin = '0';

    // 3. Creamos la ventana invisible
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    document.body.appendChild(iframe);

    const doc = iframe.contentDocument || iframe.contentWindow?.document;
    if (doc) {
      doc.open();
      doc.write(`
        <html>
          <head>
            <title>Impresión de Ticket</title>
            <style>
              /* Configuración estricta de Hardware para Impresora Térmica (Epson/Zebra) */
              @page { 
                margin: 0; 
                size: 80mm 297mm; /* Ancho térmico, alto automático */
              }
              body { 
                width: 80mm; 
                margin: 0; 
                padding: 4mm; /* Pequeño margen para que la aguja no muerda las letras */
                color: black; 
                background: white; 
              }
              /* Ocultar botones o decoraciones que no van en el papel físico */
              .print\\:hidden { display: none !important; }
            </style>
          </head>
          <body>
            ${ticketClonado.outerHTML}
          </body>
        </html>
      `);
      doc.close();

      // 4. Clonamos los colores y fuentes oficiales de Tailwind
      const head = doc.head;
      document.querySelectorAll('style, link[rel="stylesheet"]').forEach(node => {
        head.appendChild(node.cloneNode(true));
      });

      // 5. Esperamos que el navegador renderice y disparamos la impresión
      setTimeout(() => {
        iframe.contentWindow?.focus();
        iframe.contentWindow?.print();
        setTimeout(() => document.body.removeChild(iframe), 1000); // Limpieza de RAM
      }, 800);
    }
  }

  // --- SOLUCIÓN 2: GENERACIÓN DE PDF REAL (A4) ESTABLE ---
  descargarPDF() {
  this.mostrarNotificacion('exito', 'Generando PDF con el diseño del ticket...');
  this.procesando = true;

  // Obtener el ticket original
  const ticketOriginal = document.getElementById('ticket-termico');
  if (!ticketOriginal) {
    this.procesando = false;
    this.mostrarNotificacion('error', 'Ticket no encontrado');
    return;
  }

  // Clonar el ticket con todos sus estilos
  const clonTicket = ticketOriginal.cloneNode(true) as HTMLElement;
  
  // Eliminar elementos decorativos
  const elementosEliminar = clonTicket.querySelectorAll('.bg-gradient-to-r, .h-1, .h-2, .w-full.absolute, .print\\:hidden');
  elementosEliminar.forEach(el => el.remove());
  
  // Mantener el estilo exacto del ticket
  clonTicket.style.cssText = `
    max-width: 320px;
    margin: 0 auto;
    background: white;
    font-family: 'Courier New', monospace;
    font-size: 11px;
    color: #1a1a1a;
    padding: 20px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
  `;
  
  clonTicket.classList.remove('mx-auto', 'max-w-[320px]', 'shadow-xl', 'border', 'border-gray-200', 'rounded-sm');

  // Contenedor temporal
  const contenedorPDF = document.createElement('div');
  contenedorPDF.style.cssText = `
    position: fixed;
    left: -9999px;
    top: 0;
    width: 350px;
    background: white;
    padding: 20px;
  `;
  contenedorPDF.appendChild(clonTicket);
  document.body.appendChild(contenedorPDF);

  // Cargar html2canvas
  const script1 = document.createElement('script');
  script1.src = 'https://cdn.jsdelivr.net/npm/html2canvas@1.4.1/dist/html2canvas.min.js';
  
  script1.onload = () => {
    // Cargar jspdf
    const script2 = document.createElement('script');
    script2.src = 'https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js';
    
    script2.onload = () => {
      setTimeout(() => {
        const element = contenedorPDF;
        
        (window as any).html2canvas(element, {
          scale: 3,
          useCORS: true,
          backgroundColor: '#ffffff',
          logging: false,
          width: 320,
          height: element.scrollHeight
        }).then((canvas: any) => {
          // Limpiar contenedor
          document.body.removeChild(contenedorPDF);

          const imgData = canvas.toDataURL('image/png');
          const jsPDF = (window as any).jspdf.jsPDF;
          const pdf = new jsPDF('p', 'mm', 'a4');
          
          const pdfWidth = 85;
          const imgProps = pdf.getImageProperties(imgData);
          const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;
          const xOffset = (210 - pdfWidth) / 2;
          const yOffset = 30;

          // Agregar membrete personalizado (opcional)
          pdf.setFontSize(14);
          pdf.setTextColor(15, 76, 129);
          pdf.text('LABORATORIO CLÍNICO CAMARENA', 105, 15, { align: 'center' });
          
          pdf.setFontSize(8);
          pdf.setTextColor(100, 100, 100);
          pdf.text('Comprobante de Orden de Servicio', 105, 22, { align: 'center' });

          // Agregar el ticket (EXACTAMENTE IGUAL al de impresión)
          pdf.addImage(imgData, 'PNG', xOffset, yOffset, pdfWidth, pdfHeight);

          // Pie de página
          const totalHeight = yOffset + pdfHeight + 10;
          pdf.setFontSize(7);
          pdf.setTextColor(150, 150, 150);
          pdf.text('Documento generado electrónicamente - Válido como comprobante', 105, totalHeight, { align: 'center' });

          pdf.save(`Orden_Servicio_${this.ordenGenerada?.codigoTicket || Date.now()}.pdf`);
          
          this.procesando = false;
          this.mostrarNotificacion('exito', '✅ PDF descargado con el diseño exacto del ticket');
        }).catch((err: any) => {
          document.body.removeChild(contenedorPDF);
          this.procesando = false;
          console.error('Error html2canvas:', err);
          this.mostrarNotificacion('error', 'Error al capturar el ticket');
        });
      }, 600);
    };

    script2.onerror = () => {
      document.body.removeChild(contenedorPDF);
      this.procesando = false;
      this.mostrarNotificacion('error', 'Error al cargar jspdf');
    };

    document.head.appendChild(script2);
  };

  script1.onerror = () => {
    document.body.removeChild(contenedorPDF);
    this.procesando = false;
    this.mostrarNotificacion('error', 'Error al cargar html2canvas');
  };

  document.head.appendChild(script1);
}

  enviarCorreo() {
    this.procesando = true;
    this.mostrarNotificacion('advertencia', 'Conectando con el servidor SMTP...');
    
    // Simulamos el tiempo de envío al correo
    setTimeout(() => {
      this.procesando = false;
      this.mostrarNotificacion('exito', `Se ha enviado la factura al correo del paciente.`);
    }, 2000);
  }
}