import { Component, OnInit } from '@angular/core';
import { BookApiService, LibraryCategory } from '../../../core/services/book-api.service';

@Component({
  selector: 'app-category-admin',
  templateUrl: './category-admin.component.html'
})
export class CategoryAdminComponent implements OnInit {
  categories: LibraryCategory[] = [];
  categoryName = '';
  editingId: number | null = null;
  errorMessage = '';
  isLoading = false;
  isSaving = false;

  constructor(private readonly bookApi: BookApiService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.bookApi.getCategories().subscribe({
      next: categories => {
        this.categories = categories;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load categories.';
        this.isLoading = false;
      }
    });
  }

  saveCategory(): void {
    const name = this.categoryName.trim();
    if (!name) {
      this.errorMessage = 'Enter a category name.';
      return;
    }

    this.errorMessage = '';
    this.isSaving = true;

    const request = this.editingId === null
      ? this.bookApi.createCategory(name)
      : this.bookApi.updateCategory(this.editingId, name);

    request.subscribe({
      next: () => {
        this.categoryName = '';
        this.editingId = null;
        this.isSaving = false;
        this.loadCategories();
      },
      error: () => {
        this.errorMessage =
          'Could not save the category. It may already exist.';
        this.isSaving = false;
      }
    });
  }

  editCategory(category: LibraryCategory): void {
    this.editingId = category.id;
    this.categoryName = category.name;
    this.errorMessage = '';
  }

  cancelEdit(): void {
    this.editingId = null;
    this.categoryName = '';
    this.errorMessage = '';
  }

  deleteCategory(category: LibraryCategory): void {
    if (!window.confirm(`Delete category "${category.name}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.bookApi.deleteCategory(category.id).subscribe({
      next: () => this.loadCategories(),
      error: () => {
        this.errorMessage =
          'Could not delete this category. It may still be assigned to books.';
      }
    });
  }
}
