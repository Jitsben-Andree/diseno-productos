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

  // Estados principales
  insumos: any[] = [];
  cargando = true;
  
  // Estados de flujos de acción
  mostrarModal = false;
  guardando = false;
  inventarioForm: FormGroup;

  // Sistema de Notificaciones Premium (Reemplaza los alert() obsoletos)
  notificacion: any = null;

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

  // Despliega un elegante mensaje en pantalla que se desvanece solo
  mostrarNotificacion(mensaje: string, tipo: 'exito' | 'error' | 'advertencia' = 'exito') {
    this.notificacion = { mensaje, tipo };
    setTimeout(() => {
      this.notificacion = null;
    }, 4000);
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
        this.mostrarNotificacion("Error al conectar con la base de datos de almacén.", "error");
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
        this.cargarInventario(); // Recargar la tabla automáticamente
        this.mostrarNotificacion(`Insumo "${request.nombreInsumo}" reabastecido con éxito.`, "exito");
      },
      error: (err) => {
        console.error('Error al guardar', err);
        this.mostrarNotificacion("No se pudo registrar el ingreso de stock al servidor.", "error");
        this.guardando = false;
      }
    });
  }
}