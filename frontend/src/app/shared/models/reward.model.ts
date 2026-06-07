export interface Reward {
  id: number;
  title: string;
  description: string;
  tokenReward: number;
  category: string;
  icon: string;
  isRepeatable: boolean;
  isActive: boolean;
}

export interface UserReward {
  id: number;
  reward: Reward;
  completedDate: string;
}
