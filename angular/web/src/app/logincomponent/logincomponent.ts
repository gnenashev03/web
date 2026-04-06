import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/authservice';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './logincomponent.html',
  styleUrls: ['./logincomponent.css']
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin() {
    this.errorMessage = '';
    
    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.router.navigate(['/tasks']);
      },
      error: (err) => {
        if (err.status === 401) {
          this.errorMessage = 'Неверный логин или пароль';
        } else {
          this.errorMessage = 'Ошибка сервера';
        }
      }
    });
  }

  getStatusColor(status: string): string {
    const colors: Record<string, string> = {
      'OPEN': 'orange',
      'IN_PROGRESS': 'blue',
      'DONE': 'green',
      'CLOSED': 'gray'
    };
    return colors[status] || 'gray';
  }
}