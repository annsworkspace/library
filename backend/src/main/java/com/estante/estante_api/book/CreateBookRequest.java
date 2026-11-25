package com.estante.estante_api.book;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
        @NotBlank @Size(max = 200) String title,
        @NotNull Long authorId,
        @NotBlank @Size(max = 80) String genre,
        @Size(max = 2000) String notes,
        @Size(max = 500) String coverPath,
        @Size(max = 160) String loanedTo,
        LocalDate loanDate,
        LocalDate returnedDate,
        ReadingStatus readingStatus) {
}