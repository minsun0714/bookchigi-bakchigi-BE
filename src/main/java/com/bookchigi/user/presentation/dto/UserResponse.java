package com.bookchigi.user.presentation.dto;

import com.bookchigi.user.domain.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String name,
        String nickname,
        String profileImage,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getProfileImage(),
                user.getCreatedAt()
        );
    }
}
