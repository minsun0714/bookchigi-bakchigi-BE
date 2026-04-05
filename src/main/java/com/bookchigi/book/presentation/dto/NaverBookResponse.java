package com.bookchigi.book.presentation.dto;

import java.util.List;

public record NaverBookResponse(
        int total,
        int start,
        int display,
        List<Item> items
) {
    public record Item(
            String title,
            String link,
            String image,
            String author,
            String discount,
            String publisher,
            String pubdate,
            String isbn,
            String description
    ) {}
}
