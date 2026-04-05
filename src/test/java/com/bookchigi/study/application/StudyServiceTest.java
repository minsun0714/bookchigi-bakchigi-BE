package com.bookchigi.study.application;

import com.bookchigi.book.application.BookService;
import com.bookchigi.book.domain.Book;
import com.bookchigi.common.exception.BusinessException;
import com.bookchigi.common.exception.ErrorCode;
import com.bookchigi.study.domain.Study;
import com.bookchigi.study.presentation.dto.StudyCreateRequest;
import com.bookchigi.study.presentation.dto.StudyResponse;
import com.bookchigi.study.infrastructure.StudyRepository;
import com.bookchigi.user.domain.User;
import com.bookchigi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @InjectMocks
    private StudyService studyService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private BookService bookService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("스터디를 생성할 수 있다")
    void create() {
        String isbn = "9791173576577";
        Long userId = 1L;

        Book book = Book.builder()
                .isbn(isbn)
                .title("테스트 책")
                .author("테스트 저자")
                .build();

        User creator = User.createFromOAuth("test@gmail.com", "테스터", "GOOGLE");

        StudyCreateRequest request = new StudyCreateRequest(
                "자바 스터디", "자바를 공부합니다", 10,
                LocalDateTime.of(2026, 4, 10, 9, 0), LocalDateTime.of(2026, 4, 30, 18, 0),
                true
        );

        given(bookService.getOrCreateByIsbn(isbn)).willReturn(book);
        given(userRepository.findById(userId)).willReturn(Optional.of(creator));
        given(studyRepository.save(any(Study.class))).willAnswer(invocation -> invocation.getArgument(0));

        StudyResponse response = studyService.create(isbn, request, userId);

        assertThat(response.name()).isEqualTo("자바 스터디");
        assertThat(response.maxMembers()).isEqualTo(10);
        assertThat(response.isPublic()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 스터디 생성 시 예외가 발생한다")
    void createWithInvalidUser() {
        String isbn = "9791173576577";
        Long userId = 999L;

        Book book = Book.builder()
                .isbn(isbn)
                .build();

        StudyCreateRequest request = new StudyCreateRequest(
                "자바 스터디", "자바를 공부합니다", 10,
                LocalDateTime.of(2026, 4, 10, 9, 0), LocalDateTime.of(2026, 4, 30, 18, 0),
                true
        );

        given(bookService.getOrCreateByIsbn(isbn)).willReturn(book);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studyService.create(isbn, request, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
