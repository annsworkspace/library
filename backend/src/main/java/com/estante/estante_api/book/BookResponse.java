package com.estante.estante_api.book;

import java.time.LocalDate;

public record BookResponse(
        Long id,
        String title,
        String author,
        String genre,
        String notes,
        String coverPath,
        ReadingStatus readingStatus,
        String loanedTo,
        LocalDate loanDate,
        LocalDate returnedDate) {

    static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getCategory().getName(),
                book.getNotes(),
                book.getCoverPath(),
                book.getReadingStatus(),
                book.getLoanedTo(),
                book.getLoanDate(),
                book.getReturnedDate());
    }
}