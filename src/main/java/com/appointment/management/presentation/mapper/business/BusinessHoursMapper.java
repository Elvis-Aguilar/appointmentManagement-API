package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.presentation.mapper.helpers.BusinessConfigurationMapperHelper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = BusinessConfigurationMapperHelper.class)
public interface BusinessHoursMapper {

    @Mapping(target = "createdAt", ignore = true)
    BusinessHoursEntity toEntity(BusinessHoursDto dto);

    @InheritInverseConfiguration(name = "toEntity")
    @Mapping(target = "createdAt", source = "createdAt")
    BusinessHoursDto toDto(BusinessHoursEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(BusinessHoursDto dto, @MappingTarget BusinessHoursEntity entity);
}
