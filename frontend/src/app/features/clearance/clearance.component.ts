import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../shared/models/product.model';

@Component({
  selector: 'app-clearance',
  templateUrl: './clearance.component.html',
  styleUrls: ['./clearance.component.scss']
})
export class ClearanceComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  toast: string | null = null;

  timeLeft = { hours: 0, minutes: 0, seconds: 0 };
  private timer: ReturnType<typeof setInterval> | null = null;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.productService.getClearance().subscribe(p => {
      this.products = p;
      this.loading = false;
    });
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
  }

  private startCountdown(): void {
    const now = new Date();
    const midnight = new Date();
    midnight.setHours(24, 0, 0, 0);
    const diff = midnight.getTime() - now.getTime();

    const update = () => {
      const remaining = midnight.getTime() - Date.now();
      if (remaining <= 0) { this.timeLeft = { hours: 0, minutes: 0, seconds: 0 }; return; }
      this.timeLeft = {
        hours: Math.floor(remaining / 3600000),
        minutes: Math.floor((remaining % 3600000) / 60000),
        seconds: Math.floor((remaining % 60000) / 1000)
      };
    };
    update();
    this.timer = setInterval(update, 1000);
  }

  onAddedToCart(name: string): void {
    this.toast = `${name} added to cart!`;
    setTimeout(() => this.toast = null, 3000);
  }
}
