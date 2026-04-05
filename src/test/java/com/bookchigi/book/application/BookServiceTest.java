package com.bookchigi.book.application;

import com.bookchigi.book.domain.Book;
import com.bookchigi.book.infrastructure.BookRepository;
import com.bookchigi.book.infrastructure.NaverBookClient;
import com.bookchigi.book.presentation.dto.BookResponse;
import com.bookchigi.book.presentation.dto.NaverBookResponse;
import com.bookchigi.book.presentation.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private NaverBookClient naverBookClient;

    @Test
    @DisplayName("query가 없으면 RDS에서 스터디가 있는 책 목록을 조회한다")
    void getBooksWithoutQuery() {
        Book book = Book.builder()
                .isbn("9791173576577")
                .title("테스트 책")
                .author("테스트 저자")
                .build();

        PageImpl<Book> bookPage = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
        given(bookRepository.findBooksWithStudies(any(PageRequest.class))).willReturn(bookPage);

        PageResponse<BookResponse> result = bookService.getBooks(null, 0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).isbn()).isEqualTo("9791173576577");
        verify(naverBookClient, never()).search(any(), any(int.class), any(int.class));
    }

    @Test
    @DisplayName("query가 있으면 네이버 API를 호출한다")
    void getBooksWithQuery() {
        NaverBookResponse.Item item = new NaverBookResponse.Item(
                "주식투자", "https://link", "https://image",
                "저자", "19800", "출판사", "20260101",
                "9791173576577", "설명"
        );
        NaverBookResponse naverResponse = new NaverBookResponse(1, 1, 10, List.of(item));
        given(naverBookClient.search("주식", 0, 10)).willReturn(naverResponse);

        PageResponse<BookResponse> result = bookService.getBooks("주식", 0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("주식투자");
        assertThat(result.totalElements()).isEqualTo(1);
        verify(bookRepository, never()).findBooksWithStudies(any());
    }

    @Test
    @DisplayName("빈 query면 RDS에서 조회한다")
    void getBooksWithBlankQuery() {
        PageImpl<Book> bookPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        given(bookRepository.findBooksWithStudies(any(PageRequest.class))).willReturn(bookPage);

        PageResponse<BookResponse> result = bookService.getBooks("  ", 0, 10);

        assertThat(result.content()).isEmpty();
        verify(naverBookClient, never()).search(any(), any(int.class), any(int.class));
    }

    @Test
    @DisplayName("ISBN으로 책이 존재하면 기존 책을 반환한다")
    void upsertExistingBook() {
        Book existingBook = Book.builder()
                .isbn("9791173576577")
                .title("기존 책")
                .author("기존 저자")
                .build();

        given(bookRepository.findByIsbn("9791173576577")).willReturn(Optional.of(existingBook));

        Book result = bookService.upsert(
                "9791173576577", "새 제목", "새 저자",
                "출판사", "이미지", "설명", "20260101"
        );

        assertThat(result.getTitle()).isEqualTo("기존 책");
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("ISBN으로 책이 없으면 새로 저장한다")
    void upsertNewBook() {
        given(bookRepository.findByIsbn("9791173576577")).willReturn(Optional.empty());
        given(bookRepository.save(any(Book.class))).willAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.upsert(
                "9791173576577", "새 책", "새 저자",
                "출판사", "이미지", "설명", "20260101"
        );

        assertThat(result.getTitle()).isEqualTo("새 책");
        verify(bookRepository).save(any(Book.class));
    }
}
