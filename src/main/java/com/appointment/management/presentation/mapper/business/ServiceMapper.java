package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.persistance.entity.ServiceEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "appointments", ignore = true)
    ServiceEntity toEntity(ServiceDto dto);

    @InheritInverseConfiguration(name = "toEntity")
    ServiceDto toDto(ServiceEntity entity);

    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ServiceDto dto, @MappingTarget ServiceEntity entity);

}
