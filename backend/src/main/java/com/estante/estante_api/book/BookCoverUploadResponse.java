package com.estante.estante_api.book;

import java.util.List;

public record BookCoverUploadResponse(
        String coverPath,
        List<String> recognizedLines) {
}