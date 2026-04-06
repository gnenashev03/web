import { HttpErrorResponse, HttpInterceptorFn, HttpStatusCode } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  const credentials = sessionStorage.getItem('auth'); // base64(login:pass)
  if (credentials) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Basic ${credentials}`)
    });
    return next(authReq
    
    )
    // .pipe(
    //   catchError(e => {
    //     if (e instanceof HttpErrorResponse && e.status === HttpStatusCode.Unauthorized) {
    //       router.navigate(['login']);
    //     }

    //     return throwError(() => e);
    //   })
    // );
  }
  return next(req);
};