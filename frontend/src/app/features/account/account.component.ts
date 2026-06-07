import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { Order } from '../../shared/models/order.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  activeTab = 'overview';
  orders: Order[] = [];
  ordersLoading = true;

  constructor(
    public authService: AuthService,
    private http: HttpClient,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(p => {
      if (p['tab']) this.activeTab = p['tab'];
    });
    this.http.get<Order[]>(`${environment.apiUrl}/orders`).subscribe({
      next: o => { this.orders = o; this.ordersLoading = false; },
      error: () => this.ordersLoading = false
    });
    this.authService.refreshUser().subscribe();
  }
}
