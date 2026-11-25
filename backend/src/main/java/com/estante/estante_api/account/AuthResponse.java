package com.estante.estante_api.account;

public record AuthResponse(
        String token,
        String name,
        String email,
        String role) {}