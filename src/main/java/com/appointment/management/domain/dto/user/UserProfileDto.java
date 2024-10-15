package com.appointment.management.domain.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UserProfileDto(
        @Positive Long id,
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String nit,
        @NotBlank String cui,
        @NotBlank String phone,
        @NotBlank String imageUrl
) {
}
