// checks image's file signature and size. saves with generated filename

package com.estante.estante_api.book;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Service
public class BookCoverService {

    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024;

    private final Path uploadDirectory;

    public BookCoverService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDirectory = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma imagem.");
        }

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("A imagem deve ter no máximo 5 MB.");
        }

        String extension = getImageExtension(image);
        String filename = UUID.randomUUID() + extension;

        Files.createDirectories(uploadDirectory);
        Path destination = uploadDirectory.resolve(filename).normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Caminho de arquivo inválido.");
        }

        try (InputStream input = image.getInputStream()) {
            Files.copy(input, destination);
        }

        return filename;
    }

    public Optional<Resource> load(String filename) {
    if (filename == null || !filename.matches("[0-9a-fA-F-]{36}\\.(jpg|png|webp)")) {
        return Optional.empty();
    }

    Path file = uploadDirectory.resolve(filename).normalize();

    if (!file.startsWith(uploadDirectory)
            || !Files.isRegularFile(file)
            || !Files.isReadable(file)) {
        return Optional.empty();
    }

        return Optional.of(new FileSystemResource(file));
    }

    private String getImageExtension(MultipartFile image) throws IOException {
        byte[] header;

        try (InputStream input = image.getInputStream()) {
            header = input.readNBytes(12);
        }

        // JPEG signature: FF D8 FF
        if (header.length >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF) {
            return ".jpg";
        }

        // PNG signature
        if (header.length >= 8
                && (header[0] & 0xFF) == 0x89
                && header[1] == 'P'
                && header[2] == 'N'
                && header[3] == 'G'
                && header[4] == 0x0D
                && header[5] == 0x0A
                && header[6] == 0x1A
                && header[7] == 0x0A) {
            return ".png";
        }

        // WEBP signature: RIFF....WEBP
        if (header.length >= 12
                && header[0] == 'R'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == 'F'
                && header[8] == 'W'
                && header[9] == 'E'
                && header[10] == 'B'
                && header[11] == 'P') {
            return ".webp";
        }

        throw new IllegalArgumentException("Envie uma imagem JPG, PNG ou WEBP.");
    }
}