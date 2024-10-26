package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.CancellationSurchargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancellationSurchargeRepository extends JpaRepository<CancellationSurchargeEntity, Long> {
}
