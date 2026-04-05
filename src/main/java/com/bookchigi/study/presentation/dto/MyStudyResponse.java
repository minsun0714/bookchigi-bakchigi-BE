package com.bookchigi.study.presentation.dto;

import com.bookchigi.book.presentation.dto.BookResponse;
import com.bookchigi.study.domain.EnrollmentStatus;
import com.bookchigi.study.domain.Study;
import com.bookchigi.study.domain.StudyMember;
import com.bookchigi.study.domain.StudyRole;

import java.time.Instant;
import java.time.LocalDateTime;

public record MyStudyResponse(
        Long id,
        String name,
        String description,
        int maxMembers,
        LocalDateTime enrollmentStart,
        LocalDateTime enrollmentEnd,
        EnrollmentStatus enrollmentStatus,
        boolean isPublic,
        StudyRole myRole,
        Instant joinedAt,
        BookResponse book
) {
    public static MyStudyResponse from(StudyMember member) {
        Study study = member.getStudy();

        return new MyStudyResponse(
                study.getId(),
                study.getName(),
                study.getDescription(),
                study.getMaxMembers(),
                study.getEnrollmentStart(),
                study.getEnrollmentEnd(),
                study.getEnrollmentStatus(),
                study.isPublic(),
                member.getRole(),
                member.getJoinedAt(),
                BookResponse.from(study.getBook())
        );
    }
}
