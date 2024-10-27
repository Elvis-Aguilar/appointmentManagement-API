package com.appointment.management.domain.dto.report;

public record ServiceItemDto(
        String name,
        String price,
        String duration,
        String description,
        String status,
        Integer citas
) {
}
