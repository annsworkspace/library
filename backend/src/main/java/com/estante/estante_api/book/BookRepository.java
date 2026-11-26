package com.estante.estante_api.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Modifying
    @Query(value = "UPDATE books SET owner_id = :ownerId WHERE owner_id IS NULL", nativeQuery = true)
    int assignUnownedBooksTo(@Param("ownerId") Long ownerId);

    List<Book> findAllByOwner_EmailIgnoreCaseOrderByIdAsc(String email);

    Optional<Book> findByIdAndOwner_EmailIgnoreCase(Long id, String email);

    boolean existsByAuthor_Id(Long authorId);
    boolean existsByCategory_Id(Long categoryId);
}
