import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router'; // NUEVO: Para viajar a la otra pantalla
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { PacienteService } from '../../../core/services/pacientes';

@Component({
  selector: 'app-lista-pacientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule, DialogModule],
  templateUrl: './lista-pacientes.html'
})
export class ListaPacientesComponent implements OnInit {
  private pacienteService = inject(PacienteService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  pacientes: any[] = [];
  cargando = true;
  mostrarModal = false;
  guardando = false;
  buscandoReniec = false;
  
  // NUEVO: Variables para el control de edición
  modoEdicion = false;
  idPacienteEditando: string | null = null;

  pacienteForm: FormGroup;
  notificacion: { tipo: 'exito' | 'error', mensaje: string } | null = null;

  constructor() {
    this.pacienteForm = this.fb.group({
      dni: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(12)]],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      fechaNacimiento: ['', Validators.required],
      sexo: ['', Validators.required],
      telefono: ['']
    });
  }

  ngOnInit() { this.cargarPacientes(); }

  mostrarNotificacion(tipo: 'exito' | 'error', mensaje: string) {
    this.notificacion = { tipo, mensaje };
    setTimeout(() => { this.notificacion = null; }, 4000);
  }

  cargarPacientes() {
    this.cargando = true;
    this.pacienteService.listarPacientes().subscribe({
      next: (data) => { this.pacientes = data; this.cargando = false; },
      error: (err) => { 
        this.pacientes = []; this.cargando = false; 
        this.mostrarNotificacion('error', 'No se pudo cargar el directorio de pacientes.');
      }
    });
  }

  abrirModalNuevo() {
    this.modoEdicion = false;
    this.idPacienteEditando = null;
    this.pacienteForm.reset({ sexo: '' });
    // Habilitar DNI por si estaba deshabilitado de una edición anterior
    this.pacienteForm.get('dni')?.enable(); 
    this.mostrarModal = true;
  }

  abrirModalEditar(paciente: any) {
    this.modoEdicion = true;
    this.idPacienteEditando = paciente.idPaciente || paciente.oid_paciente;
    
    // Autocompletamos el formulario con los datos del paciente seleccionado
    this.pacienteForm.patchValue({
      dni: paciente.dni || paciente.odni,
      nombres: paciente.nombres || paciente.onombres,
      apellidos: paciente.apellidos || paciente.oapellidos,
      fechaNacimiento: paciente.fechaNacimiento || paciente.ofechaNacimiento,
      sexo: paciente.sexo || paciente.osexo,
      telefono: paciente.telefono || paciente.otelefono
    });

    // UX: Deshabilitamos el DNI para que no lo cambien por error al editar
    this.pacienteForm.get('dni')?.disable(); 
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  buscarReniec() {
    const dni = this.pacienteForm.get('dni')?.value;
    if (!dni || dni.length !== 8) {
      this.mostrarNotificacion('error', 'Ingrese un número de DNI válido de 8 dígitos.');
      return;
    }

    this.buscandoReniec = true;

    // Llamada REAL a tu PacienteService (Se conecta a Spring Boot)
    this.pacienteService.consultarReniec(dni).subscribe({
      next: (data) => {
        this.buscandoReniec = false;
        
        if (data && data.success) {
          const apellidosConcatenados = `${data.apellidoPaterno} ${data.apellidoMaterno}`.trim();
          this.pacienteForm.patchValue({
            nombres: data.nombres,
            apellidos: apellidosConcatenados
          });
          this.mostrarNotificacion('exito', 'Datos obtenidos de RENIEC correctamente.');
        } else if (data && data.nombres) {
          const apellidosConcatenados = `${data.apellidoPaterno || ''} ${data.apellidoMaterno || ''}`.trim();
          this.pacienteForm.patchValue({
            nombres: data.nombres,
            apellidos: apellidosConcatenados
          });
          this.mostrarNotificacion('exito', 'Datos obtenidos correctamente.');
        } else {
          this.mostrarNotificacion('error', 'No se pudo extraer información válida de este DNI.');
        }
      },
      error: (err) => {
        this.buscandoReniec = false;
        console.error('Error al conectar con RENIEC:', err);
        this.mostrarNotificacion('error', 'No se encontró el DNI o el servicio no está disponible temporalmente.');
      }
    });
  }

  guardarPaciente() {
    if (this.pacienteForm.invalid) return;
    this.guardando = true;
    
    // Obtenemos los valores. getRawValue() extrae incluso campos deshabilitados (como el DNI en edición)
    const datosPaciente = this.pacienteForm.getRawValue();

    if (this.modoEdicion) {
      // Simulación de actualización (Aquí llamarías a pacienteService.actualizarPaciente(id, datos))
      setTimeout(() => {
        this.guardando = false;
        this.cerrarModal();
        this.mostrarNotificacion('exito', 'Datos del paciente actualizados exitosamente.');
        this.cargarPacientes();
      }, 800);
    } else {
      // Creación normal
      this.pacienteService.crearPaciente(datosPaciente).subscribe({
        next: () => {
          this.guardando = false;
          this.cerrarModal();
          this.mostrarNotificacion('exito', 'Paciente registrado correctamente.');
          this.cargarPacientes();
        },
        error: () => {
          this.mostrarNotificacion('error', 'El DNI ya existe en el sistema.');
          this.guardando = false;
        }
      });
    }
  }

  irANuevaOrden(dni: string) {
    // Viajamos a la ruta 'transaccional' y le enviamos el DNI por la URL como parámetro
    // Esto se verá en el navegador así: /transaccional?dni=71234567
    this.router.navigate(['/transaccional'], { queryParams: { dni: dni } });
  }
}