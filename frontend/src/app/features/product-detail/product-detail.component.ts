import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { Product } from '../../shared/models/product.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  selectedSize = '';
  selectedColor = '';
  quantity = 1;
  adding = false;
  toast: string | null = null;
  showSizeGuide = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.productService.getById(+params['id']).subscribe({
        next: p => { this.product = p; this.loading = false; },
        error: () => this.router.navigate(['/shop'])
      });
    });
  }

  get displayPrice(): number {
    if (!this.product) return 0;
    return this.product.isClearance && this.product.clearancePrice
      ? this.product.clearancePrice : this.product.price;
  }

  get discount(): number {
    if (!this.product?.isClearance || !this.product?.clearancePrice) return 0;
    return Math.round((1 - this.product.clearancePrice / this.product.price) * 100);
  }

  get stars(): number[] {
    return Array.from({ length: 5 }, (_, i) => i < Math.floor(this.product?.rating || 0) ? 1 : 0);
  }

  get stockMessage(): string {
    const n = this.product?.stockCount;
    if (!n || n > 5) return '';
    if (n === 1) return 'Only 1 left — order soon!';
    if (n <= 3) return `Only ${n} left in stock!`;
    return `Almost gone — only ${n} left`;
  }

  get isClothing(): boolean { return this.product?.category === 'Clothing'; }
  get isShoes(): boolean { return this.product?.category === 'Shoes'; }

  get deliveryRange(): string {
    const d = new Date();
    d.setDate(d.getDate() + 3);
    const from = d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    d.setDate(d.getDate() + 2);
    const to = d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    return `${from} – ${to}`;
  }

  addToCart(): void {
    if (!this.product) return;
    this.adding = true;
    if (!this.authService.isLoggedIn) {
      this.cartService.addToGuestCart({
        productId: this.product.id, quantity: this.quantity,
        selectedSize: this.selectedSize, selectedColor: this.selectedColor,
        product: this.product
      });
      this.adding = false;
      this.showToast('Added to cart!');
      return;
    }
    this.cartService.addToCart(this.product.id, this.quantity, this.selectedSize, this.selectedColor).subscribe({
      next: () => { this.adding = false; this.showToast('Added to cart!'); },
      error: () => { this.adding = false; }
    });
  }

  private showToast(msg: string): void {
    this.toast = msg;
    setTimeout(() => this.toast = null, 3000);
  }
}
