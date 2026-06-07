import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RewardsComponent } from './rewards.component';
import { SpinWheelComponent } from '../../shared/components/spin-wheel/spin-wheel.component';

@NgModule({
  declarations: [RewardsComponent, SpinWheelComponent],
  imports: [CommonModule, RouterModule.forChild([{ path: '', component: RewardsComponent }])]
})
export class RewardsModule {}
