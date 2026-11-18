package com.estante.estante_api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//teaching to receive book from frontend
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Estante!");
    }

    @GetMapping("/api/books")
    public List<BookResponse> listBooks() {
        return List.of(
                new BookResponse("Dune", "Frank Herbert"),
                new BookResponse("Kindred", "Octavia Butler")
        );
    }

    @PostMapping("/api/books") //data comes into backend
    public BookResponse createBook(@RequestBody CreateBookRequest request) {
        return new BookResponse(request.title(), request.author());
    }

    public record CreateBookRequest(String title, String author) {
    }

    public record BookResponse(String title, String author) {
    }
}