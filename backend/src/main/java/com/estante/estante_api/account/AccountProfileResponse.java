package com.estante.estante_api.account;

public record AccountProfileResponse(
        String name,
        String email,
        String favoriteGenre) {
}