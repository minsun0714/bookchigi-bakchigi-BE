package com.bookchigi.book.application;

import com.bookchigi.book.domain.Book;
import com.bookchigi.book.dto.BookResponse;
import com.bookchigi.book.dto.NaverBookResponse;
import com.bookchigi.book.dto.PageResponse;
import com.bookchigi.book.infrastructure.NaverBookClient;
import com.bookchigi.book.persistence.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final NaverBookClient naverBookClient;

    public PageResponse<BookResponse> getBooks(String query, int page, int size) {
        if (query == null || query.isBlank()) {
            return getBooksWithStudies(page, size);
        }
        return searchFromNaver(query, page, size);
    }

    private PageResponse<BookResponse> getBooksWithStudies(int page, int size) {
        Page<Book> bookPage = bookRepository.findBooksWithStudies(PageRequest.of(page, size));

        List<BookResponse> content = bookPage.getContent().stream()
                .map(BookResponse::from)
                .toList();

        return new PageResponse<>(content, page, size, bookPage.getTotalElements(), bookPage.getTotalPages());
    }

    private PageResponse<BookResponse> searchFromNaver(String query, int page, int size) {
        NaverBookResponse response = naverBookClient.search(query, page, size);

        List<BookResponse> content = response.items().stream()
                .map(BookResponse::from)
                .toList();

        int totalPages = (int) Math.ceil((double) response.total() / size);

        return new PageResponse<>(content, page, size, response.total(), totalPages);
    }

    @Transactional
    public Book upsert(String isbn, String title, String author, String publisher, String image, String description, String pubDate) {
        return bookRepository.findByIsbn(isbn)
                .orElseGet(() -> bookRepository.save(
                        Book.builder()
                                .isbn(isbn)
                                .title(title)
                                .author(author)
                                .publisher(publisher)
                                .image(image)
                                .description(description)
                                .pubDate(pubDate)
                                .build()
                ));
    }
}
