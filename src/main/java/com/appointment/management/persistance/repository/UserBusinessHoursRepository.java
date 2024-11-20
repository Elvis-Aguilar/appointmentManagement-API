package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.UserBusinessHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBusinessHoursRepository extends JpaRepository<UserBusinessHoursEntity, Long> {
    void deleteAllByBusinessHoursId(long userId);
    List<UserBusinessHoursEntity> findAllByBusinessHoursId(Long userId);
}
