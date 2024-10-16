package com.appointment.management.domain.service.business;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.application.exception.ResourceNotFoundException;
import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import com.appointment.management.persistance.repository.ServiceRepository;
import com.appointment.management.presentation.mapper.business.ServiceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceService serviceService;

    private ServiceEntity serviceEntity;
    private ServiceDto serviceDto;
    private ServiceDto serviceDto1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.serviceDto = new ServiceDto(1L,"Corete de Cabello",
                BigDecimal.valueOf(100.00), LocalTime.of(9, 0),
                "description",4,"zona 0","fadfafadf","AVAILABLE");

        serviceDto1 = new ServiceDto(1L,"Corete de Cabello",
                BigDecimal.valueOf(100.00), LocalTime.of(9, 0),
                "description",4,"zona 0","fadfafadf","UNAVAILABLE");

        serviceEntity = new ServiceEntity();
        serviceEntity.setId(1L);
        serviceEntity.setName("Corete de Cabello");
        serviceEntity.setDescription("description");
    }

    /*test para getAllServices*/
    @Test
    void shouldReturnAllServicesSuccessfully() {
        when(serviceRepository.findAll()).thenReturn(List.of(serviceEntity));

        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        List<ServiceDto> result = serviceService.getAllServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDto, result.getFirst());

        verify(serviceRepository, times(1)).findAll();
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoServicesFound() {
        when(serviceRepository.findAll()).thenReturn(Collections.emptyList());

        List<ServiceDto> result = serviceService.getAllServices();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(serviceRepository, times(1)).findAll();
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleMultipleServices() {
        ServiceEntity secondServiceEntity = new ServiceEntity();
        secondServiceEntity.setId(2L);
        secondServiceEntity.setName("Corte de Barba");

        ServiceDto secondServiceDto = new ServiceDto(
                2L,
                "Corte de Barba",
                BigDecimal.valueOf(50.00),
                LocalTime.of(10, 0),
                "description",
                2,
                "zona 1",
                "fadfafadf",
                "AVAILABLE"
        );

        when(serviceRepository.findAll()).thenReturn(List.of(serviceEntity, secondServiceEntity));

        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);
        when(serviceMapper.toDto(secondServiceEntity)).thenReturn(secondServiceDto);

        List<ServiceDto> result = serviceService.getAllServices();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(serviceDto, result.get(0));
        assertEquals(secondServiceDto, result.get(1));

        verify(serviceRepository, times(1)).findAll();
        verify(serviceMapper, times(1)).toDto(serviceEntity);
        verify(serviceMapper, times(1)).toDto(secondServiceEntity);
    }

    /*Test para getAllServicesAvailable*/
    @Test
    void shouldReturnServicesAvailableSuccessfully() {
        this.serviceEntity.setStatus(StatusBusinessHours.AVAILABLE);
        when(serviceRepository.findAllByStatus(StatusBusinessHours.AVAILABLE)).thenReturn(List.of(serviceEntity));

        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        List<ServiceDto> result = serviceService.getAllServicesAvailable();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDto, result.getFirst());
        assertEquals(StatusBusinessHours.AVAILABLE.toString(), result.getFirst().status());

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.AVAILABLE);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoServicesAvailableFound() {
        when(serviceRepository.findAllByStatus(StatusBusinessHours.AVAILABLE)).thenReturn(Collections.emptyList());

        List<ServiceDto> result = serviceService.getAllServicesAvailable();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.AVAILABLE);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleMultipleServicesAvailable() {
        ServiceEntity secondServiceEntity = new ServiceEntity();
        secondServiceEntity.setId(2L);
        secondServiceEntity.setName("Corte de Barba");

        ServiceDto secondServiceDto = new ServiceDto(
                2L,
                "Corte de Barba",
                BigDecimal.valueOf(50.00),
                LocalTime.of(10, 0),
                "description",
                2,
                "zona 1",
                "fadfafadf",
                "AVAILABLE"
        );

        when(serviceRepository.findAllByStatus(StatusBusinessHours.AVAILABLE)).thenReturn(List.of(serviceEntity, secondServiceEntity));

        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);
        when(serviceMapper.toDto(secondServiceEntity)).thenReturn(secondServiceDto);

        List<ServiceDto> result = serviceService.getAllServicesAvailable();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(serviceDto, result.get(0));
        assertEquals(secondServiceDto, result.get(1));

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.AVAILABLE);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
        verify(serviceMapper, times(1)).toDto(secondServiceEntity);
    }

    /*Test para getAllServicesUnavailable*/
    @Test
    void shouldReturnServicesUnavailableSuccessfully() {


        when(serviceRepository.findAllByStatus(StatusBusinessHours.UNAVAILABLE)).thenReturn(List.of(serviceEntity));

        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto1);

        List<ServiceDto> result = serviceService.getAllServicesUnavailable();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDto1.id(), result.getFirst().id());
        assertEquals(StatusBusinessHours.UNAVAILABLE.toString(), result.getFirst().status());

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.UNAVAILABLE);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoServicesUnavailableFound() {
        when(serviceRepository.findAllByStatus(StatusBusinessHours.UNAVAILABLE)).thenReturn(Collections.emptyList());

        List<ServiceDto> result = serviceService.getAllServicesUnavailable();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.UNAVAILABLE);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleMultipleServicesUnavailable() {
        ServiceEntity secondServiceEntity = new ServiceEntity();
        secondServiceEntity.setId(2L);
        secondServiceEntity.setName("Corte de Barba");

        ServiceDto secondServiceDto = new ServiceDto(
                2L,
                "Corte de Barba",
                BigDecimal.valueOf(50.00),
                LocalTime.of(10, 0),
                "description",
                2,
                "zona 1",
                "fadfafadf",
                "UNAVAILABLE"
        );

        when(serviceRepository.findAllByStatus(StatusBusinessHours.UNAVAILABLE)).thenReturn(List.of(serviceEntity, secondServiceEntity));

        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto1);
        when(serviceMapper.toDto(secondServiceEntity)).thenReturn(secondServiceDto);

        List<ServiceDto> result = serviceService.getAllServicesUnavailable();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(serviceDto1, result.get(0));
        assertEquals(secondServiceDto, result.get(1));

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.UNAVAILABLE);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
        verify(serviceMapper, times(1)).toDto(secondServiceEntity);
    }

    /*test para getServiceById*/
    @Test
    void shouldReturnServiceByIdSuccessfully() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        ServiceDto result = serviceService.getServiceById(1L);

        assertNotNull(result);
        assertEquals(serviceDto, result);

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenServiceNotFound() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            serviceService.getServiceById(1L);
        });

        assertEquals("Service not found with id: 1", exception.getMessage());

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    /*test para createService*/
    @Test
    void shouldCreateServiceSuccessfully() {
        when(serviceMapper.toEntity(serviceDto)).thenReturn(serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenReturn(serviceEntity);
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        ServiceDto result = serviceService.createService(serviceDto);

        assertNotNull(result);
        assertEquals(serviceDto, result);

        verify(serviceMapper, times(1)).toEntity(serviceDto);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenSavingFails() {
        when(serviceMapper.toEntity(serviceDto)).thenReturn(serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenThrow(new RuntimeException("Error al guardar"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.createService(serviceDto);
        });

        assertEquals("Error al guardar", exception.getMessage());

        verify(serviceMapper, times(1)).toEntity(serviceDto);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    /*test para updateService*/
    @Test
    void shouldUpdateServiceSuccessfully() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        doNothing().when(serviceMapper).updateEntityFromDto(serviceDto, serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenReturn(serviceEntity);
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        ServiceDto result = serviceService.updateService(1L, serviceDto);

        assertNotNull(result);
        assertEquals(serviceDto, result);

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, times(1)).updateEntityFromDto(serviceDto, serviceEntity);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenUpdateServiceNotFound() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            serviceService.updateService(1L, serviceDto);
        });

        assertEquals("Service not found with id: 1", exception.getMessage());

        verify(serviceMapper, never()).updateEntityFromDto(any(ServiceDto.class), any(ServiceEntity.class));
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleErrorWhenSavingFails() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        doNothing().when(serviceMapper).updateEntityFromDto(serviceDto, serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenThrow(new RuntimeException("Error al actualizar"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.updateService(1L, serviceDto);
        });

        assertEquals("Error al actualizar", exception.getMessage());

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, times(1)).updateEntityFromDto(serviceDto, serviceEntity);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

    /*test para updateServiceStatus*/
    @Test
    void shouldUpdateServiceStatusSuccessfully() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(serviceEntity)).thenReturn(serviceEntity);
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        ServiceDto result = serviceService.updateServiceStatus(1L, "UNAVAILABLE");

        assertNotNull(result);
        assertEquals("UNAVAILABLE", serviceEntity.getStatus().name());

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenUpdateStatsServiceNotFound() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            serviceService.updateServiceStatus(1L, "AVAILABLE");
        });

        assertEquals("Service not found with id: 1", exception.getMessage());

        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            serviceService.updateServiceStatus(1L, "INVALID_STATUS");
        });

        assertEquals("Invalid status value: INVALID_STATUS", exception.getMessage());

        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleErrorWhenSavingFailsUpdateServiceStatus() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(serviceEntity)).thenThrow(new RuntimeException("Error al actualizar estado"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.updateServiceStatus(1L, "AVAILABLE");
        });

        assertEquals("Error al actualizar estado", exception.getMessage());

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

    /*teste para deleteService*/

    @Test
    void shouldSoftDeleteServiceSuccessfully() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        serviceService.deleteService(1L);

        assertEquals(StatusBusinessHours.DELETED, serviceEntity.getStatus());
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenServiceDeleteNotFound() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            serviceService.deleteService(1L);
        });

        assertEquals("Service not found with id: 1", exception.getMessage());

        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleErrorWhenSavingFailsDelete() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        doThrow(new RuntimeException("Error al guardar")).when(serviceRepository).save(serviceEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.deleteService(1L);
        });

        assertEquals("Error al guardar", exception.getMessage());

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

}