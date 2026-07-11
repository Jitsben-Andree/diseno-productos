import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { SidebarService } from '../../core/services/sidebar';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.html'
})
export class TopbarComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  sidebarService = inject(SidebarService); // Inyectamos el servicio

  lluqsiy() {
    this.authService.cerrarSesion();
    this.router.navigate(['/login']);
  }
}