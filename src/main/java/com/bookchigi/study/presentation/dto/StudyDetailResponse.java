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
        boolean isCurrentUserLeader,
        Instant createdAt,
        BookResponse book,
        List<StudyMemberResponse> members
) {
    public static StudyDetailResponse from(Study study, List<StudyMember> members, Long currentUserId) {
        List<StudyMemberResponse> memberResponses = members.stream()
                .map(StudyMemberResponse::from)
                .toList();

        boolean isLeader = members.stream()
                .anyMatch(m -> m.isLeader() && m.getUser().getId().equals(currentUserId));

        return new StudyDetailResponse(
                study.getId(),
                study.getName(),
                study.getDescription(),
                study.getMaxMembers(),
                study.getEnrollmentStart(),
                study.getEnrollmentEnd(),
                study.isPublic(),
                isLeader,
                study.getCreatedAt(),
                BookResponse.from(study.getBook()),
                memberResponses
        );
    }
}
