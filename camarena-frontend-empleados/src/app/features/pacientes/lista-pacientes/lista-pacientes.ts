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
  // Inyecciones
  private pacienteService = inject(PacienteService);
  private fb = inject(FormBuilder);

  // Estados de la tabla y datos
  pacientes: any[] = [];
  cargando = true;

  // Estados del modal
  mostrarModal = false;
  guardando = false;
  pacienteForm: FormGroup;

  constructor() {
    // CORRECCIÓN: Agregamos fechaNacimiento y sexo (Requeridos por Spring Boot)
    this.pacienteForm = this.fb.group({
      dni: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(8)]],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      fechaNacimiento: ['', Validators.required], // NUEVO
      sexo: ['', Validators.required],            // NUEVO
      telefono: ['']
    });
  }

  ngOnInit() {
    this.cargarPacientes();
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
      }
    });
  }

  abrirModalNuevo() {
    this.pacienteForm.reset();
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  guardarPaciente() {
    if (this.pacienteForm.invalid) return;

    this.guardando = true;
    const nuevoPaciente = this.pacienteForm.value;

    this.pacienteService.crearPaciente(nuevoPaciente).subscribe({
      next: (pacienteGuardado) => {
        this.guardando = false;
        this.cerrarModal();
        this.cargarPacientes(); // Recarga la tabla para ver el nuevo paciente
      },
      error: (err) => {
        console.error('Error al guardar', err);
        this.guardando = false;
        alert('Ocurrió un error al intentar guardar el paciente. Revisa la consola.');
      }
    });
  }
}