package com.appointment.management.domain.dto.business;

import java.util.List;

public record UpdateUserBusinessHours(
        List<Long> users
) {
}
