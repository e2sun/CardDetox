import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product } from '../../shared/models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(`${environment.apiUrl}/products`);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${environment.apiUrl}/products/${id}`);
  }

  getByCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${environment.apiUrl}/products/category/${category}`);
  }

  getClearance(): Observable<Product[]> {
    return this.http.get<Product[]>(`${environment.apiUrl}/products/clearance`);
  }

  search(query: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${environment.apiUrl}/products/search?q=${query}`);
  }
}
