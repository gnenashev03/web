import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/authservice';

@Component({
  selector: 'app-user-info',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './userinfocomponent.html',
  styleUrls: ['./userinfocomponent.css']
})
export class UserInfoComponent {
  username: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.username = this.authService.getCurrentUsername();
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}