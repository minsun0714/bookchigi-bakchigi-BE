package com.bookchigi.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record NicknameUpdateRequest(
        @NotBlank String nickname
) {}
