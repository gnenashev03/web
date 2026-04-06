import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Task } from '../models/task';

@Component({
  selector: 'app-task-item',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './taskitemcomponent.html',
  styleUrls: ['./taskitemcomponent.css']
})
export class TaskItemComponent {
  @Input() task!: Task;
  @Output() deleteTask = new EventEmitter<number>();

  ngOnInit() {
    console.log(' TaskItemComponent loaded with task:', this.task);
  }

  onDelete() {
    this.deleteTask.emit(this.task.id);
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

getStatusColor(status: string): string {
  const colors: Record<string, string> = {
    'OPEN': 'orange',
    'DONE': 'green',
    'IN_PROGRESS': 'blue',
    'CLOSED': 'gray'
  };
  return colors[status] || 'gray';
}
}