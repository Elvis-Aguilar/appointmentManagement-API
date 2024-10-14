package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.EmployeeAvailabilityDto;
import com.appointment.management.persistance.entity.EmployeeAvailabilityEntity;
import com.appointment.management.presentation.mapper.helpers.UserMapperHelper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = UserMapperHelper.class)
public interface EmployeeAvailabilityMapper {

    @Mapping(target = "createdAt", ignore = true)
    EmployeeAvailabilityEntity toEntity(EmployeeAvailabilityDto dto);

    @InheritInverseConfiguration(name = "toEntity")
    @Mapping(target = "createdAt", source = "createdAt")
    EmployeeAvailabilityDto toDto(EmployeeAvailabilityEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(EmployeeAvailabilityDto dto, @MappingTarget EmployeeAvailabilityEntity entity);
}
