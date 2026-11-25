package com.estante.estante_api.book;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String genre) {
        super("Genre not found: " + genre);
    }
}
