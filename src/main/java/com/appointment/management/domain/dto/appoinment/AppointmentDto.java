package com.appointment.management.domain.dto.appoinment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.time.LocalDateTime;

public record AppointmentDto(
        @Positive Long id,
        @NonNull @Positive
        Long customer,
        @Positive()
        Long service,
        @Positive()
        Long employeeId,
        @NonNull
        LocalDateTime startDate,
        @NonNull
        LocalDateTime endDate,
        @NotBlank
        String status,
        @NotBlank
        String paymentMethod
) {}
