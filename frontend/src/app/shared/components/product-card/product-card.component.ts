import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Product } from '../../models/product.model';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.scss']
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Output() addedToCart = new EventEmitter<string>();

  addingToCart = false;

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  get displayPrice(): number {
    return this.product.isClearance && this.product.clearancePrice
      ? this.product.clearancePrice : this.product.price;
  }

  get discount(): number {
    if (!this.product.isClearance || !this.product.clearancePrice) return 0;
    return Math.round((1 - this.product.clearancePrice / this.product.price) * 100);
  }

  get stars(): string[] {
    const full = Math.floor(this.product.rating);
    const arr = [];
    for (let i = 0; i < 5; i++) arr.push(i < full ? 'full' : 'empty');
    return arr;
  }

  quickAdd(event: Event): void {
    event.stopPropagation();
    event.preventDefault();
    if (!this.authService.isLoggedIn) {
      this.cartService.addToGuestCart({ productId: this.product.id, quantity: 1, product: this.product });
      this.addedToCart.emit(this.product.name);
      return;
    }
    this.addingToCart = true;
    this.cartService.addToCart(this.product.id, 1).subscribe({
      next: () => {
        this.addingToCart = false;
        this.addedToCart.emit(this.product.name);
      },
      error: () => { this.addingToCart = false; }
    });
  }
}
