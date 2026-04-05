package com.bookchigi.book.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(name = "uq_books_isbn", columnNames = "isbn")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String isbn;

    @Column(length = 200)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(length = 100)
    private String publisher;

    @Column(length = 500)
    private String image;

    @Column(length = 2000)
    private String description;

    @Column(name = "pub_date", length = 20)
    private String pubDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public static Book createWithIsbnOnly(String isbn) {
        return Book.builder()
                .isbn(isbn)
                .build();
    }

    public boolean hasDetail() {
        return title != null;
    }

    public void fillDetail(String title, String author, String publisher,
                           String image, String description, String pubDate) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.image = image;
        this.description = description;
        this.pubDate = pubDate;
    }
}
