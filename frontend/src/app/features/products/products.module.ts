import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductsComponent } from './products.component';
import { ProductCardModule } from '../../shared/components/product-card/product-card.module';

@NgModule({
  declarations: [ProductsComponent],
  imports: [
    CommonModule, FormsModule,
    RouterModule.forChild([{ path: '', component: ProductsComponent }]),
    ProductCardModule
  ]
})
export class ProductsModule {}
