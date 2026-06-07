import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cart, CartItem, GuestCartItem } from '../../shared/models/cart.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class CartService {
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  cart$ = this.cartSubject.asObservable();
  private guestCart: GuestCartItem[] = [];

  constructor(private http: HttpClient, private authService: AuthService) {
    this.loadGuestCart();
    if (this.authService.isLoggedIn) {
      this.loadServerCart();
    }
  }

  get cartCount(): number {
    const cart = this.cartSubject.value;
    if (cart) return cart.items.reduce((sum, i) => sum + i.quantity, 0);
    return this.guestCart.reduce((sum, i) => sum + i.quantity, 0);
  }

  get cartTotal(): number {
    const cart = this.cartSubject.value;
    if (cart) {
      return cart.items.reduce((sum, i) => {
        const price = i.product.isClearance && i.product.clearancePrice
          ? i.product.clearancePrice : i.product.price;
        return sum + price * i.quantity;
      }, 0);
    }
    return this.guestCart.reduce((sum, i) => {
      const price = i.product?.isClearance && i.product?.clearancePrice
        ? i.product.clearancePrice : (i.product?.price || 0);
      return sum + price * i.quantity;
    }, 0);
  }

  loadServerCart(): Observable<Cart> {
    return this.http.get<Cart>(`${environment.apiUrl}/cart`).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  addToCart(productId: number, quantity: number, size?: string, color?: string): Observable<Cart> {
    return this.http.post<Cart>(`${environment.apiUrl}/cart/items`, {
      productId, quantity, selectedSize: size, selectedColor: color
    }).pipe(tap(cart => this.cartSubject.next(cart)));
  }

  updateItem(itemId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${environment.apiUrl}/cart/items/${itemId}`, { quantity }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  removeItem(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${environment.apiUrl}/cart/items/${itemId}`).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/cart`).pipe(
      tap(() => this.cartSubject.next(null))
    );
  }

  mergeGuestCart(): Observable<Cart> {
    const items = this.guestCart.map(i => ({
      productId: i.productId, quantity: i.quantity,
      selectedSize: i.selectedSize, selectedColor: i.selectedColor
    }));
    return this.http.post<Cart>(`${environment.apiUrl}/cart/merge`, items).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.guestCart = [];
        localStorage.removeItem('cd_guest_cart');
      })
    );
  }

  addToGuestCart(item: GuestCartItem): void {
    const existing = this.guestCart.find(i =>
      i.productId === item.productId &&
      i.selectedSize === item.selectedSize &&
      i.selectedColor === item.selectedColor
    );
    if (existing) existing.quantity += item.quantity;
    else this.guestCart.push(item);
    this.saveGuestCart();
  }

  getGuestCart(): GuestCartItem[] {
    return this.guestCart;
  }

  private saveGuestCart(): void {
    localStorage.setItem('cd_guest_cart', JSON.stringify(this.guestCart));
  }

  private loadGuestCart(): void {
    const stored = localStorage.getItem('cd_guest_cart');
    this.guestCart = stored ? JSON.parse(stored) : [];
  }
}
