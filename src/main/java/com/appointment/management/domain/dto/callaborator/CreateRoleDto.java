package com.appointment.management.domain.dto.callaborator;

import java.util.List;

public record CreateRoleDto(
        String name,
        String description,
        List<Long> permissions

) {
}
