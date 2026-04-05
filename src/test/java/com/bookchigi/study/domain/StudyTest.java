package com.bookchigi.study.domain;

import com.bookchigi.book.domain.Book;
import com.bookchigi.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bookchigi.study.domain.EnrollmentStatus;

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

        LocalDateTime start = LocalDateTime.of(2026, 4, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 30, 18, 0);

        Study study = Study.create(
                "자바 스터디",
                "자바를 공부합니다",
                10,
                start,
                end,
                true,
                book
        );

        assertThat(study.getName()).isEqualTo("자바 스터디");
        assertThat(study.getDescription()).isEqualTo("자바를 공부합니다");
        assertThat(study.getMaxMembers()).isEqualTo(10);
        assertThat(study.getEnrollmentStart()).isEqualTo(start);
        assertThat(study.getEnrollmentEnd()).isEqualTo(end);
        assertThat(study.isPublic()).isTrue();
        assertThat(study.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("비공개 스터디를 생성할 수 있다")
    void createPrivateStudy() {
        Book book = Book.builder()
                .isbn("9791173576577")
                .title("테스트 책")
                .author("테스트 저자")
                .build();

        Study study = Study.create(
                "비공개 스터디",
                "비공개입니다",
                5,
                LocalDateTime.of(2026, 4, 10, 9, 0),
                LocalDateTime.of(2026, 4, 30, 18, 0),
                false,
                book
        );

        assertThat(study.isPublic()).isFalse();
    }

    @Test
    @DisplayName("StudyMember.createLeader로 리더를 생성할 수 있다")
    void createLeader() {
        Book book = Book.builder().isbn("9791173576577").build();
        User user = User.createFromOAuth("test@gmail.com", "테스터", "GOOGLE");

        Study study = Study.create("스터디", null, 10, null, null, true, book);
        StudyMember leader = StudyMember.createLeader(study, user);

        assertThat(leader.getRole()).isEqualTo(StudyRole.LEADER);
        assertThat(leader.isLeader()).isTrue();
        assertThat(leader.getStudy()).isEqualTo(study);
        assertThat(leader.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("StudyMember.createMember로 일반 멤버를 생성할 수 있다")
    void createMember() {
        Book book = Book.builder().isbn("9791173576577").build();
        User user = User.createFromOAuth("test@gmail.com", "테스터", "GOOGLE");

        Study study = Study.create("스터디", null, 10, null, null, true, book);
        StudyMember member = StudyMember.createMember(study, user);

        assertThat(member.getRole()).isEqualTo(StudyRole.MEMBER);
        assertThat(member.isLeader()).isFalse();
    }

    // ===== enrollmentStatus =====

    @Test
    @DisplayName("모집 기간이 없으면 ALWAYS")
    void enrollmentStatusAlways() {
        Book book = Book.builder().isbn("9791173576577").build();
        Study study = Study.create("스터디", null, 10, null, null, true, book);

        assertThat(study.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.ALWAYS);
    }

    @Test
    @DisplayName("모집 시작 전이면 UPCOMING")
    void enrollmentStatusUpcoming() {
        Book book = Book.builder().isbn("9791173576577").build();
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(10);
        Study study = Study.create("스터디", null, 10, futureStart, futureEnd, true, book);

        assertThat(study.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.UPCOMING);
    }

    @Test
    @DisplayName("모집 기간 내이면 OPEN")
    void enrollmentStatusOpen() {
        Book book = Book.builder().isbn("9791173576577").build();
        LocalDateTime pastStart = LocalDateTime.now().minusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(10);
        Study study = Study.create("스터디", null, 10, pastStart, futureEnd, true, book);

        assertThat(study.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.OPEN);
    }

    @Test
    @DisplayName("모집 마감이면 CLOSED")
    void enrollmentStatusClosed() {
        Book book = Book.builder().isbn("9791173576577").build();
        LocalDateTime pastStart = LocalDateTime.now().minusDays(10);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
        Study study = Study.create("스터디", null, 10, pastStart, pastEnd, true, book);

        assertThat(study.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.CLOSED);
    }

    @Test
    @DisplayName("start만 있고 아직 시작 전이면 UPCOMING")
    void enrollmentStatusStartOnly() {
        Book book = Book.builder().isbn("9791173576577").build();
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        Study study = Study.create("스터디", null, 10, futureStart, null, true, book);

        assertThat(study.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.UPCOMING);
    }

    @Test
    @DisplayName("end만 있고 아직 마감 전이면 OPEN")
    void enrollmentStatusEndOnly() {
        Book book = Book.builder().isbn("9791173576577").build();
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(10);
        Study study = Study.create("스터디", null, 10, null, futureEnd, true, book);

        assertThat(study.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.OPEN);
    }
}
