import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { AuthDniService } from '../auth-dni/auth-dni';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthDniService);
  const router = inject(Router);
  
  // Rescatamos el token que guardamos en el login
  const token = localStorage.getItem('jwt_camarena_paciente');

  // Clonamos la petición original para inyectarle el token
  let requestModificada = req;
  if (token) {
    requestModificada = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Dejamos que la petición viaje al servidor (next) y observamos la respuesta (pipe)
  return next(requestModificada).pipe(
    catchError((error: HttpErrorResponse) => {
      // Regla UX de Seguridad: Si el token expiró o es inválido (401 o 403)
      if (error.status === 401 || error.status === 403) {
        console.warn('Sesión expirada o acceso denegado. Redirigiendo...');
        authService.logout(); // Limpiamos la caché
        router.navigate(['/login-dni']); // Lo devolvemos al inicio sin fricción
      }
      
      // Propagamos el error para que otros componentes puedan mostrar alertas si lo desean
      return throwError(() => error);
    })
  );
};