package com.appointment.management.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SignIn2faDto(
        @NotBlank String email,
        @PositiveOrZero @NotNull Integer code
) {
}
