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
  userForm: FormGroup = this.fb.group({
    nombre: ['', Validators.required],
    username: ['', Validators.required],
    password: ['', Validators.required],
    rol: ['RECEPCIONISTA', Validators.required]
  });

  ngOnInit() { this.cargar(); }

  cargar() {
    this.svc.listarUsuarios().subscribe(data => this.usuarios = data);
  }

  guardar() {
    if (this.userForm.invalid) return;
    this.svc.crearUsuario(this.userForm.value).subscribe(() => {
      this.mostrarModal = false;
      this.cargar();
    });
  }
}