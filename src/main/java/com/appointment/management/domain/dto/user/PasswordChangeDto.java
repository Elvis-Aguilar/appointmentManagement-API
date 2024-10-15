package com.appointment.management.domain.dto.user;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeDto(
        @NotBlank String password,
        @NotBlank String repeatedPassword
) {
}
