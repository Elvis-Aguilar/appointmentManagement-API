//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.appointment.management.presentation.mapper.business;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.BusinessType;
import com.appointment.management.presentation.mapper.helpers.UserMapperHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessConfigurationMapperImpl implements BusinessConfigurationMapper {
    @Autowired
    private UserMapperHelper userMapperHelper;

    public BusinessConfigurationMapperImpl() {
    }

    private UserEntity getAdmin(Long id) {
        UserEntity admin = this.userMapperHelper.findById(id);
        if (admin == null) {
            throw new BadRequestException("Admin not found with id: "+id);
        }
        return admin;
    }

    private BusinessType getBusinessType(String status) {
        try {
           return BusinessType.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid business type: " + status);
        }
    }

    public BusinessConfigurationEntity toEntity(BusinessConfigurationDto dto) {
        if (dto == null) {
            return null;
        } else {
            BusinessConfigurationEntity businessConfigurationEntity = new BusinessConfigurationEntity();
            businessConfigurationEntity.setAdmin(this.getAdmin(dto.admin()));
            businessConfigurationEntity.setId(dto.id());
            businessConfigurationEntity.setName(dto.name());
            businessConfigurationEntity.setLogoUrl(dto.logoUrl());
            businessConfigurationEntity.setDescription(dto.description());
            businessConfigurationEntity.setBusinessType(this.getBusinessType(dto.businessType()));
            businessConfigurationEntity.setMaxDaysCancellation(dto.maxDaysCancellation());
            businessConfigurationEntity.setMaxHoursCancellation(dto.maxHoursCancellation());
            businessConfigurationEntity.setCancellationSurcharge(dto.cancellationSurcharge());
            businessConfigurationEntity.setMaxDaysUpdate(dto.maxDaysUpdate());
            businessConfigurationEntity.setMaxHoursUpdate(dto.maxHoursUpdate());
            businessConfigurationEntity.setEmployeeElection(dto.employeeElection());
            return businessConfigurationEntity;
        }
    }


    public BusinessConfigurationDto toDto(BusinessConfigurationEntity entity) {
        if (entity == null) {
            return null;
        } else {
            Long admin = null;
            Long id = null;
            String name = null;
            String logoUrl = null;
            String description = null;
            String businessType = null;
            Integer maxDaysCancellation = null;
            Integer maxHoursCancellation = null;
            BigDecimal cancellationSurcharge = null;
            Integer maxDaysUpdate = null;
            BigDecimal maxHoursUpdate = null;
            admin = this.userMapperHelper.toId(entity.getAdmin());
            id = entity.getId();
            name = entity.getName();
            logoUrl = entity.getLogoUrl();
            description = entity.getDescription();
            businessType = entity.getBusinessType().name();
            maxDaysCancellation = entity.getMaxDaysCancellation();
            maxHoursCancellation = entity.getMaxHoursCancellation();
            cancellationSurcharge = entity.getCancellationSurcharge();
            maxDaysUpdate = entity.getMaxDaysUpdate();
            maxHoursUpdate = entity.getMaxHoursUpdate();
            LocalDateTime createdAt = entity.getCreatedAt();
            return new BusinessConfigurationDto(id, name, logoUrl, admin, (LocalDateTime)createdAt, description, businessType, maxDaysCancellation, maxHoursCancellation, cancellationSurcharge, maxDaysUpdate, maxHoursUpdate, entity.isEmployeeElection());
        }
    }
}
