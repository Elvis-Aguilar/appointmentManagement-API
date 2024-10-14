package com.appointment.management.domain.dto.business;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record EmployeeAvailabilityDto(
        @Positive Long id,
        @Positive Long employee,
        @NotBlank String dayOfWeek,
        @NonNull LocalTime startTime,
        @NonNull LocalTime endTime,
        LocalDateTime createdAt
) {
}
