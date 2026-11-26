import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  BookApiService,
  LibraryBook,
  ReadingStatus
} from '../../../core/services/book-api.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit, OnDestroy {
  genres: string[] = [];
  selectedGenre = 'all';
  books: LibraryBook[] = [];
  errorMessage = '';
  selectedStatus: ReadingStatus | 'ALL' = 'ALL';
  coverUrls: Record<number, string> = {};

  private readonly coverObjectUrls: string[] = [];

  readonly readingStatuses: { value: ReadingStatus; label: string }[] = [
    { value: 'WANT_TO_READ', label: 'Want to read' },
    { value: 'READING', label: 'Reading' },
    { value: 'READ', label: 'Read' }
  ];

  constructor(private readonly bookApi: BookApiService) {}

  ngOnInit(): void {
    this.bookApi.getCategories().subscribe({
      next: categories => {
        this.genres = categories.map(category => category.name);
      },
      error: () => {
        this.errorMessage = 'Could not load genres.';
      }
    });

    this.bookApi.getBooks().subscribe({
      next: books => {
        this.books = books;
        this.loadCovers(books);
      },
      error: () => {
        this.errorMessage = 'Could not load your shelf. Check that the backend is running.';
      }
    });
  }

  ngOnDestroy(): void {
    this.coverObjectUrls.forEach(url => URL.revokeObjectURL(url));
  }

  get totalBooks(): number {
    return this.books.length;
  }

  get readBooks(): number {
    return this.books.filter(book => book.readingStatus === 'READ').length;
  }

  get currentlyReadingBooks(): number {
    return this.books.filter(book => book.readingStatus === 'READING').length;
  }

  get loanedBooks(): number {
    return this.books.filter(
      book => book.loanDate !== null && book.returnedDate === null
    ).length;
  }

  selectGenre(genre: string): void {
    this.selectedGenre = genre;
  }

  get visibleBooks(): LibraryBook[] {
    return this.books.filter(book => {
      const matchesGenre =
        this.selectedGenre === 'all' || book.genre === this.selectedGenre;
      const matchesStatus =
        this.selectedStatus === 'ALL' ||
        book.readingStatus === this.selectedStatus;

      return matchesGenre && matchesStatus;
    });
  }

  deleteBook(book: LibraryBook): void {
    if (!window.confirm(`Delete "${book.title}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.bookApi.deleteBook(book.id).subscribe({
      next: () => {
        this.books = this.books.filter(item => item.id !== book.id);

        const coverUrl = this.coverUrls[book.id];
        if (coverUrl) {
          URL.revokeObjectURL(coverUrl);
          delete this.coverUrls[book.id];
        }
      },
      error: () => {
        this.errorMessage = `Could not delete "${book.title}". Try again.`;
      }
    });
  }

  private loadCovers(books: LibraryBook[]): void {
    books.forEach(book => {
      if (!book.coverPath) {
        return;
      }

      this.bookApi.getCover(book.coverPath).subscribe({
        next: blob => {
          const url = URL.createObjectURL(blob);
          this.coverUrls[book.id] = url;
          this.coverObjectUrls.push(url);
        }
      });
    });
  }
}
