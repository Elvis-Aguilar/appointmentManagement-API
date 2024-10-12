package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ServiceRepository extends JpaRepository<ServiceEntity,Long> {

    List<ServiceEntity> findAllByStatus(StatusBusinessHours status);
}
