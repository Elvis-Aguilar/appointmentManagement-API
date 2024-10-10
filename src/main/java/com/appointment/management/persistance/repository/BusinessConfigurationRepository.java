package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessConfigurationRepository extends JpaRepository<BusinessConfigurationEntity, Long> {

}
