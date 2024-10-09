package com.appointment.management.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;


public record SignUpDto(
        @NotBlank String name,
        @NotBlank String cui,
        @NotBlank String email,
        @NotBlank String phone,
        @NotBlank String nit,
        @NotBlank String password
) {
}
