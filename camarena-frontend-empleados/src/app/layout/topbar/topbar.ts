import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-topbar',
  imports: [CommonModule],
  templateUrl: './topbar.html',
  styleUrl: './topbar.scss',
})
export class TopbarComponent {


  private authService = inject(AuthService);
  private router = inject(Router);

  // Lluqsiy ruray (Método para cerrar sesión)
  lluqsiy() {
    this.authService.cerrarSesion();
    this.router.navigate(['/login']);
  }
}

