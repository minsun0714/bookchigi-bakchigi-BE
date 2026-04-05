package com.bookchigi.study.domain;

import com.bookchigi.book.domain.Book;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "studies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_members", nullable = false)
    private int maxMembers;

    @Column(name = "enrollment_start")
    private LocalDateTime enrollmentStart;

    @Column(name = "enrollment_end")
    private LocalDateTime enrollmentEnd;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public static Study create(String name,
                                String description,
                                int maxMembers,
                                LocalDateTime enrollmentStart,
                                LocalDateTime enrollmentEnd,
                                boolean isPublic,
                                Book book) {
        return Study.builder()
                .name(name)
                .description(description)
                .maxMembers(maxMembers)
                .enrollmentStart(enrollmentStart)
                .enrollmentEnd(enrollmentEnd)
                .isPublic(isPublic)
                .book(book)
                .build();
    }
}
