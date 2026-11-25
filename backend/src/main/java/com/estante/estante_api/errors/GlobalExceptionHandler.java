package com.estante.estante_api.errors;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.security.authentication.BadCredentialsException;

import com.estante.estante_api.book.GenreNotFoundException;
import com.estante.estante_api.book.BookNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        List<ApiError.FieldErrorDetail> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();
        return response(HttpStatus.BAD_REQUEST, "Há campos inválidos na requisição.", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return response(HttpStatus.BAD_REQUEST, "O corpo da requisição está ausente ou em formato inválido.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NoResourceFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "O recurso solicitado não foi encontrado.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException exception) {
        return response(HttpStatus.METHOD_NOT_ALLOWED, "Este método não é permitido para o recurso solicitado.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException exception) {
        return response(HttpStatus.CONFLICT, "A operação viola uma regra de integridade dos dados.");
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ApiError> handleGenreNotFound(GenreNotFoundException exception) {
        return response(HttpStatus.BAD_REQUEST, "O gênero informado não existe.",
                List.of(new ApiError.FieldErrorDetail("genre", "Selecione um gênero disponível.")));
    }

    @ExceptionHandler(IllegalArgumentException.class)  // will upload 400 instead of 500
    public ResponseEntity<ApiError> handleInvalidUpload(IllegalArgumentException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleUploadTooLarge(MaxUploadSizeExceededException exception) {
        return response(HttpStatus.BAD_REQUEST, "A imagem deve ter no máximo 5 MB.");
}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        LOGGER.error("Unexpected API error", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiError> handleBookNotFound(BookNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "O livro solicitado não foi encontrado.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException exception) {
        return response(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return response(status, message, List.of());
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String message,
            List<ApiError.FieldErrorDetail> fields) {
        ApiError body = new ApiError(Instant.now(), status.value(), message, fields);
        return ResponseEntity.status(status).body(body);
    }
}
