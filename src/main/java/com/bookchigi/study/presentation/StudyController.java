package com.bookchigi.study.presentation;

import com.bookchigi.auth.domain.CustomUserPrincipal;
import com.bookchigi.book.presentation.dto.PageResponse;
import com.bookchigi.study.application.StudyService;
import com.bookchigi.study.presentation.dto.StudyCreateRequest;
import com.bookchigi.study.presentation.dto.StudyDetailResponse;
import com.bookchigi.study.presentation.dto.StudyResponse;
import com.bookchigi.study.presentation.dto.StudyUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/studies/{studyId}")
    public ResponseEntity<StudyDetailResponse> getStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getUserId() : null;
        StudyDetailResponse response = studyService.getStudy(studyId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/studies/{studyId}")
    public ResponseEntity<StudyDetailResponse> update(
            @PathVariable Long studyId,
            @Valid @RequestBody StudyUpdateRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        StudyDetailResponse response = studyService.update(studyId, request, principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/studies/{studyId}/join")
    public ResponseEntity<Void> join(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        studyService.join(studyId, principal.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/books/{isbn}/studies")
    public ResponseEntity<StudyResponse> create(
            @PathVariable String isbn,
            @Valid @RequestBody StudyCreateRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        StudyResponse response = studyService.create(isbn, request, principal.getUserId());
        return ResponseEntity.created(URI.create("/books/" + isbn + "/studies/" + response.id()))
                .body(response);
    }

    @GetMapping("/books/{isbn}/studies")
    public ResponseEntity<PageResponse<StudyResponse>> getStudies(
            @PathVariable String isbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(studyService.getStudiesByIsbn(isbn, page, size));
    }
}
