//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.EmployeeAvailabilityDto;
import com.appointment.management.persistance.entity.EmployeeAvailabilityEntity;
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

    public EmployeeAvailabilityEntity toEntity(EmployeeAvailabilityDto dto) {
        if (dto == null) {
            return null;
        } else {
            EmployeeAvailabilityEntity employeeAvailabilityEntity = new EmployeeAvailabilityEntity();
            employeeAvailabilityEntity.setId(dto.id());
            employeeAvailabilityEntity.setEmployee(this.userMapperHelper.findById(dto.employee()));
            if (dto.dayOfWeek() != null) {
                employeeAvailabilityEntity.setDayOfWeek((DayOfWeek)Enum.valueOf(DayOfWeek.class, dto.dayOfWeek()));
            }

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
            if (entity.getDayOfWeek() != null) {
                dayOfWeek = entity.getDayOfWeek().name();
            }

            startTime = entity.getStartTime();
            endTime = entity.getEndTime();
            EmployeeAvailabilityDto employeeAvailabilityDto = new EmployeeAvailabilityDto(id, employee, dayOfWeek, startTime, endTime, createdAt);
            return employeeAvailabilityDto;
        }
    }

    public void updateEntityFromDto(EmployeeAvailabilityDto dto, EmployeeAvailabilityEntity entity) {
        if (dto != null) {
            entity.setEmployee(this.userMapperHelper.findById(dto.employee()));
            if (dto.dayOfWeek() != null) {
                entity.setDayOfWeek((DayOfWeek)Enum.valueOf(DayOfWeek.class, dto.dayOfWeek()));
            } else {
                entity.setDayOfWeek((DayOfWeek)null);
            }

            entity.setStartTime(dto.startTime());
            entity.setEndTime(dto.endTime());
        }
    }
}
