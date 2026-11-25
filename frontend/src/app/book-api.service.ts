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
  coverPath?: string | null; // lets frontend send cover path when one exists and omit for books w/out cover
  readingStatus: ReadingStatus;
  loanedTo: string | null;
  loanDate: string | null;
  returnedDate: string | null;
}

export interface LibraryCategory {
  id: number;
  name: string;
}

export interface CoverUploadResponse {
  coverPath: string;
  recognizedLines: string[];
}

export type CreateBook = Omit<LibraryBook, 'id'>;

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

  getCover(filename: string): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/books/covers/${encodeURIComponent(filename)}`,
      { responseType: 'blob' }
    );
  }

  createBook(book: CreateBook): Observable<LibraryBook> {
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

  updateBook(id: number, book: CreateBook): Observable<LibraryBook> {
    return this.http.put<LibraryBook>(`${this.apiUrl}/books/${id}`, book);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/books/${id}`);
  }
}