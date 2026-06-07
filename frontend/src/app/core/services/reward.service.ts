import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Reward, UserReward } from '../../shared/models/reward.model';

@Injectable({ providedIn: 'root' })
export class RewardService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Reward[]> {
    return this.http.get<Reward[]>(`${environment.apiUrl}/rewards`);
  }

  getUserRewards(): Observable<UserReward[]> {
    return this.http.get<UserReward[]>(`${environment.apiUrl}/rewards/user`);
  }

  completeReward(rewardId: number): Observable<{ tokensEarned: number; newBalance: number }> {
    return this.http.post<any>(`${environment.apiUrl}/rewards/${rewardId}/complete`, {});
  }

  spin(): Observable<{ tokensWon: number; newBalance: number }> {
    return this.http.post<any>(`${environment.apiUrl}/rewards/spin`, {});
  }

  canSpin(): Observable<{ canSpin: boolean }> {
    return this.http.get<any>(`${environment.apiUrl}/rewards/spin/can-spin`);
  }
}
