package com.bookchigi.study.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record StudyCreateRequest(
        @NotBlank String name,
        String description,
        @Min(2) int maxMembers,
        LocalDateTime enrollmentStart,
        LocalDateTime enrollmentEnd,
        boolean isPublic
) {}
