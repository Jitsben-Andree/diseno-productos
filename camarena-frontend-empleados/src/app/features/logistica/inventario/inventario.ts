import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { InventarioService } from '../../../core/services/inventario';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule, DialogModule],
  templateUrl: './inventario.html'
})
export class InventarioComponent implements OnInit {
  private inventarioService = inject(InventarioService);
  private fb = inject(FormBuilder);

  insumos: any[] = [];
  cargando = true;
  
  mostrarModal = false;
  guardando = false;
  inventarioForm: FormGroup;

  constructor() {
    this.inventarioForm = this.fb.group({
      nombreInsumo: ['', Validators.required],
      codigoLote: [''],
      stockAgregar: [null, [Validators.required, Validators.min(1)]],
      stockMinimo: [null, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit() {
    this.cargarInventario();
  }

  cargarInventario() {
    this.cargando = true;
    this.inventarioService.listarInsumos().subscribe({
      next: (data) => {
        this.insumos = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar inventario', err);
        this.insumos = [];
        this.cargando = false;
      }
    });
  }

  get insumosConAlerta(): number {
    return this.insumos.filter(i => i.alertaStockBajo).length;
  }

  abrirModal() {
    this.inventarioForm.reset();
    this.mostrarModal = true;
  }

  guardarStock() {
    if (this.inventarioForm.invalid) return;

    this.guardando = true;
    const request = this.inventarioForm.value;

    this.inventarioService.agregarStock(request).subscribe({
      next: (res) => {
        this.mostrarModal = false;
        this.guardando = false;
        this.cargarInventario(); // Recargar tabla
      },
      error: (err) => {
        console.error('Error al guardar', err);
        alert('Ocurrió un error al intentar ingresar el stock al servidor.');
        this.guardando = false;
      }
    });
  }
}