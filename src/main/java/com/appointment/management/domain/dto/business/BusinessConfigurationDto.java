package com.appointment.management.domain.dto.business;

import com.appointment.management.persistance.enums.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BusinessConfigurationDto(
        Long id,
        @NotBlank String name,
        @NotBlank String logoUrl,
        @NotBlank Long admin,
        LocalDateTime createdAt,
        @NotBlank String description,
        @NotBlank String businessType,
        @NotBlank @PositiveOrZero Integer maxDaysCancellation,
        @NotBlank @PositiveOrZero Integer maxHoursCancellation,
        @NotBlank @PositiveOrZero BigDecimal cancellationSurcharge,
        @NotBlank @PositiveOrZero Integer maxDaysUpdate,
        @NotBlank @PositiveOrZero BigDecimal maxHoursUpdate
) {
}
