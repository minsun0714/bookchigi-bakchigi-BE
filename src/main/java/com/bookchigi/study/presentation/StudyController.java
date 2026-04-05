package com.bookchigi.study.presentation;

import com.bookchigi.auth.domain.CustomUserPrincipal;
import com.bookchigi.book.presentation.dto.PageResponse;
import com.bookchigi.study.application.StudyService;
import com.bookchigi.study.domain.StudyRole;
import com.bookchigi.study.presentation.dto.StudyMemberResponse;
import com.bookchigi.study.presentation.dto.MyStudyResponse;
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
import java.util.List;

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

    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        studyService.delete(studyId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/studies/{studyId}/pending-members")
    public ResponseEntity<List<StudyMemberResponse>> getPendingMembers(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        List<StudyMemberResponse> response = studyService.getPendingMembers(studyId, principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/studies/{studyId}/members/{userId}/approve")
    public ResponseEntity<Void> approve(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        studyService.approve(studyId, userId, principal.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/studies/{studyId}/members/{userId}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        studyService.reject(studyId, userId, principal.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me/studies")
    public ResponseEntity<PageResponse<MyStudyResponse>> getMyStudies(
            @RequestParam StudyRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PageResponse<MyStudyResponse> response = studyService.getMyStudies(principal.getUserId(), role, page, size);
        return ResponseEntity.ok(response);
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
