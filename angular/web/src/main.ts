import { bootstrapApplication } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { TaskListComponent } from './app/tasklistcomponent/tasklistcomponent';
import { TaskEditComponent } from './app/taskeditcomponent/taskeditcomponent';
import { routes } from './app/app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { authInterceptor } from './app/Interceptors/auth.interceptor';
import { AppComponent } from './app/AppComponents';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])),
    importProvidersFrom(
      CommonModule,
      FormsModule,
      RouterModule.forRoot(routes)
    )
  ]
})
