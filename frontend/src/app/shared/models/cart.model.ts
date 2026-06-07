import { Product } from './product.model';

export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
  selectedSize?: string;
  selectedColor?: string;
}

export interface Cart {
  id: number;
  items: CartItem[];
  updatedAt: string;
}

export interface GuestCartItem {
  productId: number;
  quantity: number;
  selectedSize?: string;
  selectedColor?: string;
  product?: Product;
}
