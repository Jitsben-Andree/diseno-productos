import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

// PrimeNG
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button'; 
import { MessageService } from 'primeng/api';

import { CatalogoService } from '../../../core/services/catalogo';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ToastModule,
    DialogModule,
    InputTextModule,
    ButtonModule
  ],
  templateUrl: './catalogo.html',
  styleUrls: ['./catalogo.scss'],
  providers: [MessageService]
})
export class CatalogoComponent implements OnInit {
  
  // Exámenes
  examenes: any[] = [];
  cargandoExamenes = false;
  mostrarModalExamen = false; 
  guardandoExamen = false;
  examenForm: FormGroup;      
  
  // Lista de Parámetros
  mostrarModalListaParametros = false;
  parametrosDelExamen: any[] = [];
  cargandoParametros = false;
  examenSeleccionado: any = null;

  // Formulario de Parámetros
  mostrarModalParametro = false;
  guardandoParametroState = false;
  modoEdicionParametro = false;
  parametroEditandoId: number | null = null;
  parametroForm: FormGroup;

  constructor(
    private catalogoService: CatalogoService,
    private fb: FormBuilder,
    private messageService: MessageService
  ) {
    this.examenForm = this.fb.group({
      codigo: ['', [Validators.required, Validators.maxLength(10)]],
      descripcion: ['', Validators.required],
      tipoTuboDefecto: ['SUERO', Validators.required],
      precioBase: [0, [Validators.required, Validators.min(0)]]
    });

    this.parametroForm = this.fb.group({
      nombre: ['', Validators.required],
      unidad: [''],
      valorMin: [0, Validators.required],
      valorMax: [0, Validators.required],
      sexoAplica: ['A', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarExamenes();
  }

  cargarExamenes() {
    this.cargandoExamenes = true;
    this.catalogoService.listarExamenes().subscribe({
      next: (data) => {
        this.examenes = data;
        this.cargandoExamenes = false;
      },
      error: () => {
        this.mostrarNotificacion('Error', 'No se pudieron cargar los exámenes del catálogo', 'error');
        this.cargandoExamenes = false;
      }
    });
  }

  abrirModalNuevoExamen() {
    this.examenForm.reset({ tipoTuboDefecto: 'SUERO', precioBase: 0 });
    this.guardandoExamen = false;
    this.mostrarModalExamen = true;
  }

  guardarExamen() {
    if (this.examenForm.invalid) {
      this.mostrarNotificacion('Atención', 'Complete todos los campos del examen', 'warn');
      return;
    }

    this.guardandoExamen = true;
    this.catalogoService.crearExamen(this.examenForm.value).subscribe({
      next: () => {
        this.mostrarNotificacion('Éxito', 'Examen añadido al catálogo', 'success');
        this.mostrarModalExamen = false;
        this.cargarExamenes();
      },
      error: (err) => {
        const msg = err.error || 'No se pudo registrar el examen.';
        this.mostrarNotificacion('Error', msg, 'error');
        this.guardandoExamen = false;
      }
    });
  }

  verParametros(examen: any) {
    this.examenSeleccionado = examen;
    this.mostrarModalListaParametros = true;
    this.cargarParametros(examen.idExamen);
  }

  cargarParametros(idExamen: number) {
    this.cargandoParametros = true;
    this.catalogoService.obtenerParametrosDeExamen(idExamen).subscribe({
      next: (data) => {
        this.parametrosDelExamen = data;
        this.cargandoParametros = false;
      },
      error: () => {
        this.mostrarNotificacion('Error', 'No se pudieron cargar los parámetros', 'error');
        this.cargandoParametros = false;
      }
    });
  }

  abrirModalNuevoParametro(examen: any = null) {
    if (examen) this.examenSeleccionado = examen;
    this.modoEdicionParametro = false;
    this.parametroEditandoId = null;
    this.parametroForm.reset({ sexoAplica: 'A', valorMin: 0, valorMax: 0 });
    this.mostrarModalParametro = true;
  }

  editarParametro(parametro: any) {
    this.modoEdicionParametro = true;
    this.parametroEditandoId = parametro.idParametro;
    
    this.parametroForm.patchValue({
      nombre: parametro.nombre,
      unidad: parametro.unidad,
      valorMin: parametro.rangoMin, 
      valorMax: parametro.rangoMax,
      sexoAplica: parametro.sexoAplica || 'A'
    });
    
    this.mostrarModalParametro = true;
  }

  guardarParametro() {
    if (this.parametroForm.invalid) {
      this.mostrarNotificacion('Atención', 'Complete los campos obligatorios del parámetro', 'warn');
      return;
    }
    
    this.guardandoParametroState = true;

    if (this.modoEdicionParametro && this.parametroEditandoId) {
      this.catalogoService.actualizarParametro(this.parametroEditandoId, this.parametroForm.value).subscribe({
        next: () => {
          this.mostrarNotificacion('Éxito', 'Parámetro actualizado', 'success');
          this.cerrarModalFormParametro();
          this.cargarParametros(this.examenSeleccionado.idExamen);
        },
        error: () => { 
          this.mostrarNotificacion('Error', 'Error al actualizar el parámetro', 'error'); 
          this.guardandoParametroState = false;
        }
      });
    } else {
      this.catalogoService.agregarParametroAExamen(this.examenSeleccionado.idExamen, this.parametroForm.value).subscribe({
        next: () => {
          this.mostrarNotificacion('Éxito', 'Parámetro agregado', 'success');
          this.cerrarModalFormParametro();
          if (this.mostrarModalListaParametros) {
             this.cargarParametros(this.examenSeleccionado.idExamen);
          }
        },
        error: () => { 
          this.mostrarNotificacion('Error', 'Error al guardar el parámetro', 'error'); 
          this.guardandoParametroState = false;
        }
      });
    }
  }

  eliminarParametro(idParametro: number, nombre: string) {
    const confirmar = confirm(`¿Estás seguro de eliminar el parámetro "${nombre}"?`);
    
    if (confirmar) {
      this.catalogoService.eliminarParametro(idParametro).subscribe({
        next: () => {
          this.mostrarNotificacion('Eliminado', 'Parámetro eliminado', 'success');
          this.cargarParametros(this.examenSeleccionado.idExamen);
        },
        error: (err) => {
          const mensajeError = err.error || 'No se pudo eliminar, está en uso.';
          this.mostrarNotificacion('Acción Denegada', mensajeError, 'error');
        }
      });
    }
  }

  cerrarModalFormParametro() {
    this.mostrarModalParametro = false;
    this.guardandoParametroState = false;
  }

  mostrarNotificacion(titulo: string, mensaje: string, tipo: 'success' | 'error' | 'warn' | 'info') {
    this.messageService.add({ severity: tipo, summary: titulo, detail: mensaje, life: 4000 });
  }
}