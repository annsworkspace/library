import { Component, OnInit } from '@angular/core';
import { BookApiService, LibraryAuthor } from '../../../core/services/book-api.service';

@Component({
  selector: 'app-author-admin',
  templateUrl: './author-admin.component.html'
})
export class AuthorAdminComponent implements OnInit {
  authors: LibraryAuthor[] = [];
  authorName = '';
  editingId: number | null = null;
  errorMessage = '';
  isLoading = false;
  isSaving = false;

  constructor(private readonly bookApi: BookApiService) {}

  ngOnInit(): void {
    this.loadAuthors();
  }

  loadAuthors(): void {
    this.isLoading = true;
    this.bookApi.getAuthors().subscribe({
      next: authors => {
        this.authors = authors;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load authors.';
        this.isLoading = false;
      }
    });
  }

  saveAuthor(): void {
    const name = this.authorName.trim();
    if (!name) {
      this.errorMessage = 'Enter an author name.';
      return;
    }

    this.errorMessage = '';
    this.isSaving = true;

    const request = this.editingId === null
      ? this.bookApi.createAuthor(name)
      : this.bookApi.updateAuthor(this.editingId, name);

    request.subscribe({
      next: () => {
        this.authorName = '';
        this.editingId = null;
        this.isSaving = false;
        this.loadAuthors();
      },
      error: () => {
        this.errorMessage = 'Could not save the author. The name may already exist.';
        this.isSaving = false;
      }
    });
  }

  editAuthor(author: LibraryAuthor): void {
    this.editingId = author.id;
    this.authorName = author.name;
    this.errorMessage = '';
  }

  cancelEdit(): void {
    this.editingId = null;
    this.authorName = '';
    this.errorMessage = '';
  }

  deleteAuthor(author: LibraryAuthor): void {
    if (!window.confirm(`Delete author "${author.name}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.bookApi.deleteAuthor(author.id).subscribe({
      next: () => this.loadAuthors(),
      error: () => {
        this.errorMessage =
          'Could not delete this author. They may still be assigned to books.';
      }
    });
  }
}
