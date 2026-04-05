package com.bookchigi.book.presentation;

import com.bookchigi.book.application.BookService;
import com.bookchigi.book.infrastructure.NaverBookClient;
import com.bookchigi.book.presentation.dto.BookResponse;
import com.bookchigi.book.presentation.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private NaverBookClient naverBookClient;

    @Test
    @DisplayName("GET /books - query 없이 책 목록을 조회한다")
    void getBooksWithoutQuery() throws Exception {
        BookResponse bookResponse = new BookResponse(
                "9791173576577", "테스트 책", "테스트 저자",
                "출판사", "이미지", "설명", "20260101"
        );
        PageResponse<BookResponse> pageResponse = new PageResponse<>(
                List.of(bookResponse), 0, 10, 1, 1
        );

        given(bookService.getBooks(null, 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].isbn").value("9791173576577"))
                .andExpect(jsonPath("$.content[0].title").value("테스트 책"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /books?query=주식 - 키워드로 검색한다")
    void getBooksWithQuery() throws Exception {
        BookResponse bookResponse = new BookResponse(
                "9791173576577", "주식투자", "저자",
                "출판사", "이미지", "설명", "20260101"
        );
        PageResponse<BookResponse> pageResponse = new PageResponse<>(
                List.of(bookResponse), 0, 10, 1, 1
        );

        given(bookService.getBooks("주식", 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/books")
                        .param("query", "주식")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("주식투자"));
    }
}
