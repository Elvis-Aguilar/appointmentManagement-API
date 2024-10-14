package com.appointment.management.domain.service.business;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.domain.dto.business.EmployeeAvailabilityDto;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.entity.EmployeeAvailabilityEntity;
import com.appointment.management.persistance.repository.EmployeeAvailabilityRepository;
import com.appointment.management.presentation.mapper.business.EmployeeAvailabilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeAvailabilityService {

    private final EmployeeAvailabilityRepository employeeAvailabilityRepository;
    private final EmployeeAvailabilityMapper employeeAvailabilityMapper;

    public List<EmployeeAvailabilityDto> getAllAvailabilities() {
        return employeeAvailabilityRepository.findAll().stream()
                .map(employeeAvailabilityMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EmployeeAvailabilityDto> getAvailabilitiesByEmployeeId(Long employeeId) {
        return employeeAvailabilityRepository.findAllByEmployeeId(employeeId).stream()
                .map(employeeAvailabilityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeeAvailabilityDto createAvailability(EmployeeAvailabilityDto dto) {
        EmployeeAvailabilityEntity entity = employeeAvailabilityMapper.toEntity(dto);
        EmployeeAvailabilityEntity savedEntity = employeeAvailabilityRepository.save(entity);
        return employeeAvailabilityMapper.toDto(savedEntity);
    }

    @Transactional
    public List<EmployeeAvailabilityDto> createAllList(List<EmployeeAvailabilityDto> dtoList) {
        List<EmployeeAvailabilityEntity> entities = dtoList.stream().map(this.employeeAvailabilityMapper::toEntity).toList();
        return this.employeeAvailabilityRepository
                .saveAll(entities)
                .stream()
                .map(this.employeeAvailabilityMapper::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeAvailabilityDto updateAvailability(Long id, EmployeeAvailabilityDto dto) {
        EmployeeAvailabilityEntity entity = this.employeeAvailabilityRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Availability not found with id: " + id));
        this.employeeAvailabilityMapper.updateEntityFromDto(dto, entity);
        EmployeeAvailabilityEntity updatedEntity = this.employeeAvailabilityRepository.save(entity);
        return this.employeeAvailabilityMapper.toDto(updatedEntity);
    }

    public void deleteAvailability(Long id) {
        EmployeeAvailabilityEntity entity = employeeAvailabilityRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Availability not found with id: " + id));
        employeeAvailabilityRepository.delete(entity);
    }

}
