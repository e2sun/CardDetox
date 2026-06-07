import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  { path: '', loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule) },
  { path: 'shop', loadChildren: () => import('./features/products/products.module').then(m => m.ProductsModule) },
  { path: 'product/:id', loadChildren: () => import('./features/product-detail/product-detail.module').then(m => m.ProductDetailModule) },
  { path: 'cart', loadChildren: () => import('./features/cart/cart.module').then(m => m.CartModule) },
  { path: 'checkout', canActivate: [AuthGuard], loadChildren: () => import('./features/checkout/checkout.module').then(m => m.CheckoutModule) },
  { path: 'order-success/:id', canActivate: [AuthGuard], loadChildren: () => import('./features/order-success/order-success.module').then(m => m.OrderSuccessModule) },
  { path: 'auth', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  { path: 'account', canActivate: [AuthGuard], loadChildren: () => import('./features/account/account.module').then(m => m.AccountModule) },
  { path: 'clearance', loadChildren: () => import('./features/clearance/clearance.module').then(m => m.ClearanceModule) },
  { path: 'rewards', canActivate: [AuthGuard], loadChildren: () => import('./features/rewards/rewards.module').then(m => m.RewardsModule) },
  { path: 'about', loadChildren: () => import('./features/about/about.module').then(m => m.AboutModule) },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
