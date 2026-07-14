import { Routes } from '@angular/router';

export const routes: Routes = [
  // 1. Pantalla de Entrada (Fuera del Layout)
  { 
    path: 'login-dni', 
    loadComponent: () => import('./features/login-dni/login-dni').then(m => m.LoginDniComponent) 
  },
  
  // 2. Rutas Internas (Dentro del Layout con Barra Inferior)
  {
    path: '',
    loadComponent: () => import('./layout/app-layout/app-layout').then(m => m.AppLayout),
    children: [
      { 
        path: 'dashboard-paciente', 
        loadComponent: () => import('./features/dashboard-paciente/dashboard-paciente').then(m => m.DashboardPacienteComponent) 
      },
      // PRÓXIMOS PASOS (Punto 4)
      // { 
      //   path: 'historial-clinico', 
      //   loadComponent: () => import('./features/historial-clinico/historial-clinico.component').then(m => m.HistorialClinicoComponent) 
      // },
      // { 
      //   path: 'grupo-familiar', 
      //   loadComponent: () => import('./features/grupo-familiar/grupo-familiar.component').then(m => m.GrupoFamiliarComponent) 
      // },
      { path: '', redirectTo: 'dashboard-paciente', pathMatch: 'full' }
    ]
  },

  // 3. El Visor de PDF ocupa toda la pantalla (Suele ir fuera del Layout del menú inferior por comodidad de lectura)
  { 
    path: 'visor-pdf', 
    loadComponent: () => import('./features/visor-pdf/visor-pdf').then(m => m.VisorPdf) 
  },

  // 4. Ruta comodín por si escriben una URL que no existe
  { path: '**', redirectTo: 'login-dni' }
];