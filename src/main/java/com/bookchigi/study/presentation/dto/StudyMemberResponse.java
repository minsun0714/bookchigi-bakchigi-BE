package com.bookchigi.study.presentation.dto;

import com.bookchigi.study.domain.StudyMember;

import java.time.Instant;

public record StudyMemberResponse(
        Long userId,
        String nickname,
        boolean isLeader,
        Instant joinedAt
) {
    public static StudyMemberResponse from(StudyMember member) {
        return new StudyMemberResponse(
                member.getUser().getId(),
                member.getUser().getNickname(),
                member.isLeader(),
                member.getJoinedAt()
        );
    }
}
