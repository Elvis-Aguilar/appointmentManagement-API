package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.BusinessHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessHoursRepository extends JpaRepository<BusinessHoursEntity, Long> {
}
