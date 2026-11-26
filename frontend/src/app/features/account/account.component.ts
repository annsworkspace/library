import { Component, OnInit } from '@angular/core';
import { AuthService, AccountProfile } from '../../core/services/auth.service';
import { BookApiService } from '../../core/services/book-api.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html'
})
export class AccountComponent implements OnInit {
  genres: string[] = [];
  profile: AccountProfile = {
    name: '',
    email: '',
    favoriteGenre: null
  };

  currentPassword = '';
  newPassword = '';

  saved = false;
  passwordSaved = false;
  errorMessage = '';
  passwordError = '';
  isSavingProfile = false;
  isChangingPassword = false;

  constructor(
    private readonly authService: AuthService,
    private readonly bookApi: BookApiService
  ) {}

  ngOnInit(): void {
    this.bookApi.getCategories().subscribe({
      next: categories => {
        this.genres = categories.map(category => category.name);
      },
      error: () => {
        this.errorMessage = 'Could not load genres.';
      }
    });

    this.authService.getAccountProfile().subscribe({
      next: profile => {
        this.profile = profile;
      },
      error: () => {
        this.errorMessage = 'Could not load your account details.';
      }
    });
  }

  saveProfile(): void {
    this.saved = false;
    this.errorMessage = '';
    this.isSavingProfile = true;

    this.authService.updateAccountProfile(this.profile).subscribe({
      next: response => {
        this.profile = {
          name: response.name,
          email: response.email,
          favoriteGenre: this.profile.favoriteGenre
        };
        this.saved = true;
        this.isSavingProfile = false;
      },
      error: error => {
        this.errorMessage =
          error.error?.message || 'Could not save your account details.';
        this.isSavingProfile = false;
      }
    });
  }

  changePassword(): void {
    this.passwordSaved = false;
    this.passwordError = '';
    this.isChangingPassword = true;

    this.authService
      .changePassword(this.currentPassword, this.newPassword)
      .subscribe({
        next: () => {
          this.currentPassword = '';
          this.newPassword = '';
          this.passwordSaved = true;
          this.isChangingPassword = false;
        },
        error: error => {
          this.passwordError =
            error.error?.message || 'Could not change your password.';
          this.isChangingPassword = false;
        }
      });
  }
}
