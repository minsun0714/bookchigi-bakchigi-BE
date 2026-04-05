package com.bookchigi.book.presentation.dto;

import com.bookchigi.book.domain.Book;

public record BookResponse(
        String isbn,
        String title,
        String author,
        String publisher,
        String image,
        String description,
        String pubDate
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getImage(),
                book.getDescription(),
                book.getPubDate()
        );
    }

    public static BookResponse from(NaverBookResponse.Item item) {
        return new BookResponse(
                item.isbn(),
                item.title(),
                item.author(),
                item.publisher(),
                item.image(),
                item.description(),
                item.pubdate()
        );
    }
}
