package com.estante.estante_api.author;
import com.estante.estante_api.book.BookRepository;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    public AuthorService(
            AuthorRepository authorRepository,
            BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository.findAllByOrderByNameAsc()
                .stream()
                .map(AuthorResponse::from)
                .toList();
    }

    @Transactional
    public AuthorResponse create(CreateAuthorRequest request) {
        String name = request.name().trim();

        if (authorRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("An author with this name already exists.");
        }

        Author author = authorRepository.save(new Author(name));
        return AuthorResponse.from(author);
    }

    @Transactional
    public AuthorResponse update(Long id, CreateAuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found."));

        String name = request.name().trim();
        authorRepository.findByNameIgnoreCase(name)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "An author with this name already exists.");
                });

        author.rename(name);
        return AuthorResponse.from(author);
    }

    @Transactional
    public void delete(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found."));

        if (bookRepository.existsByAuthor_Id(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete an author who is assigned to books.");
        }

        authorRepository.delete(author);
    }

}