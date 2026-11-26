package com.estante.estante_api.author;

public record AuthorResponse(Long id, String name) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.getId(), author.getName());
    }
}