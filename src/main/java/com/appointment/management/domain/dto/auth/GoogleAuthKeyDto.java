package com.appointment.management.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthKeyDto(
        @NotBlank String authKey,
        @NotBlank Integer code) {
}
