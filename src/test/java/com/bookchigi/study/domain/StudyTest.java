package com.bookchigi.study.domain;

import com.bookchigi.book.domain.Book;
import com.bookchigi.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StudyTest {

    @Test
    @DisplayName("Study.create로 스터디를 생성할 수 있다")
    void create() {
        Book book = Book.builder()
                .isbn("9791173576577")
                .title("테스트 책")
                .author("테스트 저자")
                .build();

        User creator = User.createFromOAuth("test@gmail.com", "테스터", "GOOGLE");

        LocalDateTime start = LocalDateTime.of(2026, 4, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 30, 18, 0);

        Study study = Study.create(
                "자바 스터디",
                "자바를 공부합니다",
                10,
                start,
                end,
                true,
                book,
                creator
        );

        assertThat(study.getName()).isEqualTo("자바 스터디");
        assertThat(study.getDescription()).isEqualTo("자바를 공부합니다");
        assertThat(study.getMaxMembers()).isEqualTo(10);
        assertThat(study.getEnrollmentStart()).isEqualTo(start);
        assertThat(study.getEnrollmentEnd()).isEqualTo(end);
        assertThat(study.isPublic()).isTrue();
        assertThat(study.getBook()).isEqualTo(book);
        assertThat(study.getCreator()).isEqualTo(creator);
    }

    @Test
    @DisplayName("비공개 스터디를 생성할 수 있다")
    void createPrivateStudy() {
        Book book = Book.builder()
                .isbn("9791173576577")
                .title("테스트 책")
                .author("테스트 저자")
                .build();

        User creator = User.createFromOAuth("test@gmail.com", "테스터", "GOOGLE");

        Study study = Study.create(
                "비공개 스터디",
                "비공개입니다",
                5,
                LocalDateTime.of(2026, 4, 10, 9, 0),
                LocalDateTime.of(2026, 4, 30, 18, 0),
                false,
                book,
                creator
        );

        assertThat(study.isPublic()).isFalse();
    }
}
