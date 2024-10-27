package com.appointment.management.domain.dto.report;

import java.util.List;

public record ServiceSendDto(
        List<ServiceItemDto> items,
        Integer total,
        String rangeDate,
        String filtro
) {
}
