package com.appointment.management.domain.dto.auth;

import lombok.NonNull;
import lombok.With;

public record UserWithGoogleSecretDto(
        @NonNull Long id,
        @NonNull String name,
        @NonNull String email,
        @NonNull String role,
        @With String googleAuthKey
) {
}
