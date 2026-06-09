import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html', 
  styleUrl: './login.scss'     
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // Estados del componente
  loginForm: FormGroup;
  cargando = false;
  mensajeError = '';
  mostrarContrasena = false; // Permite alternar la visibilidad de la contraseña

  constructor() {
    // Inicialización del formulario con validaciones robustas
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(4)]],
      recordarme: [false]
    });
  }

  // Getter rápido para acceder a los controles del formulario en el HTML
  get f() {
    return this.loginForm.controls;
  }

  toggleMostrarContrasena() {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.cargando = true;
    this.mensajeError = '';

    const credenciales = this.loginForm.value;

    this.authService.login(credenciales).subscribe({
      next: (response) => {
        this.router.navigate(['/dashboard']); 
      },
      error: (err) => {
        this.cargando = false;
        if (err.status === 401 || err.status === 403) {
          this.mensajeError = 'Credenciales incorrectas. Verifique su correo o contraseña.';
        } else {
          this.mensajeError = 'Error de conexión con el servidor. Intente más tarde.';
        }
      }
    });
  }

  // Mocks de inicio de sesión con redes sociales
  loginConSocial(proveedor: string) {
    console.log(`Iniciando sesión de prueba con ${proveedor}`);
    // Aquí puedes enlazar tu lógica de OAuth2 si es necesario más adelante
  }
}