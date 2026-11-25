import { Component, OnInit } from '@angular/core';
import { BookApiService } from '../book-api.service';

interface Profile { firstName: string; lastName: string; email: string; favoriteGenre: string; }
const PROFILE_KEY = 'snoopys-library-profile';

@Component({ selector: 'app-account', templateUrl: './account.component.html' })
export class AccountComponent implements OnInit {
  genres: string[] = [];
  profile: Profile = this.loadProfile();
  password = '';
  saved = false;
  errorMessage = '';

  constructor(private readonly bookApi: BookApiService) {}

  ngOnInit(): void {
    this.bookApi.getCategories().subscribe({
      next: categories => this.genres = categories.map(category => category.name),
      error: () => this.errorMessage = 'Could not load genres. Check that the backend is running.'
    });
  }

  saveProfile(): void {
    localStorage.setItem(PROFILE_KEY, JSON.stringify(this.profile));
    this.password = '';
    this.saved = true;
    this.errorMessage = '';
  }

  private loadProfile(): Profile {
    try {
      return JSON.parse(localStorage.getItem(PROFILE_KEY) || '') as Profile;
    } catch {
      return { firstName: '', lastName: '', email: '', favoriteGenre: 'Romance' };
    }
  }
}
