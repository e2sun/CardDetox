import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { Cart } from '../../shared/models/cart.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {
  cart: Cart | null = null;
  loading = false;

  useTokens = false;
  tokensToUse = 0;

  form = {
    recipientName: '',
    address: '123 Fashion Street',
    city: 'Style City',
    state: 'CA',
    zip: '90210'
  };

  constructor(
    private cartService: CartService,
    public authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cartService.cart$.subscribe(c => this.cart = c);
    this.cartService.loadServerCart().subscribe();
    if (this.authService.currentUser) {
      this.form.recipientName = this.authService.currentUser.username;
    }
  }

  get subtotal(): number {
    return this.cart?.items.reduce((sum, i) => {
      const p = i.product.isClearance && i.product.clearancePrice ? i.product.clearancePrice : i.product.price;
      return sum + p * i.quantity;
    }, 0) || 0;
  }

  get maxTokens(): number {
    return Math.min(this.authService.currentUser?.detoxTokens || 0, this.subtotal);
  }

  get finalTotal(): number {
    return Math.max(0, this.subtotal - (this.useTokens ? this.tokensToUse : 0));
  }

  onToggleTokens(): void {
    if (this.useTokens) {
      this.tokensToUse = this.maxTokens;
    } else {
      this.tokensToUse = 0;
    }
  }

  placeOrder(): void {
    this.loading = true;
    const payload = {
      useTokens: this.useTokens,
      tokensToUse: this.useTokens ? this.tokensToUse : 0,
      ...this.form
    };
    this.http.post<any>(`${environment.apiUrl}/orders/checkout`, payload).subscribe({
      next: (order) => {
        this.authService.refreshUser().subscribe();
        this.router.navigate(['/order-success', order.id]);
      },
      error: () => { this.loading = false; }
    });
  }
}
