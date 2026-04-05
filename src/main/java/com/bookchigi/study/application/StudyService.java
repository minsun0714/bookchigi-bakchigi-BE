package com.bookchigi.study.application;

import com.bookchigi.book.application.BookService;
import com.bookchigi.book.domain.Book;
import com.bookchigi.book.presentation.dto.PageResponse;
import com.bookchigi.common.exception.BusinessException;
import com.bookchigi.common.exception.ErrorCode;
import com.bookchigi.study.domain.Study;
import com.bookchigi.study.presentation.dto.StudyCreateRequest;
import com.bookchigi.study.presentation.dto.StudyResponse;
import com.bookchigi.study.infrastructure.StudyRepository;
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
                book,
                creator
        );
        Study savedStudy = studyRepository.save(study);

        return StudyResponse.from(savedStudy);
    }

    public PageResponse<StudyResponse> getStudiesByIsbn(String isbn, int page, int size) {
        Page<Study> studyPage = studyRepository.findByBookIsbn(isbn, PageRequest.of(page, size));

        List<StudyResponse> content = studyPage.getContent().stream()
                .map(StudyResponse::from)
                .toList();

        return new PageResponse<>(content, page, size, studyPage.getTotalElements(), studyPage.getTotalPages());
    }
}
