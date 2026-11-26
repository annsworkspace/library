package com.estante.estante_api.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAuthorRequest(
        @NotBlank @Size(max = 160) String name) {
}