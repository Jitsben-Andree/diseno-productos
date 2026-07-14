import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // Proveemos las peticiones HTTP y le inyectamos nuestro Interceptor de Seguridad
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};