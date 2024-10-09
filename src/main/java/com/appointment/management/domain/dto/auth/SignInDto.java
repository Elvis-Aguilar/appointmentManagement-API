package com.appointment.management.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SignInDto(
        @NotBlank String email,
        @NotBlank String password
) {
}
