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

  constructor() {
    // Inicialización del formulario con validaciones
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  // Getter rápido para acceder a los controles del formulario en el HTML
  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    // Si el formulario es inválido, no hace nada
    if (this.loginForm.invalid) return;

    this.cargando = true;
    this.mensajeError = '';

    const credenciales = this.loginForm.value;

    this.authService.login(credenciales).subscribe({
      next: (response) => {
        // Redirigimos al usuario al dashboard principal
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
}