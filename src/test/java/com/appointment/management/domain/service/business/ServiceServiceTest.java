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

    //Variable globales para el Given Global
    private ServiceEntity serviceEntity;
    private ServiceDto serviceDto;
    private ServiceDto serviceDto1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
        this.serviceDto = new ServiceDto(1L,"Corete de Cabello",
                BigDecimal.valueOf(100.00), LocalTime.of(9, 0),
                "description",4,"zona 0","fadfafadf","AVAILABLE");

        serviceDto1 = new ServiceDto(1L,"Corete de Cabello",
                BigDecimal.valueOf(100.00), LocalTime.of(9, 0),
                "description",4,"zona 0","fadfafadf","UNAVAILABLE");

        //Given Global
        serviceEntity = new ServiceEntity();
        serviceEntity.setId(1L);
        serviceEntity.setName("Corete de Cabello");
        serviceEntity.setDescription("description");
    }

    /*test para getAllServices*/
    @Test
    void shouldReturnAllServicesSuccessfully() {
        //When
        when(serviceRepository.findAll()).thenReturn(List.of(serviceEntity));
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServices();

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDto, result.getFirst());

        verify(serviceRepository, times(1)).findAll();
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoServicesFound() {
        //When
        when(serviceRepository.findAll()).thenReturn(Collections.emptyList());

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServices();

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serviceRepository, times(1)).findAll();
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleMultipleServices() {
        //Given
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

        //When
        when(serviceRepository.findAll()).thenReturn(List.of(serviceEntity, secondServiceEntity));
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);
        when(serviceMapper.toDto(secondServiceEntity)).thenReturn(secondServiceDto);

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServices();

        //Then
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
        //Given
        this.serviceEntity.setStatus(StatusBusinessHours.AVAILABLE);

        //When
        when(serviceRepository.findAllByStatus(StatusBusinessHours.AVAILABLE)).thenReturn(List.of(serviceEntity));
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServicesAvailable();

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDto, result.getFirst());
        assertEquals(StatusBusinessHours.AVAILABLE.toString(), result.getFirst().status());

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.AVAILABLE);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoServicesAvailableFound() {
        //When
        when(serviceRepository.findAllByStatus(StatusBusinessHours.AVAILABLE)).thenReturn(Collections.emptyList());

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServicesAvailable();

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.AVAILABLE);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleMultipleServicesAvailable() {
        //Given
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

        //When
        when(serviceRepository.findAllByStatus(StatusBusinessHours.AVAILABLE)).thenReturn(List.of(serviceEntity, secondServiceEntity));
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);
        when(serviceMapper.toDto(secondServiceEntity)).thenReturn(secondServiceDto);

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServicesAvailable();

        //Then
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

        //When
        when(serviceRepository.findAllByStatus(StatusBusinessHours.UNAVAILABLE)).thenReturn(List.of(serviceEntity));
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto1);

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServicesUnavailable();

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDto1.id(), result.getFirst().id());
        assertEquals(StatusBusinessHours.UNAVAILABLE.toString(), result.getFirst().status());

        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.UNAVAILABLE);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoServicesUnavailableFound() {
        //When
        when(serviceRepository.findAllByStatus(StatusBusinessHours.UNAVAILABLE)).thenReturn(Collections.emptyList());

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServicesUnavailable();

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serviceRepository, times(1)).findAllByStatus(StatusBusinessHours.UNAVAILABLE);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleMultipleServicesUnavailable() {
        //Given
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

        //When
        when(serviceRepository.findAllByStatus(StatusBusinessHours.UNAVAILABLE)).thenReturn(List.of(serviceEntity, secondServiceEntity));
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto1);
        when(serviceMapper.toDto(secondServiceEntity)).thenReturn(secondServiceDto);

        //Llamando a la funcion a testear
        List<ServiceDto> result = serviceService.getAllServicesUnavailable();

        //Then
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
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        //Llamando a la funcion a testear
        ServiceDto result = serviceService.getServiceById(1L);

        //Then
        assertNotNull(result);
        assertEquals(serviceDto, result);

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenServiceNotFound() {

        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            serviceService.getServiceById(1L);
        });

        //Then
        assertEquals("Service not found with id: 1", exception.getMessage());
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    /*test para createService*/
    @Test
    void shouldCreateServiceSuccessfully() {
        //When
        when(serviceMapper.toEntity(serviceDto)).thenReturn(serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenReturn(serviceEntity);
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        //Llamando a la funcion a testear
        ServiceDto result = serviceService.createService(serviceDto);

        //Then
        assertNotNull(result);
        assertEquals(serviceDto, result);
        verify(serviceMapper, times(1)).toEntity(serviceDto);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenSavingFails() {
        //When
        when(serviceMapper.toEntity(serviceDto)).thenReturn(serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenThrow(new RuntimeException("Error al guardar"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.createService(serviceDto);
        });

        //Then
        assertEquals("Error al guardar", exception.getMessage());
        verify(serviceMapper, times(1)).toEntity(serviceDto);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, never()).toDto(any(ServiceEntity.class));
    }

    /*test para updateService*/
    @Test
    void shouldUpdateServiceSuccessfully() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        doNothing().when(serviceMapper).updateEntityFromDto(serviceDto, serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenReturn(serviceEntity);
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        //Llamando a la funcion a testear
        ServiceDto result = serviceService.updateService(1L, serviceDto);

        //Then
        assertNotNull(result);
        assertEquals(serviceDto, result);
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, times(1)).updateEntityFromDto(serviceDto, serviceEntity);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenUpdateServiceNotFound() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            serviceService.updateService(1L, serviceDto);
        });

        //Then
        assertEquals("Service not found with id: 1", exception.getMessage());
        verify(serviceMapper, never()).updateEntityFromDto(any(ServiceDto.class), any(ServiceEntity.class));
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleErrorWhenSavingFails() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        doNothing().when(serviceMapper).updateEntityFromDto(serviceDto, serviceEntity);
        when(serviceRepository.save(serviceEntity)).thenThrow(new RuntimeException("Error al actualizar"));

        //Llamando a la funcion a testear
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.updateService(1L, serviceDto);
        });

        //Then
        assertEquals("Error al actualizar", exception.getMessage());
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceMapper, times(1)).updateEntityFromDto(serviceDto, serviceEntity);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

    /*test para updateServiceStatus*/
    @Test
    void shouldUpdateServiceStatusSuccessfully() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(serviceEntity)).thenReturn(serviceEntity);
        when(serviceMapper.toDto(serviceEntity)).thenReturn(serviceDto);

        //Llamando a la funcion a testear
        ServiceDto result = serviceService.updateServiceStatus(1L, "UNAVAILABLE");

        //Then
        assertNotNull(result);
        assertEquals("UNAVAILABLE", serviceEntity.getStatus().name());
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(serviceMapper, times(1)).toDto(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenUpdateStatsServiceNotFound() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        //Llamando a la funcion a testear
        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            serviceService.updateServiceStatus(1L, "AVAILABLE");
        });

        //Then
        assertEquals("Service not found with id: 1", exception.getMessage());
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        //Llamando a la funcion a testear
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            serviceService.updateServiceStatus(1L, "INVALID_STATUS");
        });

        //Then
        assertEquals("Invalid status value: INVALID_STATUS", exception.getMessage());
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleErrorWhenSavingFailsUpdateServiceStatus() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(serviceEntity)).thenThrow(new RuntimeException("Error al actualizar estado"));

        //Llamando a la funcion a testear
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.updateServiceStatus(1L, "AVAILABLE");
        });

        //Then
        assertEquals("Error al actualizar estado", exception.getMessage());
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

    /*teste para deleteService*/

    @Test
    void shouldSoftDeleteServiceSuccessfully() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        //Llamando a la funcion a testear
        serviceService.deleteService(1L);

        //Then
        assertEquals(StatusBusinessHours.DELETED, serviceEntity.getStatus());
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

    @Test
    void shouldThrowExceptionWhenServiceDeleteNotFound() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        //Llamando a la funcion a testear
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            serviceService.deleteService(1L);
        });

        //Then
        assertEquals("Service not found with id: 1", exception.getMessage());
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void shouldHandleErrorWhenSavingFailsDelete() {
        //When
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        doThrow(new RuntimeException("Error al guardar")).when(serviceRepository).save(serviceEntity);

        //Llamando a la funcion a testear
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.deleteService(1L);
        });

        //Then
        assertEquals("Error al guardar", exception.getMessage());
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(serviceEntity);
    }

}