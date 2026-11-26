package com.estante.estante_api.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estante.estante_api.book.BookRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final BookRepository bookRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName()
                ))
                .toList();
    }


    @Transactional
        public CategoryResponse create(CreateCategoryRequest request) {
            String name = request.name().trim();

            if (categoryRepository.existsByNameIgnoreCase(name)) {
                throw new IllegalArgumentException(
                        "A category with this name already exists.");
            }

            Category category = categoryRepository.save(new Category(name));
            return new CategoryResponse(category.getId(), category.getName());
        }

        @Transactional
        public CategoryResponse update(Long id, CreateCategoryRequest request) {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found."));

            String name = request.name().trim();
            categoryRepository.findByNameIgnoreCase(name)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException(
                                "A category with this name already exists.");
                    });

            category.rename(name);
            return new CategoryResponse(category.getId(), category.getName());
        }

        @Transactional
        public void delete(Long id) {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found."));

            if (bookRepository.existsByCategory_Id(id)) {
                throw new IllegalArgumentException(
                        "Cannot delete a category that is assigned to books.");
            }

            categoryRepository.delete(category);
        }
}