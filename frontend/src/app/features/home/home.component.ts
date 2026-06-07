import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../shared/models/product.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  featuredProducts: Product[] = [];
  newArrivals: Product[] = [];
  toast: string | null = null;

  categories = [
    { name: 'Clothing', image: 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=600&h=700', slug: 'Clothing' },
    { name: 'Accessories', image: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=600&h=700', slug: 'Accessories' },
    { name: 'Shoes', image: 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&h=700', slug: 'Shoes' },
    { name: 'Lifestyle', image: 'https://images.unsplash.com/photo-1608181831718-c9d180d1b42c?auto=format&fit=crop&w=600&h=700', slug: 'Lifestyle' },
    { name: 'Beauty', image: 'https://images.unsplash.com/photo-1556228578-8c89e6adf883?auto=format&fit=crop&w=600&h=700', slug: 'Beauty' },
  ];

  constructor(private productService: ProductService, private router: Router) {}

  ngOnInit(): void {
    this.productService.getAll().subscribe(products => {
      this.featuredProducts = products.filter(p => p.rating >= 4.8).slice(0, 8);
      this.newArrivals = products.slice(0, 4);
    });
  }

  onAddedToCart(name: string): void {
    this.toast = `${name} added to cart`;
    setTimeout(() => this.toast = null, 3000);
  }

  shopCategory(category: string): void {
    this.router.navigate(['/shop'], { queryParams: { category } });
  }
}
