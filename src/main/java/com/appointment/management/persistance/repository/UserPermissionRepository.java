package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.UserPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity, Long> {
    void deleteAllByUserId(long userId);
}
