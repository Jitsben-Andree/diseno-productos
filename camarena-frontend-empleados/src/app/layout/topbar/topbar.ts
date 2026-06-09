import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.html'
})
export class TopbarComponent {

  private authService = inject(AuthService);
  private router = inject(Router);

  lluqsiy() {
    this.authService.cerrarSesion();
    this.router.navigate(['/login']);
  }
}