import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task';
import { environment } from '../environments/environment';
import { TaskStatus } from '../models/task_status';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private baseUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

getTasks(userId: number): Observable<Task[]> {
  return this.http.get<Task[]>(this.baseUrl, {
    params: { userId: userId.toString() }
  });
}

  getTask(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.baseUrl}/${id}`);
  }

  createTask(task: { title: string; status: TaskStatus | string }): Observable<Task> {
    return this.http.post<Task>(this.baseUrl, task);
  }

  updateTask(task: { id: number; title: string; status: TaskStatus | string }): Observable<Task> {
  return this.http.put<Task>(`${this.baseUrl}/${task.id}`, task);
}

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}