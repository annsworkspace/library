import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './features/library/home/home.component';
import { AddBookComponent } from './features/library/add-book/add-book.component';
import { AccountComponent } from './features/account/account.component';
import { AuthComponent } from './features/auth/auth.component';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';
import { AuthorAdminComponent } from './features/admin/authors/author-admin.component';
import { CategoryAdminComponent } from './features/admin/categories/category-admin.component';

const routes: Routes = [
  { path: 'login', component: AuthComponent, data: { mode: 'login' } },
  { path: 'register', component: AuthComponent, data: { mode: 'register' } },

  { path: '', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'add', component: AddBookComponent, canActivate: [AuthGuard] },
  { path: 'edit/:id', component: AddBookComponent, canActivate: [AuthGuard] },
  { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },

  {
    path: 'admin/categories',
    component: CategoryAdminComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'admin/authors',
    component: AuthorAdminComponent,
    canActivate: [AuthGuard, AdminGuard]
  },

  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
