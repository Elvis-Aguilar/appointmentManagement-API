package com.appointment.management.domain.dto.report;

import java.util.List;

public record clietnReportSendDto(
        List<clienteReportItemDto> items,
        String rangeDate,
        String filtro
) {
}
