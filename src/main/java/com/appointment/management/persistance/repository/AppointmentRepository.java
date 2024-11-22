package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM AppointmentEntity a " +
            "WHERE a.employee.id = :employeeId " +
            "AND (a.startDate < :endDate AND a.endDate > :startDate)")
    boolean existsByEmployeeIdAndDateRange(@Param("employeeId") Long employeeId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    List<AppointmentEntity> findByEmployeeId(Long employeeId);

}