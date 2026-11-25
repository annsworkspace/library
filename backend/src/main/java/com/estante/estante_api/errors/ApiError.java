package com.estante.estante_api.errors;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String message,
        List<FieldErrorDetail> fields) {

    public record FieldErrorDetail(String field, String message) {
    }
}
