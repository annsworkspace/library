package com.estante.estante_api.book;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books/covers")
public class BookCoverController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookCoverController.class);

    private final BookCoverService bookCoverService;
    private final TesseractOcrService tesseractOcrService;

    public BookCoverController(
            BookCoverService bookCoverService,
            TesseractOcrService tesseractOcrService) {
        this.bookCoverService = bookCoverService;
        this.tesseractOcrService = tesseractOcrService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BookCoverUploadResponse uploadCover(
            @RequestParam("image") MultipartFile image) throws IOException {
        String filename = bookCoverService.store(image);

        Resource savedImage = bookCoverService.load(filename)
                .orElseThrow(() -> new IOException("A imagem salva não pôde ser carregada."));

        List<String> recognizedLines;
        try {
            recognizedLines = tesseractOcrService.recognize(savedImage.getFile().toPath());
        } catch (IOException exception) {
            LOGGER.warn("OCR failed for uploaded cover {}; returning the cover without recognized text.",
                    filename, exception);
            recognizedLines = List.of();
        }

        return new BookCoverUploadResponse(filename, recognizedLines);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getCover(@PathVariable String filename) {
        Resource resource = bookCoverService.load(filename).orElse(null);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType contentType = filename.endsWith(".png")
                ? MediaType.IMAGE_PNG
                : filename.endsWith(".webp")
                        ? MediaType.parseMediaType("image/webp")
                        : MediaType.IMAGE_JPEG;

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }
}
