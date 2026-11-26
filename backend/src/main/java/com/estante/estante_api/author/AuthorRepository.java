package com.estante.estante_api.author;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findAllByOrderByNameAsc();
    Optional<Author> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    
}