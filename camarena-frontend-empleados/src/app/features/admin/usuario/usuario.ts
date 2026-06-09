import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { UsuarioService } from '../../../core/services/usuarios';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule, DialogModule],
  templateUrl: './usuario.html'
})
export class UsuariosComponent implements OnInit {
  private svc = inject(UsuarioService);
  private fb = inject(FormBuilder);
  
  usuarios: any[] = [];
  mostrarModal = false;
  guardando = false;

  // Sistema de Notificaciones Flotante
  notificacion: any = null;

  // Formulario adaptado a EmpleadoRequest de Java
  userForm: FormGroup = this.fb.group({
    dni: ['', [Validators.required, Validators.minLength(8)]],
    nombres: ['', Validators.required],
    apellidos: ['', Validators.required],
    cargo: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    nombreRol: ['ROLE_RECEPCION', Validators.required]
  });

  ngOnInit() { 
    this.cargar(); 
  }

  // Método unificado para alertas elegantes sin bloquear la UI
  mostrarNotificacion(mensaje: string, tipo: 'exito' | 'error' | 'advertencia' = 'exito') {
    this.notificacion = { mensaje, tipo };
    setTimeout(() => {
      this.notificacion = null;
    }, 4000);
  }

  cargar() {
    this.svc.listarUsuarios().subscribe({
      next: (data) => this.usuarios = data,
      error: (err) => {
        console.error("Error al cargar personal", err);
        this.mostrarNotificacion("Error al conectar con el servidor para cargar el personal.", "error");
      }
    });
  }

  guardar() {
    if (this.userForm.invalid) return;
    this.guardando = true;

    this.svc.crearUsuario(this.userForm.value).subscribe({
      next: () => {
        this.mostrarModal = false;
        this.guardando = false;
        const nombre = this.userForm.get('nombres')?.value;
        this.userForm.reset({ nombreRol: 'ROLE_RECEPCION' });
        this.cargar();
        this.mostrarNotificacion(`Colaborador ${nombre} registrado con éxito.`, "exito");
      },
      error: (err) => {
        console.error("Error al guardar usuario", err);
        this.mostrarNotificacion("Error al registrar. Verifique que el DNI o Email no estén duplicados.", "error");
        this.guardando = false;
      }
    });
  }
}