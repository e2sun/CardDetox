import { Product } from './product.model';

export interface OrderItem {
  id: number;
  product: Product;
  quantity: number;
  selectedSize?: string;
  selectedColor?: string;
  priceAtPurchase: number;
}

export interface Order {
  id: number;
  items: OrderItem[];
  subtotal: number;
  tokensUsed: number;
  totalTokens: number;
  status: string;
  fakeTrackingNumber: string;
  createdAt: string;
  estimatedDelivery: string;
}
