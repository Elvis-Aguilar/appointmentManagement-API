package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.UserPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity, Long> {
    void deleteAllByUserId(long userId);

    List<UserPermissionEntity> findAllByUserId(Long userId);
}
