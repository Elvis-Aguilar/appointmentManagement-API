//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.appointment.management.presentation.mapper.business;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.business.EmployeeAvailabilityDto;
import com.appointment.management.persistance.entity.EmployeeAvailabilityEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.DayOfWeek;
import com.appointment.management.presentation.mapper.helpers.UserMapperHelper;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeAvailabilityMapperImpl implements EmployeeAvailabilityMapper {
    @Autowired
    private UserMapperHelper userMapperHelper;

    public EmployeeAvailabilityMapperImpl() {
    }

    private UserEntity getUser(Long id) {
        UserEntity admin = this.userMapperHelper.findById(id);
        if (admin == null) {
            throw new BadRequestException("Admin not found with id: "+id);
        }
        return admin;
    }

    private DayOfWeek getDayWeek(String dayWeek) {
        try {
            return DayOfWeek.valueOf(dayWeek);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Day Week: " + dayWeek);
        }
    }

    public EmployeeAvailabilityEntity toEntity(EmployeeAvailabilityDto dto) {
        if (dto == null) {
            return null;
        } else {
            EmployeeAvailabilityEntity employeeAvailabilityEntity = new EmployeeAvailabilityEntity();
            employeeAvailabilityEntity.setId(dto.id());
            employeeAvailabilityEntity.setEmployee(this.getUser(dto.employee()));
            employeeAvailabilityEntity.setDayOfWeek(this.getDayWeek(dto.dayOfWeek()));
            employeeAvailabilityEntity.setStartTime(dto.startTime());
            employeeAvailabilityEntity.setEndTime(dto.endTime());
            return employeeAvailabilityEntity;
        }
    }

    public EmployeeAvailabilityDto toDto(EmployeeAvailabilityEntity entity) {
        if (entity == null) {
            return null;
        } else {
            LocalDateTime createdAt = null;
            Long id = null;
            Long employee = null;
            String dayOfWeek = null;
            LocalTime startTime = null;
            LocalTime endTime = null;
            createdAt = entity.getCreatedAt();
            id = entity.getId();
            employee = this.userMapperHelper.toId(entity.getEmployee());
            dayOfWeek = entity.getDayOfWeek().name();
            startTime = entity.getStartTime();
            endTime = entity.getEndTime();
            return new EmployeeAvailabilityDto(id, employee, dayOfWeek, startTime, endTime, createdAt);
        }
    }

    public void updateEntityFromDto(EmployeeAvailabilityDto dto, EmployeeAvailabilityEntity entity) {
        if (dto != null) {
            entity.setEmployee(this.getUser(dto.employee()));
            entity.setDayOfWeek(this.getDayWeek(dto.dayOfWeek()));
            entity.setStartTime(dto.startTime());
            entity.setEndTime(dto.endTime());
        }
    }
}
