package com.appointment.management.domain.service.business;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.repository.ServiceRepository;
import com.appointment.management.presentation.mapper.business.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional(readOnly = true)
    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceDto getServiceById(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Service not found with id: " + id));
        return serviceMapper.toDto(entity);
    }

    @Transactional
    public ServiceDto createService(ServiceDto serviceDto) {
        ServiceEntity entity = serviceMapper.toEntity(serviceDto);
        System.out.println(entity.toString());
        ServiceEntity savedEntity = serviceRepository.save(entity);
        return serviceMapper.toDto(savedEntity);
    }

    @Transactional
    public ServiceDto updateService(Long id, ServiceDto serviceDto) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Service not found with id: " + id));
        this.serviceMapper.updateEntityFromDto(serviceDto, entity);
        ServiceEntity updatedEntity = serviceRepository.save(entity);
        return serviceMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ValueNotFoundException("Service not found with id: " + id);
        }
        serviceRepository.deleteById(id);
    }
}
