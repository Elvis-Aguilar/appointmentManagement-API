//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.domain.dto.user.UserProfileDto;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.entity.UserEntity;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    public UserMapperImpl() {
    }

    public UserEntity toEntity(UserProfileDto dto) {
        if (dto == null) {
            return null;
        } else {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(dto.id());
            userEntity.setName(dto.name());
            userEntity.setCui(dto.cui());
            userEntity.setNit(dto.nit());
            userEntity.setEmail(dto.email());
            userEntity.setPhone(dto.phone());
            userEntity.setImageUrl(dto.imageUrl());
            return userEntity;
        }
    }

    public ServiceDto toDto(ServiceEntity entity) {
        if (entity == null) {
            return null;
        } else {
            Long id = null;
            String name = null;
            BigDecimal price = null;
            LocalTime duration = null;
            String description = null;
            Integer peopleReaches = null;
            String location = null;
            String imageUrl = null;
            String status = null;
            id = entity.getId();
            name = entity.getName();
            price = entity.getPrice();
            duration = entity.getDuration();
            description = entity.getDescription();
            peopleReaches = entity.getPeopleReaches();
            location = entity.getLocation();
            imageUrl = entity.getImageUrl();
            if (entity.getStatus() != null) {
                status = entity.getStatus().name();
            }

            ServiceDto serviceDto = new ServiceDto(id, name, price, duration, description, peopleReaches, location, imageUrl, status);
            return serviceDto;
        }
    }

    public void updateEntityFromDto(UserProfileDto dto, UserEntity entity) {
        if (dto != null) {
            entity.setName(dto.name());
            entity.setCui(dto.cui());
            entity.setNit(dto.nit());
            entity.setEmail(dto.email());
            entity.setPhone(dto.phone());
            entity.setImageUrl(dto.imageUrl());
        }
    }
}
