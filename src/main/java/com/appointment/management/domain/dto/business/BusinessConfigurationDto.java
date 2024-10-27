package com.appointment.management.domain.dto.business;

import com.appointment.management.persistance.enums.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BusinessConfigurationDto(
        @Positive Long id,
        @NotBlank String name,
        @NotBlank String logoUrl,
        @NonNull @Positive Long admin,
        LocalDateTime createdAt,
        @NotBlank String description,
        @NotBlank String businessType,
        @NonNull @PositiveOrZero Integer maxDaysCancellation,
        @NonNull @PositiveOrZero Integer maxHoursCancellation,
        @NonNull @PositiveOrZero BigDecimal cancellationSurcharge,
        @NonNull @PositiveOrZero Integer maxDaysUpdate,
        @NonNull @PositiveOrZero BigDecimal maxHoursUpdate,
        boolean employeeElection
) {
    public BusinessConfigurationDto(String name, String logoUrl) {
        this(null, name, logoUrl, 1L, LocalDateTime.now(), "Default description", "Default business type",
                7, 24, BigDecimal.ZERO, 7, BigDecimal.ZERO, false);
    }
}
