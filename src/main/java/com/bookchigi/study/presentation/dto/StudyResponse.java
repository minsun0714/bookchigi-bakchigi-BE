package com.bookchigi.study.presentation.dto;

import com.bookchigi.study.domain.Study;

import java.time.Instant;
import java.time.LocalDateTime;

public record StudyResponse(
        Long id,
        String name,
        String description,
        int maxMembers,
        LocalDateTime enrollmentStart,
        LocalDateTime enrollmentEnd,
        boolean isPublic,
        String leaderNickname,
        Instant createdAt
) {
    public static StudyResponse from(Study study, String leaderNickname) {
        return new StudyResponse(
                study.getId(),
                study.getName(),
                study.getDescription(),
                study.getMaxMembers(),
                study.getEnrollmentStart(),
                study.getEnrollmentEnd(),
                study.isPublic(),
                leaderNickname,
                study.getCreatedAt()
        );
    }
}
