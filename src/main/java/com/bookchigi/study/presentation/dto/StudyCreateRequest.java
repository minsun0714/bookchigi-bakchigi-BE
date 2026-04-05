package com.bookchigi.study.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudyCreateRequest(
        @NotBlank String name,
        String description,
        @Min(2) int maxMembers,
        @NotNull LocalDate enrollmentStart,
        @NotNull LocalDate enrollmentEnd,
        boolean isPublic
) {}
