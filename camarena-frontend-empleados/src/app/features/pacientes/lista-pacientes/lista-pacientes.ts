import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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

  // Estados principales de la tabla
  pacientes: any[] = [];
  cargando = true;

  // Estados del modal y flujos de acción
  mostrarModal = false;
  guardando = false;
  cargandoReniec = false;
  pacienteForm: FormGroup;

  // Sistema de notificaciones UI (Reemplaza a los alert() nativos)
  notificacion: { tipo: 'exito' | 'error', mensaje: string } | null = null;

  constructor() {
    this.pacienteForm = this.fb.group({
      dni: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(8), Validators.pattern('^[0-9]*$')]],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      fechaNacimiento: ['', Validators.required],
      sexo: ['', Validators.required],
      telefono: ['']
    });
  }

  ngOnInit() {
    this.cargarPacientes();
  }

  mostrarNotificacion(tipo: 'exito' | 'error', mensaje: string) {
    this.notificacion = { tipo, mensaje };
    // Ocultar automáticamente después de 4 segundos
    setTimeout(() => {
      this.notificacion = null;
    }, 4000);
  }

  cargarPacientes() {
    this.cargando = true;
    this.pacienteService.listarPacientes().subscribe({
      next: (data) => {
        this.pacientes = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar pacientes', err);
        this.cargando = false;
        this.pacientes = [];
        this.mostrarNotificacion('error', 'No se pudo cargar la lista de pacientes. Verifique su conexión.');
      }
    });
  }

  abrirModalNuevo() {
    this.pacienteForm.reset({
      dni: '',
      nombres: '',
      apellidos: '',
      fechaNacimiento: '',
      sexo: '',
      telefono: ''
    });
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  buscarReniec() {
    const dniControl = this.pacienteForm.get('dni');
    const dni = dniControl?.value;

    if (!dni || dni.length !== 8) {
      this.mostrarNotificacion('error', 'Ingrese un número de DNI válido de 8 dígitos.');
      return;
    }

    this.cargandoReniec = true;

    this.pacienteService.consultarReniec(dni).subscribe({
      next: (data) => {
        this.cargandoReniec = false;
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
        this.cargandoReniec = false;
        console.error('Error al conectar con RENIEC:', err);
        this.mostrarNotificacion('error', 'No se encontró el DNI o el servicio no está disponible temporalmente.');
      }
    });
  }

  guardarPaciente() {
    if (this.pacienteForm.invalid) {
      this.mostrarNotificacion('error', 'Complete todos los campos obligatorios correctamente.');
      return;
    }

    this.guardando = true;
    const nuevoPaciente = this.pacienteForm.value;

    this.pacienteService.crearPaciente(nuevoPaciente).subscribe({
      next: (pacienteGuardado) => {
        this.guardando = false;
        this.cerrarModal();
        this.mostrarNotificacion('exito', 'Paciente registrado exitosamente.');
        this.cargarPacientes(); 
      },
      error: (err) => {
        console.error('Error al guardar el paciente', err);
        this.guardando = false;
        this.mostrarNotificacion('error', 'Ocurrió un error al intentar guardar el paciente.');
      }
    });
  }
}