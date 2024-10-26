package com.appointment.management.domain.dto.appoinment;

import com.appointment.management.persistance.enums.StatusCancellation;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.time.LocalDateTime;

public record CancellationSurchargeDto(
        @Positive Long id,

        @NonNull @Positive
        Long appointment,

        @NonNull
        LocalDateTime date,

        @NonNull @Positive
        Long customer,

        @NonNull
        StatusCancellation status
) {
}
