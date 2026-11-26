import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html'
})
export class AuthComponent implements OnInit {
  isRegister = false;
  name = '';
  email = '';
  password = '';
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isRegister = this.route.snapshot.data['mode'] === 'register';
  }

  submit(): void {
    this.errorMessage = '';
    this.isSubmitting = true;

    const request = this.isRegister
      ? this.authService.register(this.name.trim(), this.email.trim(), this.password)
      : this.authService.login(this.email.trim(), this.password);

    request.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigateByUrl('/');
      },
      error: error => {
        this.isSubmitting = false;
        this.errorMessage =
          error.error?.message || 'Could not authenticate. Check your details and try again.';
      }
    });
  }
}
