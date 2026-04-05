package com.bookchigi.study.application;

import com.bookchigi.book.application.BookService;
import com.bookchigi.book.domain.Book;
import com.bookchigi.book.presentation.dto.PageResponse;
import com.bookchigi.common.exception.BusinessException;
import com.bookchigi.common.exception.ErrorCode;
import com.bookchigi.study.domain.EnrollmentStatus;
import com.bookchigi.study.domain.Study;
import com.bookchigi.study.domain.StudyMember;
import com.bookchigi.study.domain.StudyRole;
import com.bookchigi.study.infrastructure.StudyMemberRepository;
import com.bookchigi.study.infrastructure.StudyRepository;
import com.bookchigi.study.presentation.dto.StudyCreateRequest;
import com.bookchigi.study.presentation.dto.MyStudyResponse;
import com.bookchigi.study.presentation.dto.StudyDetailResponse;
import com.bookchigi.study.presentation.dto.StudyResponse;
import com.bookchigi.study.presentation.dto.StudyUpdateRequest;
import com.bookchigi.user.domain.User;
import com.bookchigi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final BookService bookService;
    private final UserRepository userRepository;

    @Transactional
    public StudyResponse create(String isbn, StudyCreateRequest request, Long userId) {
        Book book = bookService.getOrCreateByIsbn(isbn);

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Study study = Study.create(
                request.name(),
                request.description(),
                request.maxMembers(),
                request.enrollmentStart(),
                request.enrollmentEnd(),
                request.isPublic(),
                book
        );
        Study savedStudy = studyRepository.save(study);

        StudyMember leader = StudyMember.createLeader(savedStudy, creator);
        studyMemberRepository.save(leader);

        return StudyResponse.from(savedStudy, creator.getNickname());
    }

    public StudyDetailResponse getStudy(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        if (!study.isPublic()) {
            if (userId == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }
            boolean isMember = studyMemberRepository.existsByStudyIdAndUserId(studyId, userId);
            if (!isMember) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
        }

        List<StudyMember> members = studyMemberRepository.findByStudyId(studyId);

        return StudyDetailResponse.from(study, members, userId);
    }

    @Transactional
    public StudyDetailResponse update(Long studyId, StudyUpdateRequest request, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        boolean isLeader = studyMemberRepository.findByStudyIdAndRole(studyId, StudyRole.LEADER)
                .map(member -> member.getUser().getId().equals(userId))
                .orElse(false);

        if (!isLeader) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        study.update(
                request.name(),
                request.description(),
                request.maxMembers(),
                request.enrollmentStart(),
                request.enrollmentEnd(),
                request.isPublic()
        );

        List<StudyMember> members = studyMemberRepository.findByStudyId(studyId);
        return StudyDetailResponse.from(study, members, userId);
    }

    @Transactional
    public void join(Long studyId, Long userId) {
        Study study = studyRepository.findByIdForUpdate(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        EnrollmentStatus status = study.getEnrollmentStatus();
        if (status == EnrollmentStatus.UPCOMING || status == EnrollmentStatus.CLOSED) {
            throw new BusinessException(ErrorCode.STUDY_ENROLLMENT_CLOSED);
        }

        if (studyMemberRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new BusinessException(ErrorCode.STUDY_ALREADY_JOINED);
        }

        long currentCount = studyMemberRepository.countByStudyId(studyId);
        if (currentCount >= study.getMaxMembers()) {
            throw new BusinessException(ErrorCode.STUDY_FULL);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        StudyMember member = StudyMember.createMember(study, user);
        studyMemberRepository.save(member);
    }

    public PageResponse<MyStudyResponse> getMyStudies(Long userId, StudyRole role, int page, int size) {
        Page<StudyMember> memberPage = studyMemberRepository
                .findByUserIdAndRoleOrderByJoinedAtDesc(userId, role, PageRequest.of(page, size));

        List<MyStudyResponse> content = memberPage.getContent().stream()
                .map(MyStudyResponse::from)
                .toList();

        return new PageResponse<>(content, page, size, memberPage.getTotalElements(), memberPage.getTotalPages());
    }

    public PageResponse<StudyResponse> getStudiesByIsbn(String isbn, int page, int size) {
        Page<Study> studyPage = studyRepository.findByBookIsbnOrderByCreatedAtDesc(isbn, PageRequest.of(page, size));

        List<StudyResponse> content = studyPage.getContent().stream()
                .map(study -> {
                    String leaderNickname = studyMemberRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER)
                            .map(member -> member.getUser().getNickname())
                            .orElse(null);
                    return StudyResponse.from(study, leaderNickname);
                })
                .toList();

        return new PageResponse<>(content, page, size, studyPage.getTotalElements(), studyPage.getTotalPages());
    }
}
