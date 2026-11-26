//receives api requests
package com.estante.estante_api.book;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
public List<BookResponse> listBooks(
        @AuthenticationPrincipal Jwt jwt) {
    return bookService.findAll(jwt.getSubject());
}

@PostMapping
public ResponseEntity<BookResponse> createBook(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody CreateBookRequest request) {
    BookResponse created =
            bookService.create(request, jwt.getSubject());

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();

    return ResponseEntity.created(location).body(created);
}

@PutMapping("/{id}")
public BookResponse updateBook(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody CreateBookRequest request) {
    return bookService.update(id, request, jwt.getSubject());
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBook(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt) {
    bookService.delete(id, jwt.getSubject());
    return ResponseEntity.noContent().build();
}

}
