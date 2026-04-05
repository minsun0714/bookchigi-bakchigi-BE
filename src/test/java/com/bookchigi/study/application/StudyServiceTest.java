package com.bookchigi.study.application;

import com.bookchigi.book.application.BookService;
import com.bookchigi.book.domain.Book;
import com.bookchigi.common.exception.BusinessException;
import com.bookchigi.common.exception.ErrorCode;
import com.bookchigi.study.domain.Study;
import com.bookchigi.study.domain.StudyMember;
import com.bookchigi.study.domain.StudyRole;
import com.bookchigi.study.infrastructure.StudyMemberRepository;
import com.bookchigi.study.infrastructure.StudyRepository;
import com.bookchigi.study.presentation.dto.StudyCreateRequest;
import com.bookchigi.study.presentation.dto.StudyDetailResponse;
import com.bookchigi.study.presentation.dto.StudyResponse;
import com.bookchigi.study.presentation.dto.StudyUpdateRequest;
import com.bookchigi.user.domain.User;
import com.bookchigi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @InjectMocks
    private StudyService studyService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private BookService bookService;

    @Mock
    private UserRepository userRepository;

    private Book createBook() {
        return Book.builder()
                .isbn("9791173576577")
                .title("테스트 책")
                .author("테스트 저자")
                .build();
    }

    private User createUser() {
        return User.createFromOAuth("test@gmail.com", "테스터", "GOOGLE");
    }

    // ===== create =====

    @Test
    @DisplayName("스터디를 생성하면 리더로 등록된다")
    void create() {
        String isbn = "9791173576577";
        Long userId = 1L;
        Book book = createBook();
        User creator = createUser();

        StudyCreateRequest request = new StudyCreateRequest(
                "자바 스터디", "자바를 공부합니다", 10,
                LocalDateTime.of(2026, 4, 10, 9, 0), LocalDateTime.of(2026, 4, 30, 18, 0),
                true
        );

        given(bookService.getOrCreateByIsbn(isbn)).willReturn(book);
        given(userRepository.findById(userId)).willReturn(Optional.of(creator));
        given(studyRepository.save(any(Study.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(studyMemberRepository.save(any(StudyMember.class))).willAnswer(invocation -> invocation.getArgument(0));

        StudyResponse response = studyService.create(isbn, request, userId);

        assertThat(response.name()).isEqualTo("자바 스터디");
        assertThat(response.maxMembers()).isEqualTo(10);
        verify(studyMemberRepository).save(any(StudyMember.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 스터디 생성 시 예외가 발생한다")
    void createWithInvalidUser() {
        String isbn = "9791173576577";
        Long userId = 999L;
        Book book = Book.builder().isbn(isbn).build();

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

    // ===== getStudy =====

    @Test
    @DisplayName("공개 스터디는 비로그인 사용자도 조회할 수 있다")
    void getPublicStudy() {
        Study study = Study.create("공개 스터디", "설명", 10, null, null, true, createBook());

        given(studyRepository.findById(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.findByStudyId(1L)).willReturn(List.of());

        StudyDetailResponse response = studyService.getStudy(1L, null);

        assertThat(response.name()).isEqualTo("공개 스터디");
        assertThat(response.isCurrentUserLeader()).isFalse();
        assertThat(response.isCurrentUserMember()).isFalse();
    }

    @Test
    @DisplayName("비공개 스터디는 비로그인 시 401")
    void getPrivateStudyWithoutLogin() {
        Study study = Study.create("비공개 스터디", "설명", 5, null, null, false,
                Book.builder().isbn("9791173576577").build());

        given(studyRepository.findById(1L)).willReturn(Optional.of(study));

        assertThatThrownBy(() -> studyService.getStudy(1L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("비공개 스터디는 멤버가 아니면 403")
    void getPrivateStudyWithoutMembership() {
        Study study = Study.create("비공개 스터디", "설명", 5, null, null, false,
                Book.builder().isbn("9791173576577").build());

        given(studyRepository.findById(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.existsByStudyIdAndUserId(1L, 999L)).willReturn(false);

        assertThatThrownBy(() -> studyService.getStudy(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("비공개 스터디는 멤버이면 조회할 수 있다")
    void getPrivateStudyWithMembership() {
        Study study = Study.create("비공개 스터디", "설명", 5, null, null, false,
                Book.builder().isbn("9791173576577").title("책").author("저자").build());

        given(studyRepository.findById(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.existsByStudyIdAndUserId(1L, 1L)).willReturn(true);
        given(studyMemberRepository.findByStudyId(1L)).willReturn(List.of());

        StudyDetailResponse response = studyService.getStudy(1L, 1L);

        assertThat(response.name()).isEqualTo("비공개 스터디");
    }

    // ===== update =====

    @Test
    @DisplayName("리더는 스터디를 수정할 수 있다")
    void update() {
        Long leaderId = 1L;
        Book book = createBook();
        User leader = User.builder()
                .id(leaderId)
                .email("test@gmail.com")
                .name("테스터")
                .oauthProvider("GOOGLE")
                .build();
        Study study = Study.create("원래 이름", "설명", 10, null, null, true, book);
        StudyMember leaderMember = StudyMember.createLeader(study, leader);

        given(studyRepository.findById(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.findByStudyIdAndRole(1L, StudyRole.LEADER)).willReturn(Optional.of(leaderMember));
        given(studyMemberRepository.findByStudyId(1L)).willReturn(List.of(leaderMember));

        StudyUpdateRequest request = new StudyUpdateRequest(
                "수정된 이름", "수정된 설명", 20, null, null, false
        );

        StudyDetailResponse response = studyService.update(1L, request, leaderId);

        assertThat(response.name()).isEqualTo("수정된 이름");
        assertThat(response.maxMembers()).isEqualTo(20);
        assertThat(response.isPublic()).isFalse();
    }

    @Test
    @DisplayName("리더가 아니면 스터디를 수정할 수 없다")
    void updateWithoutLeaderRole() {
        Study study = Study.create("스터디", "설명", 10, null, null, true, createBook());

        given(studyRepository.findById(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.findByStudyIdAndRole(1L, StudyRole.LEADER)).willReturn(Optional.empty());

        StudyUpdateRequest request = new StudyUpdateRequest(
                "수정", "설명", 20, null, null, true
        );

        assertThatThrownBy(() -> studyService.update(1L, request, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.FORBIDDEN.getMessage());
    }

    // ===== join =====

    @Test
    @DisplayName("스터디에 합류할 수 있다")
    void join() {
        Study study = Study.create("스터디", "설명", 10, null, null, true, createBook());
        User user = createUser();

        given(studyRepository.findByIdForUpdate(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.existsByStudyIdAndUserId(1L, 1L)).willReturn(false);
        given(studyMemberRepository.countByStudyId(1L)).willReturn(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(studyMemberRepository.save(any(StudyMember.class))).willAnswer(invocation -> invocation.getArgument(0));

        studyService.join(1L, 1L);

        verify(studyMemberRepository).save(any(StudyMember.class));
    }

    @Test
    @DisplayName("이미 가입한 스터디에 다시 합류하면 예외가 발생한다")
    void joinAlreadyJoined() {
        Study study = Study.create("스터디", "설명", 10, null, null, true, createBook());

        given(studyRepository.findByIdForUpdate(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.existsByStudyIdAndUserId(1L, 1L)).willReturn(true);

        assertThatThrownBy(() -> studyService.join(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.STUDY_ALREADY_JOINED.getMessage());

        verify(studyMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("정원이 가득 찬 스터디에 합류하면 예외가 발생한다")
    void joinFullStudy() {
        Study study = Study.create("스터디", "설명", 2, null, null, true, createBook());

        given(studyRepository.findByIdForUpdate(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.existsByStudyIdAndUserId(1L, 1L)).willReturn(false);
        given(studyMemberRepository.countByStudyId(1L)).willReturn(2L);

        assertThatThrownBy(() -> studyService.join(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.STUDY_FULL.getMessage());

        verify(studyMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("모집 기간이 아니면 합류할 수 없다")
    void joinOutsideEnrollmentPeriod() {
        LocalDateTime pastStart = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime pastEnd = LocalDateTime.of(2020, 1, 31, 23, 59);
        Study study = Study.create("스터디", "설명", 10, pastStart, pastEnd, true, createBook());

        given(studyRepository.findByIdForUpdate(1L)).willReturn(Optional.of(study));

        assertThatThrownBy(() -> studyService.join(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.STUDY_ENROLLMENT_CLOSED.getMessage());

        verify(studyMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("모집 기간이 설정되지 않으면 언제든 합류할 수 있다")
    void joinWithoutEnrollmentPeriod() {
        Study study = Study.create("스터디", "설명", 10, null, null, true, createBook());
        User user = createUser();

        given(studyRepository.findByIdForUpdate(1L)).willReturn(Optional.of(study));
        given(studyMemberRepository.existsByStudyIdAndUserId(1L, 1L)).willReturn(false);
        given(studyMemberRepository.countByStudyId(1L)).willReturn(0L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(studyMemberRepository.save(any(StudyMember.class))).willAnswer(invocation -> invocation.getArgument(0));

        studyService.join(1L, 1L);

        verify(studyMemberRepository).save(any(StudyMember.class));
    }
}
