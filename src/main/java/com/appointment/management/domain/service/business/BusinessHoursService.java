package com.appointment.management.domain.service.business;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.repository.BusinessHoursRepository;
import com.appointment.management.presentation.mapper.business.BusinessHoursMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessHoursService {

    private final BusinessHoursRepository businessHoursRepository;
    private final BusinessHoursMapper businessHoursMapper;

    @Autowired
    public BusinessHoursService(BusinessHoursRepository businessHoursRepository, BusinessHoursMapper businessHoursMapper) {
        this.businessHoursRepository = businessHoursRepository;
        this.businessHoursMapper = businessHoursMapper;
    }

    @Transactional
    public BusinessHoursDto save(BusinessHoursDto dto) {
        BusinessHoursEntity entity = this.businessHoursMapper.toEntity(dto);
        BusinessHoursEntity savedEntity = this.businessHoursRepository.save(entity);
        return this.businessHoursMapper.toDto(savedEntity);
    }

    @Transactional
    public List<BusinessHoursDto> createAllList(List<BusinessHoursDto> dtoList) {
        if (dtoList.isEmpty()){
            return List.of();
        }
        List<BusinessHoursEntity> entities = dtoList.stream().map(this.businessHoursMapper::toEntity).toList();
         return this.businessHoursRepository
                 .saveAll(entities)
                 .stream()
                 .map(businessHoursMapper::toDto)
                 .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public BusinessHoursDto getById(Long id) {
        BusinessHoursEntity entity = businessHoursRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("BusinessHours not found with id: " + id));
        return businessHoursMapper.toDto(entity);
    }

    @Transactional
    public BusinessHoursDto update(Long id, BusinessHoursDto dto) {
        BusinessHoursEntity existingEntity = businessHoursRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("BusinessHours not found with id: " + id));

        // Mapear los cambios del DTO a la entidad existente
        businessHoursMapper.updateEntityFromDto(dto, existingEntity);

        BusinessHoursEntity updatedEntity = businessHoursRepository.save(existingEntity);
        return businessHoursMapper.toDto(updatedEntity);
    }

    @Transactional
    public void delete(Long id) {
        BusinessHoursEntity entity = businessHoursRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("BusinessHours not found with id: " + id));
        businessHoursRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<BusinessHoursDto> getAll() {
        List<BusinessHoursEntity> entities = businessHoursRepository.findAll();
        return entities.stream()
                .map(businessHoursMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessHoursDto> getAllWithNullSpecificDateIs() {
        List<BusinessHoursEntity> entities = businessHoursRepository.findBySpecificDateIsNull();
        return entities.stream()
                .map(businessHoursMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessHoursDto> getBusinessHoursInDateRange(LocalDate startDate, LocalDate endDate) {
        List<BusinessHoursEntity> entities = businessHoursRepository.findBySpecificDateBetween(startDate, endDate);
        return entities.stream()
                .map(businessHoursMapper::toDto)
                .collect(Collectors.toList());
    }


}
