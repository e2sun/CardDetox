import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { Cart, CartItem, GuestCartItem } from '../../shared/models/cart.model';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;
  guestCart: GuestCartItem[] = [];
  loading = true;

  constructor(
    public cartService: CartService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn) {
      this.cartService.loadServerCart().subscribe({
        next: cart => { this.cart = cart; this.loading = false; },
        error: () => this.loading = false
      });
      this.cartService.cart$.subscribe(c => this.cart = c);
    } else {
      this.guestCart = this.cartService.getGuestCart();
      this.loading = false;
    }
  }

  get serverItems(): CartItem[] {
    return this.cart?.items || [];
  }

  get guestItems(): GuestCartItem[] {
    return this.guestCart;
  }

  get subtotal(): number {
    if (this.authService.isLoggedIn) {
      return this.cart?.items.reduce((sum, i) => {
        const p = i.product.isClearance && i.product.clearancePrice ? i.product.clearancePrice : i.product.price;
        return sum + p * i.quantity;
      }, 0) || 0;
    }
    return this.guestCart.reduce((sum, i) => {
      const price = i.product?.isClearance && i.product?.clearancePrice ? i.product.clearancePrice : (i.product?.price || 0);
      return sum + price * i.quantity;
    }, 0);
  }

  getServerItemPrice(item: CartItem): number {
    return item.product.isClearance && item.product.clearancePrice
      ? item.product.clearancePrice : item.product.price;
  }

  getGuestItemPrice(item: GuestCartItem): number {
    const p = item.product;
    if (!p) return 0;
    return p.isClearance && p.clearancePrice ? p.clearancePrice : p.price;
  }

  updateQty(item: CartItem, qty: number): void {
    this.cartService.updateItem(item.id, qty).subscribe();
  }

  removeItem(item: CartItem): void {
    this.cartService.removeItem(item.id).subscribe();
  }

  checkout(): void {
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/auth'], { queryParams: { redirect: '/checkout' } });
      return;
    }
    this.router.navigate(['/checkout']);
  }

  get totalItems(): number {
    return this.authService.isLoggedIn ? this.serverItems.length : this.guestItems.length;
  }
}
