import { Routes } from '@angular/router';
import { TaskListComponent } from './tasklistcomponent/tasklistcomponent';
import { TaskEditComponent } from './taskeditcomponent/taskeditcomponent';
import { LoginComponent } from './logincomponent/logincomponent';
export const routes: Routes = [
  { path: 'tasks', component: TaskListComponent },
  { path: 'tasks/:taskId', component: TaskEditComponent },
  { path: 'tasks/new', component: TaskEditComponent },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/tasks', pathMatch: 'full' },
  { path: '**', redirectTo: '/tasks' }
];