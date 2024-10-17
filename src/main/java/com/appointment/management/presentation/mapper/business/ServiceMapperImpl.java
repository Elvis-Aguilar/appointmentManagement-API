//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapperImpl implements ServiceMapper {
    public ServiceMapperImpl() {
    }

    public ServiceEntity toEntity(ServiceDto dto) {
        if (dto == null) {
            return null;
        } else {
            ServiceEntity serviceEntity = new ServiceEntity();
            serviceEntity.setId(dto.id());
            serviceEntity.setName(dto.name());
            serviceEntity.setPrice(dto.price());
            serviceEntity.setDuration(dto.duration());
            serviceEntity.setDescription(dto.description());
            serviceEntity.setPeopleReaches(dto.peopleReaches());
            serviceEntity.setLocation(dto.location());
            serviceEntity.setImageUrl(dto.imageUrl());
            return serviceEntity;
        }
    }

    public ServiceDto toDto(ServiceEntity entity) {
        if (entity == null) {
            return null;
        } else {
            Long id = entity.getId();
            String name = entity.getName();
            BigDecimal  price = entity.getPrice();
            LocalTime duration = entity.getDuration();
            String description = entity.getDescription();
            Integer peopleReaches = entity.getPeopleReaches();
            String location = entity.getLocation();
            String imageUrl = entity.getImageUrl();
            String status = entity.getStatus().name();
            return new ServiceDto(id, name, price, duration, description, peopleReaches, location, imageUrl, status);
        }
    }

    public void updateEntityFromDto(ServiceDto dto, ServiceEntity entity) {
        if (dto != null) {
            entity.setName(dto.name());
            entity.setPrice(dto.price());
            entity.setDuration(dto.duration());
            entity.setDescription(dto.description());
            entity.setPeopleReaches(dto.peopleReaches());
            entity.setLocation(dto.location());
            entity.setImageUrl(dto.imageUrl());
            if (dto.status() != null) {
                entity.setStatus((StatusBusinessHours)Enum.valueOf(StatusBusinessHours.class, dto.status()));
            } else {
                entity.setStatus((StatusBusinessHours)null);
            }

        }
    }
}


