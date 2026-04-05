package com.bookchigi.book.infrastructure;

import com.bookchigi.book.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN Study s ON s.book = b
        ORDER BY b.createdAt DESC
    """)
    Page<Book> findBooksWithStudies(Pageable pageable);
}
