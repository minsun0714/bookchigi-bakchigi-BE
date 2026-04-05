package com.bookchigi.study.presentation;

import com.bookchigi.auth.domain.CustomUserPrincipal;
import com.bookchigi.book.dto.PageResponse;
import com.bookchigi.study.application.StudyService;
import com.bookchigi.study.dto.StudyCreateRequest;
import com.bookchigi.study.dto.StudyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/books/{isbn}/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<StudyResponse> create(
            @PathVariable String isbn,
            @Valid @RequestBody StudyCreateRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        StudyResponse response = studyService.create(isbn, request, principal.getUserId());
        return ResponseEntity.created(URI.create("/books/" + isbn + "/studies/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<StudyResponse>> getStudies(
            @PathVariable String isbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(studyService.getStudiesByIsbn(isbn, page, size));
    }
}
