package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessConfigurationRepository extends JpaRepository<BusinessConfigurationEntity, Long> {
    Optional<BusinessConfigurationEntity> findFirstByOrderByIdAsc();
}
