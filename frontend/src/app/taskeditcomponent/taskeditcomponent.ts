import { Component, OnInit,ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TaskService } from '../services/taskservice';  
import { Task } from '../models/task';  
import { TaskStatus } from '../models/task_status';     

@Component({
  selector: 'app-task-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './taskeditcomponent.html',
  styleUrls: ['./taskeditcomponent.css']
})
export class TaskEditComponent implements OnInit {
  task: Task = {
    id: 0,
    title: '',
    status: TaskStatus.OPEN,
    createdAt: new Date().toISOString()
  };
  
  isNew = true;
  isLoading = false;
  errorMessage = '';
  
  statuses = Object.values(TaskStatus);

  constructor(
    private taskService: TaskService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('taskId');
    
    if (idParam && idParam !== 'new') {
      this.isNew = false;
      const id = Number(idParam);
      
      if (!isNaN(id)) {
        this.isLoading = true;
        this.taskService.getTask(id).subscribe({
          next: (data) => {
            this.task = data;
            this.isLoading = false;
            this.cdr.detectChanges();  
            console.log(' Task loaded for edit:', data);
          },
          error: (err) => {
            this.isLoading = false;
            this.cdr.detectChanges();  
            this.errorMessage = 'Ошибка загрузки задачи';
            console.error(' Error loading task', err);
            
            if (err.status === 404) {
              alert('Задача не найдена');
              this.router.navigate(['/tasks']);
            }
          }
        });
      } else {
        this.router.navigate(['/tasks']);
      }
    }
  }


    save() {
  if (!this.task.title || this.task.title.trim().length < 3) {
    alert('Название задачи должно быть не менее 3 символов');
    return;
  }

  this.isLoading = true;

  if (this.isNew) {
    this.taskService.createTask({
      title: this.task.title,
      status: this.task.status
    }).subscribe({
      next: () => {
        console.log(' Task created');
        this.router.navigate(['/tasks']);
      },
      error: (err) => {
        this.isLoading = false;
        this.handleError(err, 'создания');
      }
    });
  } else {
    this.taskService.updateTask({
      id: this.task.id,
      title: this.task.title,
      status: this.task.status
    } as Task).subscribe({  
      next: () => {
        console.log(' Task updated');
        this.router.navigate(['/tasks']);
      },
      error: (err) => {
        this.isLoading = false;  
        this.handleError(err, 'редактирования');
      }
    });
  }
}
private handleError(err: any, action: string) {
  console.error(`Error ${action} task`, err);
  
  if (err.status === 400) {
    alert(`Ошибка при ${action}: неверные данные`);
    this.router.navigate(['/tasks']);  
  } else if (err.status === 403) {
    alert(`Нет прав для ${action} задачи`);
    this.router.navigate(['/tasks']);  
  } else if (err.status === 409) {
    alert('Превышен лимит активных задач (максимум 10).\n\nУдалите или закройте некоторые задачи перед созданием новых.');
    this.router.navigate(['/tasks']);
  } else if (err.status === 404) {
    alert('Задача не найдена');
    this.router.navigate(['/tasks']);
  } else {
    alert(`Ошибка при ${action} задачи`);
    this.router.navigate(['/tasks']);  
  }
}
cancel() {
  this.router.navigate(['/tasks']);
}
  getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    'OPEN': 'Открыта',
    'DONE': 'Сделана',
    'IN_PROGRESS': 'В процессе',
    'CLOSED': 'Закрыта'
  };
  return labels[status] || status;
}
}