//will return 404 when someone tries to edit or delete a book that doesnt exist 

package com.estante.estante_api.book;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book not found: " + id);
    }
}