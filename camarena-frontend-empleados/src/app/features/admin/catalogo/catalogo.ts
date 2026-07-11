import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { CatalogoService } from '../../../core/services/catalogo';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule, DialogModule],
  templateUrl: './catalogo.html'
})
export class CatalogoComponent implements OnInit {
  private catalogoService = inject(CatalogoService);
  private fb = inject(FormBuilder);

  examenes: any[] = [];
  cargando = true;
  
  // Modal Examen
  mostrarModalExamen = false;
  guardando = false;
  examenForm: FormGroup;

  // Modal Parámetro
  mostrarModalParametro = false;
  examenSeleccionado: any = null;
  parametroForm: FormGroup;

  // Sistema de Notificaciones Flotante
  notificacion: any = null;

  constructor() {
    this.examenForm = this.fb.group({
      codigo: ['', Validators.required],
      descripcion: ['', Validators.required],
      tipoTuboDefecto: ['', Validators.required],
      precioBase: [null, [Validators.required, Validators.min(0.1)]]
    });

    this.parametroForm = this.fb.group({
      nombre: ['', Validators.required],
      unidad: [''],
      valorMin: [0, Validators.required],
      valorMax: [0, Validators.required],
      sexoAplica: ['A', Validators.required] 
    });
  }

  ngOnInit() {
    this.cargarCatalogo();
  }

  // Método unificado para alertas elegantes sin bloquear la UI
  mostrarNotificacion(mensaje: string, tipo: 'exito' | 'error' | 'advertencia' = 'exito') {
    this.notificacion = { mensaje, tipo };
    setTimeout(() => {
      this.notificacion = null;
    }, 4000);
  }

  cargarCatalogo() {
    this.cargando = true;
    this.catalogoService.listarExamenes().subscribe({
      next: (data) => {
        this.examenes = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar catálogo', err);
        this.mostrarNotificacion("Error al conectar con el catálogo de exámenes.", "error");
        this.examenes = [];
        this.cargando = false;
      }
    });
  }

  abrirModalNuevo() {
    this.examenForm.reset();
    this.mostrarModalExamen = true;
  }

  guardarExamen() {
    if (this.examenForm.invalid) return;

    this.guardando = true;
    const request = this.examenForm.value;

    this.catalogoService.crearExamen(request).subscribe({
      next: (res) => {
        this.mostrarModalExamen = false;
        this.guardando = false;
        this.cargarCatalogo(); 
        this.mostrarNotificacion(`Examen ${request.codigo} creado con éxito.`, "exito");
      },
      error: (err) => {
        console.error('Error al crear', err);
        this.mostrarNotificacion("Ocurrió un error. Verifique que el código no esté duplicado.", "error");
        this.guardando = false;
      }
    });
  }

  abrirModalParametro(examen: any) {
    this.examenSeleccionado = examen;
    this.parametroForm.reset({
      valorMin: 0,
      valorMax: 0,
      sexoAplica: 'A'
    });
    this.mostrarModalParametro = true;
  }

  guardarParametro() {
    if (this.parametroForm.invalid) return;

    this.guardando = true;
    this.catalogoService.agregarParametro(this.examenSeleccionado.idExamen, this.parametroForm.value).subscribe({
      next: () => {
        this.mostrarModalParametro = false;
        this.guardando = false;
        this.mostrarNotificacion("Parámetro biológico agregado correctamente.", "exito");
      },
      error: (err) => {
        console.error('Error al guardar parámetro', err);
        this.mostrarNotificacion("Error de servidor al guardar el parámetro.", "error");
        this.guardando = false;
      }
    });
  }
}