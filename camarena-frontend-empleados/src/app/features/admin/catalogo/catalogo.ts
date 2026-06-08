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

  constructor() {
    this.examenForm = this.fb.group({
      codigo: ['', Validators.required],
      descripcion: ['', Validators.required],
      tipoTuboDefecto: ['', Validators.required],
      precioBase: [null, [Validators.required, Validators.min(0.1)]]
    });

    this.parametroForm = this.fb.group({
      nombre: ['', Validators.required],
      unidad: ['']
    });
  }

  ngOnInit() {
    this.cargarCatalogo();
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
        this.cargarCatalogo(); // Recargamos para ver el nuevo examen
      },
      error: (err) => {
        console.error('Error al crear', err);
        alert('Ocurrió un error al guardar. Verifique que el código no esté duplicado.');
        this.guardando = false;
      }
    });
  }

  // Lógica para Parámetros
  abrirModalParametro(examen: any) {
    this.examenSeleccionado = examen;
    this.parametroForm.reset();
    this.mostrarModalParametro = true;
  }

  guardarParametro() {
    if (this.parametroForm.invalid) return;

    this.guardando = true;
    this.catalogoService.agregarParametro(this.examenSeleccionado.idExamen, this.parametroForm.value).subscribe({
      next: () => {
        alert('Parámetro agregado exitosamente a la base de datos.');
        this.mostrarModalParametro = false;
        this.guardando = false;
      },
      error: (err) => {
        alert('Error al agregar el parámetro.');
        this.guardando = false;
      }
    });
  }
}