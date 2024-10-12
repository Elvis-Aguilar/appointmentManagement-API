package com.appointment.management.domain.dto.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalTime;

public record ServiceDto(
        Long id,
        @NotBlank String name,
        @NonNull @PositiveOrZero BigDecimal price,
        @NonNull LocalTime duration,
        @NotBlank String description,
        Integer peopleReaches,
        String location,
        String imageUrl
) {
}
