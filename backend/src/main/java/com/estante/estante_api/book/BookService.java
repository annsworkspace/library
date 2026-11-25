package com.estante.estante_api.book;

import com.estante.estante_api.account.AppUser;
import com.estante.estante_api.account.AppUserRepository;
import com.estante.estante_api.author.Author;
import com.estante.estante_api.author.AuthorRepository;
import com.estante.estante_api.category.Category;
import com.estante.estante_api.category.CategoryRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AppUserRepository appUserRepository;
    private final AuthorRepository authorRepository;

    public BookService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            AppUserRepository appUserRepository,
            AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.appUserRepository = appUserRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll(String ownerEmail) {
        return bookRepository
                .findAllByOwner_EmailIgnoreCaseOrderByIdAsc(ownerEmail)
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional
    public BookResponse create(CreateBookRequest request, String ownerEmail) {
        validateLoanData(request);

        AppUser owner = appUserRepository.findByEmailIgnoreCase(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        Author author = findAuthor(request.authorId());
        Category category = findCategory(request.genre());

        Book book = new Book(
                owner,
                request.title().trim(),
                author,
                category,
                normalizeNotes(request.notes()),
                request.coverPath(),
                normalizeBorrower(request.loanedTo()),
                request.loanDate(),
                request.returnedDate(),
                request.readingStatus());

        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, CreateBookRequest request, String ownerEmail) {
        validateLoanData(request);

        Book book = bookRepository.findByIdAndOwner_EmailIgnoreCase(id, ownerEmail)
                .orElseThrow(() -> new BookNotFoundException(id));

        Author author = findAuthor(request.authorId());
        Category category = findCategory(request.genre());

        book.update(
                request.title().trim(),
                author,
                category,
                normalizeNotes(request.notes()),
                request.coverPath(),
                normalizeBorrower(request.loanedTo()),
                request.loanDate(),
                request.returnedDate(),
                request.readingStatus());

        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id, String ownerEmail) {
        Book book = bookRepository.findByIdAndOwner_EmailIgnoreCase(id, ownerEmail)
                .orElseThrow(() -> new BookNotFoundException(id));

        bookRepository.delete(book);
    }

    private Author findAuthor(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found."));
    }

    private Category findCategory(String genre) {
        return categoryRepository.findByNameIgnoreCase(genre.trim())
                .orElseThrow(() -> new GenreNotFoundException(genre));
    }

    private void validateLoanData(CreateBookRequest request) {
        boolean hasBorrower =
                request.loanedTo() != null && !request.loanedTo().isBlank();
        boolean hasLoanDate = request.loanDate() != null;

        if (hasBorrower != hasLoanDate) {
            throw new IllegalArgumentException(
                    "Enter both the borrower's name and the loan date.");
        }

        if (!hasBorrower && request.returnedDate() != null) {
            throw new IllegalArgumentException(
                    "A return date requires borrower and loan details.");
        }

        if (request.loanDate() != null
                && request.loanDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "The loan date cannot be in the future.");
        }

        if (request.returnedDate() != null
                && request.returnedDate().isBefore(request.loanDate())) {
            throw new IllegalArgumentException(
                    "The return date cannot be before the loan date.");
        }
    }

    private String normalizeNotes(String notes) {
        return notes == null || notes.isBlank() ? null : notes.trim();
    }

    private String normalizeBorrower(String borrower) {
        return borrower == null || borrower.isBlank() ? null : borrower.trim();
    }
}