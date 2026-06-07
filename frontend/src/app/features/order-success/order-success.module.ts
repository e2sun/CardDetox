import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderSuccessComponent } from './order-success.component';

@NgModule({
  declarations: [OrderSuccessComponent],
  imports: [CommonModule, RouterModule.forChild([{ path: '', component: OrderSuccessComponent }])]
})
export class OrderSuccessModule {}
