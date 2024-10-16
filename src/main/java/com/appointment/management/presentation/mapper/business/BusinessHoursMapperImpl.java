//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.enums.DayOfWeek;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import com.appointment.management.presentation.mapper.helpers.BusinessConfigurationMapperHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessHoursMapperImpl implements BusinessHoursMapper {
    @Autowired
    private BusinessConfigurationMapperHelper businessConfigurationMapperHelper;

    public BusinessHoursMapperImpl() {
    }

    public BusinessHoursEntity toEntity(BusinessHoursDto dto) {
        if (dto == null) {
            return null;
        } else {
            BusinessHoursEntity businessHoursEntity = new BusinessHoursEntity();
            businessHoursEntity.setId(dto.id());
            businessHoursEntity.setBusiness(this.businessConfigurationMapperHelper.findById(dto.business()));
            if (dto.dayOfWeek() != null) {
                businessHoursEntity.setDayOfWeek((DayOfWeek)Enum.valueOf(DayOfWeek.class, dto.dayOfWeek()));
            }

            businessHoursEntity.setSpecificDate(dto.specificDate());
            businessHoursEntity.setOpeningTime(dto.openingTime());
            businessHoursEntity.setClosingTime(dto.closingTime());
            if (dto.status() != null) {
                businessHoursEntity.setStatus((StatusBusinessHours)Enum.valueOf(StatusBusinessHours.class, dto.status()));
            }

            businessHoursEntity.setAvailableWorkers(dto.availableWorkers());
            businessHoursEntity.setAvailableAreas(dto.availableAreas());
            return businessHoursEntity;
        }
    }

    public BusinessHoursDto toDto(BusinessHoursEntity entity) {
        if (entity == null) {
            return null;
        } else {
            LocalDateTime createdAt = null;
            Long id = null;
            Long business = null;
            String dayOfWeek = null;
            LocalDate specificDate = null;
            LocalTime openingTime = null;
            LocalTime closingTime = null;
            String status = null;
            Integer availableWorkers = null;
            Integer availableAreas = null;
            createdAt = entity.getCreatedAt();
            id = entity.getId();
            business = this.businessConfigurationMapperHelper.toId(entity.getBusiness());
            if (entity.getDayOfWeek() != null) {
                dayOfWeek = entity.getDayOfWeek().name();
            }

            specificDate = entity.getSpecificDate();
            openingTime = entity.getOpeningTime();
            closingTime = entity.getClosingTime();
            if (entity.getStatus() != null) {
                status = entity.getStatus().name();
            }

            availableWorkers = entity.getAvailableWorkers();
            availableAreas = entity.getAvailableAreas();
            BusinessHoursDto businessHoursDto = new BusinessHoursDto(id, business, dayOfWeek, specificDate, openingTime, closingTime, createdAt, status, availableWorkers, availableAreas);
            return businessHoursDto;
        }
    }

    public void updateEntityFromDto(BusinessHoursDto dto, BusinessHoursEntity entity) {
        if (dto != null) {
            entity.setBusiness(this.businessConfigurationMapperHelper.findById(dto.business()));
            if (dto.dayOfWeek() != null) {
                entity.setDayOfWeek((DayOfWeek)Enum.valueOf(DayOfWeek.class, dto.dayOfWeek()));
            } else {
                entity.setDayOfWeek((DayOfWeek)null);
            }

            entity.setSpecificDate(dto.specificDate());
            entity.setOpeningTime(dto.openingTime());
            entity.setClosingTime(dto.closingTime());
            if (dto.status() != null) {
                entity.setStatus((StatusBusinessHours)Enum.valueOf(StatusBusinessHours.class, dto.status()));
            } else {
                entity.setStatus((StatusBusinessHours)null);
            }

            entity.setAvailableWorkers(dto.availableWorkers());
            entity.setAvailableAreas(dto.availableAreas());
        }
    }
}
