package com.appointment.management.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordDto(
        @NotBlank String email
) {
}
