import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClearanceComponent } from './clearance.component';
import { ProductCardModule } from '../../shared/components/product-card/product-card.module';

@NgModule({
  declarations: [ClearanceComponent],
  imports: [CommonModule, RouterModule.forChild([{ path: '', component: ClearanceComponent }]), ProductCardModule]
})
export class ClearanceModule {}
