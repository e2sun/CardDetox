import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent {
  mode: 'login' | 'register' = 'login';
  loading = false;
  error = '';

  loginData = { email: '', password: '' };
  registerData = { username: '', email: '', password: '' };

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  login(): void {
    this.loading = true;
    this.error = '';
    this.authService.login(this.loginData).subscribe({
      next: () => {
        this.cartService.mergeGuestCart().subscribe();
        const redirect = this.route.snapshot.queryParams['redirect'] || '/';
        this.router.navigate([redirect]);
      },
      error: (err) => {
        this.error = err.error || 'Invalid email or password';
        this.loading = false;
      }
    });
  }

  register(): void {
    this.loading = true;
    this.error = '';
    this.authService.register(this.registerData).subscribe({
      next: () => {
        this.cartService.mergeGuestCart().subscribe();
        this.router.navigate(['/rewards']);
      },
      error: (err) => {
        this.error = err.error || 'Registration failed';
        this.loading = false;
      }
    });
  }
}
