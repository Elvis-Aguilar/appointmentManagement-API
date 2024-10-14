package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.EmployeeAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeAvailabilityRepository extends JpaRepository<EmployeeAvailabilityEntity, Long> {
    List<EmployeeAvailabilityEntity> findAllByEmployeeId(Long employeeId);
}
