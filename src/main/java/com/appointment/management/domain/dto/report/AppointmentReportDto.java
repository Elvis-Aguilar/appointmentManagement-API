package com.appointment.management.domain.dto.report;

import java.util.List;

public record AppointmentReportDto(
        List<AppointmentReportItemDto> items,
        Integer total,
        String rangeDate,
        String filtro
) {
}
