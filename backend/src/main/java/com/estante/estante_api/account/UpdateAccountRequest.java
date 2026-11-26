package com.estante.estante_api.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 80) String favoriteGenre) {
}