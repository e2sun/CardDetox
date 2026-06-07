import { Component, OnInit } from '@angular/core';
import { RewardService } from '../../core/services/reward.service';
import { AuthService } from '../../core/services/auth.service';
import { Reward, UserReward } from '../../shared/models/reward.model';

@Component({
  selector: 'app-rewards',
  templateUrl: './rewards.component.html',
  styleUrls: ['./rewards.component.scss']
})
export class RewardsComponent implements OnInit {
  rewards: Reward[] = [];
  userRewards: UserReward[] = [];
  canSpin = false;
  spinResult: number | null = null;
  spinning = false;
  toast: string | null = null;
  completing: number | null = null;

  constructor(
    public authService: AuthService,
    private rewardService: RewardService
  ) {}

  ngOnInit(): void {
    this.rewardService.getAll().subscribe(r => this.rewards = r);
    this.rewardService.getUserRewards().subscribe(ur => this.userRewards = ur);
    this.rewardService.canSpin().subscribe(r => this.canSpin = r.canSpin);
  }

  onSpinComplete(tokensWon: number): void {
    this.spinResult = tokensWon;
    this.canSpin = false;
    this.authService.updateLocalUser({ detoxTokens: (this.authService.currentUser?.detoxTokens || 0) + tokensWon });
    this.showToast(`You won ${tokensWon} Detox Tokens! 🌿`);
  }

  isCompletedToday(rewardId: number): boolean {
    const today = new Date().toISOString().split('T')[0];
    return this.userRewards.some(ur => ur.reward.id === rewardId && ur.completedDate === today);
  }

  completeReward(reward: Reward): void {
    if (this.isCompletedToday(reward.id)) return;
    this.completing = reward.id;
    this.rewardService.completeReward(reward.id).subscribe({
      next: (res) => {
        this.completing = null;
        this.rewardService.getUserRewards().subscribe(ur => this.userRewards = ur);
        this.authService.updateLocalUser({ detoxTokens: res.newBalance });
        this.showToast(`+${res.tokensEarned} DT earned for "${reward.title}"! 🌿`);
      },
      error: (err) => {
        this.completing = null;
        this.showToast(err.error || 'Already completed');
      }
    });
  }

  get categories(): string[] {
    return [...new Set(this.rewards.map(r => r.category))];
  }

  rewardsByCategory(cat: string): Reward[] {
    return this.rewards.filter(r => r.category === cat);
  }

  private showToast(msg: string): void {
    this.toast = msg;
    setTimeout(() => this.toast = null, 4000);
  }
}
