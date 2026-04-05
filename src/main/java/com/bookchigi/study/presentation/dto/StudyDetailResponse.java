package com.bookchigi.study.presentation.dto;

import com.bookchigi.book.presentation.dto.BookResponse;
import com.bookchigi.study.domain.Study;

import java.time.Instant;
import java.time.LocalDateTime;

public record StudyDetailResponse(
        Long id,
        String name,
        String description,
        int maxMembers,
        LocalDateTime enrollmentStart,
        LocalDateTime enrollmentEnd,
        boolean isPublic,
        String leaderNickname,
        Instant createdAt,
        BookResponse book
) {
    public static StudyDetailResponse from(Study study, String leaderNickname) {
        return new StudyDetailResponse(
                study.getId(),
                study.getName(),
                study.getDescription(),
                study.getMaxMembers(),
                study.getEnrollmentStart(),
                study.getEnrollmentEnd(),
                study.isPublic(),
                leaderNickname,
                study.getCreatedAt(),
                BookResponse.from(study.getBook())
        );
    }
}
