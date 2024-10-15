package com.appointment.management.domain.dto.user;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(
        Long id,
        String name,
        String email,
        String nit,
        String cui,
        String phone,
        LocalDateTime createdAt,
        String imageUrl,
        Boolean hasMultiFactorAuth,
        String role,
        List<String> permissions
) {
}
