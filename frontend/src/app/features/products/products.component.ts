import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../shared/models/product.model';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  filtered: Product[] = [];
  loading = true;
  toast: string | null = null;

  selectedCategory = '';
  selectedSubcategory = '';
  sortBy = 'default';
  searchQuery = '';

  categories = ['Clothing', 'Accessories', 'Shoes', 'Lifestyle', 'Beauty'];
  subcategories: Record<string, string[]> = {
    Clothing: ['Dresses', 'Tops', 'Bottoms', 'Outerwear'],
    Accessories: ['Bags', 'Jewelry', 'Scarves', 'Eyewear'],
    Shoes: ['Heels', 'Boots', 'Casual'],
    Lifestyle: ['Candles', 'Home', 'Stationery', 'Wellness'],
    Beauty: ['Skincare', 'Fragrance', 'Makeup']
  };

  constructor(private productService: ProductService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.selectedCategory = params['category'] || '';
      this.searchQuery = params['q'] || '';
      this.loadProducts();
    });
  }

  loadProducts(): void {
    this.loading = true;
    const obs = this.searchQuery
      ? this.productService.search(this.searchQuery)
      : this.selectedCategory
        ? this.productService.getByCategory(this.selectedCategory)
        : this.productService.getAll();

    obs.subscribe(products => {
      this.products = products;
      this.applyFilters();
      this.loading = false;
    });
  }

  applyFilters(): void {
    let result = [...this.products];
    if (this.selectedSubcategory) {
      result = result.filter(p => p.subcategory === this.selectedSubcategory);
    }
    switch (this.sortBy) {
      case 'price-asc': result.sort((a, b) => a.price - b.price); break;
      case 'price-desc': result.sort((a, b) => b.price - a.price); break;
      case 'rating': result.sort((a, b) => b.rating - a.rating); break;
    }
    this.filtered = result;
  }

  setCategory(cat: string): void {
    this.selectedCategory = this.selectedCategory === cat ? '' : cat;
    this.selectedSubcategory = '';
    this.loadProducts();
  }

  setSubcategory(sub: string): void {
    this.selectedSubcategory = this.selectedSubcategory === sub ? '' : sub;
    this.applyFilters();
  }

  onSort(): void { this.applyFilters(); }

  onAddedToCart(name: string): void {
    this.toast = `${name} added to cart!`;
    setTimeout(() => this.toast = null, 3000);
  }

  get pageTitle(): string {
    if (this.searchQuery) return `Search: "${this.searchQuery}"`;
    return this.selectedCategory || 'All Products';
  }
}
