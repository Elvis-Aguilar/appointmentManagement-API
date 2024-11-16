package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.RolePermissionEntity;
import com.appointment.management.persistance.entity.UserPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {

    void deleteAllByRoleId(long userId);

    List<RolePermissionEntity> findAllByRoleId(Long roleId);

}
