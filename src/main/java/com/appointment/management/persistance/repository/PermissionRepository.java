package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    Set<PermissionEntity> findAllByNameIn(Iterable<String> names);
}