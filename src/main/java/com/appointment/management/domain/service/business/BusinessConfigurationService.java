package com.appointment.management.domain.service.business;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.repository.BusinessConfigurationRepository;
import com.appointment.management.presentation.mapper.business.BusinessConfigurationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


@Service
public class BusinessConfigurationService {

    @Autowired
    private BusinessConfigurationRepository businessConfigurationRepository;

    @Autowired
    private BusinessConfigurationMapper businessConfigurationMapper;

    @Transactional
    public BusinessConfigurationDto save(BusinessConfigurationDto businessConfigurationDto) {
        BusinessConfigurationEntity businessConfigurationEntity = businessConfigurationMapper.toEntity(businessConfigurationDto);
        BusinessConfigurationEntity savedEntity = businessConfigurationRepository.save(businessConfigurationEntity);
        return businessConfigurationMapper.toDto(savedEntity);
    }


    public BusinessConfigurationDto findById(Long id) {
        Optional<BusinessConfigurationEntity> businessConfig = businessConfigurationRepository.findById(id);
        return businessConfig.map(businessConfigurationMapper::toDto)
                .orElseThrow(() -> new ValueNotFoundException("Business configuration not found with ID: " + id));
    }

    public BusinessConfigurationDto findFirst() {
        Optional<BusinessConfigurationEntity> businessConfig = businessConfigurationRepository.findFirstByOrderByIdAsc();
        return businessConfig.map(businessConfigurationMapper::toDto)
                .orElseThrow(() -> new ValueNotFoundException("No business configuration found"));
    }


    @Transactional
    public BusinessConfigurationDto update(Long id, BusinessConfigurationDto dto) {
        BusinessConfigurationEntity existingEntity = businessConfigurationRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Configuracion del negocio no encontradas con el Id: " + id));
        BusinessConfigurationEntity update = businessConfigurationMapper.toEntity(dto);
        update.setId(id);
        // Guardamos los cambios
        BusinessConfigurationEntity updatedEntity = businessConfigurationRepository.save(update);
        updatedEntity.setCreatedAt(existingEntity.getCreatedAt());
        return businessConfigurationMapper.toDto(updatedEntity);
    }

}
