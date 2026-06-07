export interface User {
  userId: number;
  username: string;
  email: string;
  detoxTokens: number;
  spinAvailable: boolean;
  totalOrders: number;
  totalSaved: number;
}

export interface AuthResponse extends User {
  token: string;
}
