export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  clearancePrice?: number;
  isClearance: boolean;
  imageUrl: string;
  category: string;
  subcategory: string;
  brand?: string;
  inStock: boolean;
  stockCount: number;
  rating: number;
  reviewCount: number;
  sizes?: string[];
  colors?: string[];
  tags?: string[];
}
