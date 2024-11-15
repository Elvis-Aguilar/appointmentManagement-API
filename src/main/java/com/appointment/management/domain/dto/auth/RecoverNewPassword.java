package com.appointment.management.domain.dto.auth;

import lombok.NonNull;


public record RecoverNewPassword(
        @NonNull
        String newPassword
) {
}
