package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.presentation.mapper.helpers.UserMapperHelper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapperHelper.class)
public interface BusinessConfigurationMapper {

    @Mapping(target = "businessHours", ignore = true)
    @Mapping(target = "admin", source = "admin")
    @Mapping(target = "createdAt", ignore = true)
    BusinessConfigurationEntity toEntity(BusinessConfigurationDto dto);

    @InheritInverseConfiguration
    BusinessConfigurationDto toDto(BusinessConfigurationEntity entity);

}