package com.appointment.management.domain.dto.auth;

import lombok.NonNull;

public record TokenDto(
        @NonNull String accessToken,
        @NonNull Long id,
        @NonNull String name,
        @NonNull String email,
        @NonNull Boolean temporal,
        @NonNull String role
) {
}
