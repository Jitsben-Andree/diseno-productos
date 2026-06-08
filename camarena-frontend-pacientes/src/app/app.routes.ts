import { Routes } from '@angular/router';
import { LoginDni } from './features/login-dni/login-dni';
import { AppLayout } from './layout/app-layout/app-layout';
// import { authDniGuard } from './core/guards/auth-dni.guard';

export const routes: Routes = [
  { 
    path: 'acceso', 
    component: LoginDni
  },
  {
    path: '',
    component: AppLayout,
    // canActivate: [authDniGuard],
    children: [
      { path: '', redirectTo: 'inicio', pathMatch: 'full' },
      { 
        path: 'inicio', 
        loadComponent: () => import('./features/dashboard-paciente/dashboard-paciente').then(m => m.DashboardPaciente)
      },
      { 
        path: 'historial', 
        loadComponent: () => import('./features/historial-clinico/historial-clinico').then(m => m.HistorialClinico)
      },
      { 
        path: 'resultados/:id', 
        loadComponent: () => import('./features/visor-pdf/visor-pdf').then(m => m.VisorPdf)
      },
      { 
        path: 'agendar', 
        loadComponent: () => import('./features/agendamiento-online/agendamiento-online').then(m => m.AgendamientoOnline)
      },
      { 
        path: 'familia', 
        loadComponent: () => import('./features/grupo-familiar/grupo-familiar').then(m => m.GrupoFamiliar)
      }
    ]
  },
  { path: '**', redirectTo: 'acceso' }
];
