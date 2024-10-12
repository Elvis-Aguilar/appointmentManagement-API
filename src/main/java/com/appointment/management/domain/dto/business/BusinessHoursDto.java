package com.appointment.management.domain.dto.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BusinessHoursDto(
        Long id,
        @NonNull @Positive Long business,
        @NotBlank String dayOfWeek,
        LocalDate specificDate,
        @NonNull LocalTime openingTime,
        @NonNull LocalTime closingTime,
        LocalDateTime createdAt,
        @NotBlank String status,
        @NonNull @PositiveOrZero Integer availableWorkers,
        @NonNull @PositiveOrZero Integer availableAreas

) {
}
