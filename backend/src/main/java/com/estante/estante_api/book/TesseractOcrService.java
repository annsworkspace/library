package com.estante.estante_api.book;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

@Service
public class TesseractOcrService {

    private static final long TIMEOUT_SECONDS = 30;
    private static final int TARGET_LONG_EDGE = 1600;

    public List<String> recognize(Path imagePath) throws IOException {
        Path processedImage = preprocess(imagePath);
        Path authorBand = null;

        try {
            List<String> lines = readLines(runTesseract(processedImage, "por+eng", "11"));

            authorBand = cropBottomTextBand(imagePath);
            if (authorBand != null) {
                String authorText = runTesseract(authorBand, "eng", "13");
                String authorLine = authorText
                        .lines()
                        .map(String::trim)
                        .map(TesseractOcrService::removeInternalNoise)
                        .map(TesseractOcrService::trimEdgeNoise)
                        .filter(TesseractOcrService::looksLikeUsefulLine)
                        .reduce((first, second) -> second)
                        .orElse("");

                if (!authorLine.isEmpty()) {
                    mergeAuthorLine(lines, authorLine);
                }
            }

            return List.copyOf(lines);
        } finally {
            if (authorBand != null) {
                Files.deleteIfExists(authorBand);
            }
            if (!processedImage.equals(imagePath)) {
                Files.deleteIfExists(processedImage);
            }
        }
    }

    private String runTesseract(Path imagePath, String languages, String pageSegmentationMode)
            throws IOException {
        Path outputFile = Files.createTempFile("estante-ocr-", ".txt");
        Process process = null;

        try {
            process = new ProcessBuilder(
                    "tesseract",
                    imagePath.toAbsolutePath().toString(),
                    "stdout",
                    "--oem",
                    "1",
                    "-l",
                    languages,
                    "--psm",
                    pageSegmentationMode)
                    .redirectOutput(outputFile.toFile())
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();

            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IOException("O reconhecimento demorou mais que 30 segundos.");
            }

            if (process.exitValue() != 0) {
                throw new IOException("O Tesseract não conseguiu ler a imagem.");
            }

            return Files.readString(outputFile, StandardCharsets.UTF_8);
        } catch (InterruptedException exception) {
            if (process != null) {
                process.destroyForcibly();
            }
            Thread.currentThread().interrupt();
            throw new IOException("O reconhecimento foi interrompido.", exception);
        } finally {
            Files.deleteIfExists(outputFile);
        }
    }

    private List<String> readLines(String text) {
        return text.lines()
                .map(String::trim)
                .filter(TesseractOcrService::looksLikeUsefulLine)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private Path preprocess(Path imagePath) throws IOException {
        BufferedImage original;

        try {
            original = ImageIO.read(imagePath.toFile());
        } catch (IOException exception) {
            return imagePath;
        }

        // Keep formats ImageIO cannot read, such as unsupported WebP, on the original OCR path.
        if (original == null) {
            return imagePath;
        }

        Rectangle coverBounds = coverBounds(original);
        int cropX = coverBounds.x;
        int cropY = coverBounds.y;
        int cropWidth = coverBounds.width;
        int cropHeight = coverBounds.height;

        double scale = Math.max(1.0, TARGET_LONG_EDGE / (double) Math.max(cropWidth, cropHeight));
        int outputWidth = Math.max(1, (int) Math.round(cropWidth * scale));
        int outputHeight = Math.max(1, (int) Math.round(cropHeight * scale));

        BufferedImage grayscale = new BufferedImage(
                outputWidth,
                outputHeight,
                BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = grayscale.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(
                original,
                0,
                0,
                outputWidth,
                outputHeight,
                cropX,
                cropY,
                cropX + cropWidth,
                cropY + cropHeight,
                null);
        graphics.dispose();

        BufferedImage higherContrast = new RescaleOp(1.25f, -10f, null).filter(grayscale, null);

        Path processedImage = Files.createTempFile("estante-ocr-image-", ".png");
        if (!ImageIO.write(higherContrast, "png", processedImage.toFile())) {
            Files.deleteIfExists(processedImage);
            return imagePath;
        }

        return processedImage;
    }

    private Path cropBottomTextBand(Path originalImage) throws IOException {
        // Use the original color image for the author line. The grayscale contrast pass
        // improves the full-cover text, but it can distort the fine serifs in author names.
        BufferedImage image = ImageIO.read(originalImage.toFile());
        if (image == null || image.getHeight() < 20 || image.getWidth() < 20) {
            return null;
        }

        Rectangle coverBounds = coverBounds(image);
        int left = coverBounds.x + Math.round(coverBounds.width * 0.058f);
        int top = coverBounds.y + Math.round(coverBounds.height * 0.864f);
        int width = Math.max(1, Math.round(coverBounds.width * 0.867f));
        int height = Math.max(1, Math.round(coverBounds.height * 0.074f));
        width = Math.min(width, image.getWidth() - left);
        height = Math.min(height, image.getHeight() - top);
        if (width < 20 || height < 10) {
            return null;
        }

        BufferedImage band = image.getSubimage(left, top, width, height);
        BufferedImage enlarged = new BufferedImage(
                width * 4,
                height * 4,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = enlarged.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.drawImage(band, 0, 0, width * 4, height * 4, null);
        graphics.dispose();

        Path croppedImage = Files.createTempFile("estante-ocr-author-", ".png");
        if (!ImageIO.write(enlarged, "png", croppedImage.toFile())) {
            Files.deleteIfExists(croppedImage);
            return null;
        }
        return croppedImage;
    }

    private Rectangle coverBounds(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Phone photos often have a wide blank background around a centered portrait cover.
        // Crop that side space while keeping a small margin around the cover.
        if (width > height * 1.2) {
            int cropHeight = Math.max(1, Math.round(height * 0.92f));
            int cropWidth = Math.max(1, Math.round(cropHeight * 0.64f));
            int cropX = Math.max(0, (width - cropWidth) / 2);
            int cropY = Math.max(0, (height - cropHeight) / 2);
            return new Rectangle(cropX, cropY, cropWidth, cropHeight);
        }

        return new Rectangle(0, 0, width, height);
    }

    private void mergeAuthorLine(List<String> lines, String authorLine) {
        String candidate = trimEdgeNoise(authorLine);
        String normalizedCandidate = normalize(candidate);
        if (normalizedCandidate.length() < 5) {
            return;
        }

        for (int index = 0; index < lines.size(); index++) {
            String normalizedLine = normalizeAuthor(lines.get(index));
            if (normalizedLine.length() >= 5
                    && (normalizedCandidate.startsWith(normalizedLine)
                            || normalizedLine.startsWith(normalizedCandidate))) {
                if (normalizedCandidate.length() > normalizedLine.length()) {
                    lines.set(index, candidate);
                }
                return;
            }
        }

        lines.add(candidate);
    }

    private static boolean looksLikeUsefulLine(String line) {
        long letterOrDigitCount = line.codePoints()
                .filter(Character::isLetterOrDigit)
                .count();
        return letterOrDigitCount >= 3;
    }

    private static String trimEdgeNoise(String line) {
        return line.replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "").trim();
    }

    private static String removeInternalNoise(String line) {
        return line.replaceAll("[^\\p{L}\\p{N} ]", " ").replaceAll("\\s+", " ").trim();
    }

    private static String normalize(String line) {
        return line.replaceAll("[^\\p{L}\\p{N}]", "").toUpperCase();
    }

    private static String normalizeAuthor(String line) {
        String substantialWords = java.util.Arrays.stream(line.split("\\s+"))
                .filter(word -> word.length() > 1)
                .reduce("", String::concat);
        return normalize(substantialWords);
    }
}
