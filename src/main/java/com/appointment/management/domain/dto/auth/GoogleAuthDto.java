package com.appointment.management.domain.dto.auth;

import lombok.NonNull;

public record GoogleAuthDto(
        @NonNull String qrCodeUrl,
        @NonNull String secret
) {
}
