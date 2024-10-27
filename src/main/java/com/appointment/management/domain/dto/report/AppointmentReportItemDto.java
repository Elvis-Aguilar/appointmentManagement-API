package com.appointment.management.domain.dto.report;

public record AppointmentReportItemDto(
        String fecha,
        String horaInicio,
        String cliente,
        String estado,
        String servicio,
        String empleado,
        Integer price
) {
}
