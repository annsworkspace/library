import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ReadingStatus = 'WANT_TO_READ' | 'READING' | 'READ';

export interface LibraryBook {
  id: number;
  title: string;
  author: string;
  genre: string;
  notes: string | null;
  coverPath?: string | null;
  readingStatus: ReadingStatus;
  loanedTo: string | null;
  loanDate: string | null;
  returnedDate: string | null;
}

export interface LibraryCategory {
  id: number;
  name: string;
}

export interface LibraryAuthor {
  id: number;
  name: string;
}

export interface CoverUploadResponse {
  coverPath: string;
  recognizedLines: string[];
}

export type SaveBook =
  Omit<LibraryBook, 'id' | 'author'> & { authorId: number };

@Injectable({ providedIn: 'root' })
export class BookApiService {
  private readonly apiUrl = 'http://localhost:8081/api';

  constructor(private readonly http: HttpClient) {}

  getBooks(): Observable<LibraryBook[]> {
    return this.http.get<LibraryBook[]>(`${this.apiUrl}/books`);
  }

  getCategories(): Observable<LibraryCategory[]> {
    return this.http.get<LibraryCategory[]>(`${this.apiUrl}/categories`);
  }

  createCategory(name: string): Observable<LibraryCategory> {
  return this.http.post<LibraryCategory>(`${this.apiUrl}/categories`, { name });
  }

  updateCategory(id: number, name: string): Observable<LibraryCategory> {
    return this.http.put<LibraryCategory>(`${this.apiUrl}/categories/${id}`, { name });
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/categories/${id}`);
  }

  getAuthors(): Observable<LibraryAuthor[]> {
    return this.http.get<LibraryAuthor[]>(`${this.apiUrl}/authors`);
  }

  createAuthor(name: string): Observable<LibraryAuthor> {
    return this.http.post<LibraryAuthor>(`${this.apiUrl}/authors`, { name });
  }

  updateAuthor(id: number, name: string): Observable<LibraryAuthor> {
  return this.http.put<LibraryAuthor>(`${this.apiUrl}/authors/${id}`, { name });
  }

  deleteAuthor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/authors/${id}`);
  }

  getCover(filename: string): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/books/covers/${encodeURIComponent(filename)}`,
      { responseType: 'blob' }
    );
  }

  createBook(book: SaveBook): Observable<LibraryBook> {
    return this.http.post<LibraryBook>(`${this.apiUrl}/books`, book);
  }

  uploadCover(image: File): Observable<CoverUploadResponse> {
    const formData = new FormData();
    formData.append('image', image);

    return this.http.post<CoverUploadResponse>(
      `${this.apiUrl}/books/covers`,
      formData
    );
  }

  updateBook(id: number, book: SaveBook): Observable<LibraryBook> {
    return this.http.put<LibraryBook>(`${this.apiUrl}/books/${id}`, book);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/books/${id}`);
  }
}