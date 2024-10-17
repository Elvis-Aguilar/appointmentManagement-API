//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.appointment.management.presentation.mapper.business;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.enums.BusinessType;
import com.appointment.management.persistance.enums.DayOfWeek;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import com.appointment.management.presentation.mapper.helpers.BusinessConfigurationMapperHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessHoursMapperImpl implements BusinessHoursMapper {
    @Autowired
    private BusinessConfigurationMapperHelper businessConfigurationMapperHelper;

    public BusinessHoursMapperImpl() {
    }

    private StatusBusinessHours getStatus(String status){
        try {
            return StatusBusinessHours.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid business Hours type: " + status);
        }
    }

    private DayOfWeek getDayWeek(String dayWeek) {
        try {
            return DayOfWeek.valueOf(dayWeek);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Day Week: " + dayWeek);
        }
    }

    private BusinessConfigurationEntity getBussines(Long id){
        BusinessConfigurationEntity entity = this.businessConfigurationMapperHelper.findById(id);
        if (entity == null) {
            throw new BadRequestException("Business Configuration not found with id: "+id);
        }
        return entity;
    }

    public BusinessHoursEntity toEntity(BusinessHoursDto dto) {
        if (dto == null) {
            return null;
        } else {
            BusinessHoursEntity businessHoursEntity = new BusinessHoursEntity();
            businessHoursEntity.setId(dto.id());
            businessHoursEntity.setBusiness(this.getBussines(dto.business()));
            businessHoursEntity.setSpecificDate(dto.specificDate());
            businessHoursEntity.setOpeningTime(dto.openingTime());
            businessHoursEntity.setClosingTime(dto.closingTime());
            businessHoursEntity.setStatus(this.getStatus(dto.status()));
            businessHoursEntity.setAvailableWorkers(dto.availableWorkers());
            businessHoursEntity.setAvailableAreas(dto.availableAreas());
            businessHoursEntity.setDayOfWeek(this.getDayWeek(dto.dayOfWeek()));
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
            dayOfWeek = entity.getDayOfWeek().name();
            specificDate = entity.getSpecificDate();
            openingTime = entity.getOpeningTime();
            closingTime = entity.getClosingTime();
            status = entity.getStatus().name();
            availableWorkers = entity.getAvailableWorkers();
            availableAreas = entity.getAvailableAreas();
            return new BusinessHoursDto(id, business, dayOfWeek, specificDate, openingTime, closingTime, createdAt, status, availableWorkers, availableAreas);
        }
    }

    public void updateEntityFromDto(BusinessHoursDto dto, BusinessHoursEntity entity) {
        if (dto != null) {
            entity.setBusiness(this.businessConfigurationMapperHelper.findById(dto.business()));
            entity.setDayOfWeek(this.getDayWeek(dto.dayOfWeek()));
            entity.setSpecificDate(dto.specificDate());
            entity.setOpeningTime(dto.openingTime());
            entity.setClosingTime(dto.closingTime());
            entity.setStatus(this.getStatus(dto.status()));
            entity.setAvailableWorkers(dto.availableWorkers());
            entity.setAvailableAreas(dto.availableAreas());
        }
    }
}
