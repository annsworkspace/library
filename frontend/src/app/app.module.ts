import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './features/library/home/home.component';
import { AddBookComponent } from './features/library/add-book/add-book.component';
import { AccountComponent } from './features/account/account.component';
import { AuthComponent } from './features/auth/auth.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { AuthorAdminComponent } from './features/admin/authors/author-admin.component';
import { CategoryAdminComponent } from './features/admin/categories/category-admin.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AddBookComponent,
    AccountComponent,
    AuthComponent,
    AuthorAdminComponent,
    CategoryAdminComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
