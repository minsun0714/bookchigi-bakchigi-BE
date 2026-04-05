package com.bookchigi.study.presentation.dto;

import com.bookchigi.book.presentation.dto.BookResponse;
import com.bookchigi.study.domain.Study;
import com.bookchigi.study.domain.StudyMember;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record StudyDetailResponse(
        Long id,
        String name,
        String description,
        int maxMembers,
        LocalDateTime enrollmentStart,
        LocalDateTime enrollmentEnd,
        boolean isPublic,
        Instant createdAt,
        BookResponse book,
        List<StudyMemberResponse> members
) {
    public static StudyDetailResponse from(Study study, List<StudyMember> members) {
        List<StudyMemberResponse> memberResponses = members.stream()
                .map(StudyMemberResponse::from)
                .toList();

        return new StudyDetailResponse(
                study.getId(),
                study.getName(),
                study.getDescription(),
                study.getMaxMembers(),
                study.getEnrollmentStart(),
                study.getEnrollmentEnd(),
                study.isPublic(),
                study.getCreatedAt(),
                BookResponse.from(study.getBook()),
                memberResponses
        );
    }
}
