import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { TaskService } from '../services/taskservice';
import { Task } from '../models/task';
import { TaskItemComponent } from '../taskitemcomponent/taskitemcomponent';
import { AuthService } from '../services/authservice';
import { UserInfoComponent } from '../userinfocomponent/userinfocomponent';
@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterModule, TaskItemComponent, UserInfoComponent], 
  templateUrl: './tasklistcomponent.html',
  styleUrls: ['./tasklistcomponent.css']
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];

  constructor(
    private taskService: TaskService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks() {
    const userId = this.authService.getCurrentUserId();
    console.log(' Loading tasks for userId:', userId);

    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.taskService.getTasks(userId).subscribe({
      next: (data) => {
        console.log(' Tasks loaded:', data.length);
        this.tasks = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(' Error loading tasks', err);
        if (err.status === 401 || err.status === 403) {
          this.router.navigate(['/login']);
        }
      }
    });
  }

  onDeleteTask(id: number) {
  if (!confirm('Вы уверены, что хотите удалить эту задачу?')) {
    return;
  }

  this.taskService.deleteTask(id).subscribe({
    next: () => {
      this.tasks = this.tasks.filter(t => t.id !== id);
      console.log(' Task deleted:', id);
    },
    error: (err) => {
      console.error(' Error deleting task', err);
      if (err.status === 400) {
        alert('Нельзя удалить задачу: прошло менее 5 минут с момента создания');
      } else if (err.status === 403) {
        alert('Нет прав для удаления этой задачи (требуется роль ADMIN)');
      } else if (err.status === 404) {
        alert('Задача не найдена');
      } else {
        alert('Ошибка при удалении задачи');
      }
    }
  });
}
}