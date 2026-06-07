import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductDetailComponent } from './product-detail.component';

@NgModule({
  declarations: [ProductDetailComponent],
  imports: [
    CommonModule, FormsModule,
    RouterModule.forChild([{ path: '', component: ProductDetailComponent }])
  ]
})
export class ProductDetailModule {}
