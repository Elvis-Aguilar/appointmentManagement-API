package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.BusinessHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BusinessHoursRepository extends JpaRepository<BusinessHoursEntity, Long> {

    List<BusinessHoursEntity> findBySpecificDateIsNull();

    List<BusinessHoursEntity> findBySpecificDateIsNotNull();

    List<BusinessHoursEntity> findBySpecificDateBetween(LocalDate startDate, LocalDate endDate);
}
