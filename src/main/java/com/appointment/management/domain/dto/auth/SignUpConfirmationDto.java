package com.appointment.management.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SignUpConfirmationDto(
        @NotBlank String email,
        @NotBlank String code
) {
}
