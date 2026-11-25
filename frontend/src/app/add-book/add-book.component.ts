import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookApiService, ReadingStatus } from '../book-api.service';

@Component({ selector: 'app-add-book', templateUrl: './add-book.component.html' })
export class AddBookComponent implements OnInit, OnDestroy {
  genres: string[] = [];
  title = '';
  author = '';
  genre = '';
  notes = '';
  readingStatus: ReadingStatus = 'WANT_TO_READ';
  errorMessage = '';
  bookId: number | null = null;
  isEditing = false;
  coverPath: string | null = null;
  coverPreviewUrl: string | null = null;
  isUploadingCover = false;
  isSaving = false;
  recognizedLines: string[] = [];
  ocrMessage = '';
  loanedTo = '';
  loanDate = '';
  returnedDate = '';

  constructor(
    private readonly bookApi: BookApiService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnDestroy(): void {
    this.clearCoverPreview();
  }

  onCoverSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const image = input.files?.[0];

    if (!image) {
      return;
    }

    this.clearCoverPreview();
    this.coverPreviewUrl = URL.createObjectURL(image);
    this.errorMessage = '';
    this.ocrMessage = '';
    this.recognizedLines = [];
    this.isUploadingCover = true;

    this.bookApi.uploadCover(image).subscribe({
      next: result => {
        this.coverPath = result.coverPath;
        this.recognizedLines = result.recognizedLines ?? [];
        this.ocrMessage = this.recognizedLines.length === 0
          ? 'No text was found. You can enter the book details manually.'
          : '';
        this.isUploadingCover = false;
        if (this.coverPath) {
          this.loadSavedCoverPreview(this.coverPath);
        }
    },
      error: () => {
        this.isUploadingCover = false;
        this.clearCoverPreview();
        this.errorMessage =
          'Could not upload cover. Choose a JPG, PNG, or WEBP image under 5 MB.';
        input.value = '';
      }
    });
  }

  private clearCoverPreview(): void {
    if (this.coverPreviewUrl) {
      URL.revokeObjectURL(this.coverPreviewUrl);
      this.coverPreviewUrl = null;
    }
  }

  private loadSavedCoverPreview(filename: string): void {
    this.bookApi.getCover(filename).subscribe({
      next: blob => {
        this.clearCoverPreview();
        this.coverPreviewUrl = URL.createObjectURL(blob);
      },
      error: () => {
        this.errorMessage = 'Could not load the saved book cover.';
      }
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam !== null) {
      const id = Number(idParam);
      if (!Number.isInteger(id) || id <= 0) {
        this.errorMessage = 'Invalid book ID.';
        return;
      }

      this.bookId = id;
      this.isEditing = true;
    }

    this.bookApi.getCategories().subscribe({
      next: categories => {
        this.genres = categories.map(category => category.name);
        if (!this.isEditing) {
          this.genre = this.genres.includes('Romance')
            ? 'Romance'
            : (this.genres[0] || '');
        }
      },
      error: () => {
        this.errorMessage =
          'Could not load genres. Check that the backend is running.';
      }
    });

    if (this.bookId !== null) { // loading an existing book
      this.bookApi.getBooks().subscribe({
        next: books => {
          const book = books.find(item => item.id === this.bookId);
          if (!book) {
            this.errorMessage = 'That book could not be found.';
            return;
          }

          this.title = book.title;
          this.author = book.author;
          this.genre = book.genre;
          this.notes = book.notes || '';
          this.coverPath = book.coverPath ?? null;
          this.readingStatus = book.readingStatus;
          this.loanedTo = book.loanedTo ?? '';
          this.loanDate = book.loanDate ?? '';
          this.returnedDate = book.returnedDate ?? '';
        },
        error: () => {
          this.errorMessage = 'Could not load the selected book.';
        }
      });
    }
  }

  saveBook(): void {
    this.errorMessage = '';

    if (!this.title.trim() || !this.author.trim() || !this.genre) {
      this.errorMessage =
        'Enter a title and author, and choose a genre before saving.';
      return;
    }

    const book = {
      title: this.title.trim(),
      author: this.author.trim(),
      genre: this.genre,
      notes: this.notes.trim() || null,
      coverPath: this.coverPath,
      readingStatus: this.readingStatus,
      loanedTo: this.loanedTo.trim() || null,
      loanDate: this.loanDate || null,
      returnedDate: this.returnedDate || null
    };

    const request = this.isEditing && this.bookId !== null
      ? this.bookApi.updateBook(this.bookId, book)
      : this.bookApi.createBook(book);

    this.isSaving = true;
    request.subscribe({
      next: () => {
        this.isSaving = false;
        this.router.navigate(['/']);
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage =
          'Could not save this book. Check the details and try again.';
      }
    });
  }

  useOcrLine(line: string, field: 'title' | 'author'): void {
    if (field === 'title') {
      this.title = line;
    } else {
      this.author = line;
    }
  }
}
