import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';

import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  //Layout protegido
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/panel-principal/panel-principal').then(
            (m) => m.PanelPrincipalComponent,
          ),
      },
      {
        path: 'pacientes',
        loadComponent: () =>
          import('./features/pacientes/lista-pacientes/lista-pacientes').then(
            (m) => m.ListaPacientesComponent,
          ),
      },
      {
        path: 'transaccional',
        loadComponent: () =>
          import('./features/transaccional/nueva-orden/nueva-orden').then(
            (m) => m.NuevaOrdenComponent,
          ),
      },
      {
        path: 'topico',
        loadComponent: () =>
          import('./features/topico/kanban-muestras/kanban-muestras').then(
            (m) => m.KanbanMuestrasComponent,
          ),
      },
      {
        path: 'resultados',
        loadComponent: () =>
          import('./features/resultados/validacion-clinica/validacion-clinica').then(
            (m) => m.ValidacionClinicaComponent,
          ),
      },
      {
        path: 'inventario',
        loadComponent: () =>
          import('./features/logistica/inventario/inventario').then((m) => m.InventarioComponent),
      },
      {
        path: 'catalogo',
        loadComponent: () =>
          import('./features/admin/catalogo/catalogo').then((m) => m.CatalogoComponent),
      },
      {
        path: 'personal',
        loadComponent: () =>
          import('./features/admin/usuario/usuario').then((m) => m.UsuariosComponent),
      },
    ],
  },

  { path: '**', redirectTo: 'login' },
];
