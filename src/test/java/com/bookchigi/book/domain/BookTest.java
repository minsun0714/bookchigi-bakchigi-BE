package com.bookchigi.book.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    @Test
    @DisplayName("Book을 빌더로 생성할 수 있다")
    void create() {
        Book book = Book.builder()
                .isbn("9791173576577")
                .title("테스트 책")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .image("https://example.com/image.jpg")
                .description("테스트 설명")
                .pubDate("20260101")
                .build();

        assertThat(book.getIsbn()).isEqualTo("9791173576577");
        assertThat(book.getTitle()).isEqualTo("테스트 책");
        assertThat(book.getAuthor()).isEqualTo("테스트 저자");
        assertThat(book.getPublisher()).isEqualTo("테스트 출판사");
        assertThat(book.getImage()).isEqualTo("https://example.com/image.jpg");
        assertThat(book.getDescription()).isEqualTo("테스트 설명");
        assertThat(book.getPubDate()).isEqualTo("20260101");
    }
}
