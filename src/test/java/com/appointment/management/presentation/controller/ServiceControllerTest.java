package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.domain.service.business.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    private ServiceDto serviceDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        serviceDto = new ServiceDto(
                1L,
                "Test Service",
                BigDecimal.valueOf(10.10),
                LocalTime.of(1, 30),
                "This is a test service",
                10,
                "Test Location",
                "http://test.com/image.jpg",
                "AVAILABLE"
        );
    }


    @Test
    void getAllServices_ShouldReturnListOfServices() {
        // Arrange
        when(serviceService.getAllServices()).thenReturn(List.of(serviceDto));

        // Act
        ResponseEntity<List<ServiceDto>> response = serviceController.getAllServices();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(serviceService, times(1)).getAllServices();
    }

    @Test
    void getAvailableServices_ShouldReturnListOfAvailableServices() {
        // Arrange
        when(serviceService.getAllServicesAvailable()).thenReturn(List.of(serviceDto));

        // Act
        ResponseEntity<List<ServiceDto>> response = serviceController.getAvailableServices();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(serviceService, times(1)).getAllServicesAvailable();
    }

    @Test
    void getUnavailableServices_ShouldReturnListOfUnavailableServices() {
        // Arrange
        when(serviceService.getAllServicesUnavailable()).thenReturn(List.of(serviceDto));

        // Act
        ResponseEntity<List<ServiceDto>> response = serviceController.getUnavailableServices();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(serviceService, times(1)).getAllServicesUnavailable();
    }

    @Test
    void getServiceById_ShouldReturnServiceDto_WhenServiceExists() {
        // Arrange
        Long id = 1L;
        when(serviceService.getServiceById(id)).thenReturn(serviceDto);

        // Act
        ResponseEntity<ServiceDto> response = serviceController.getServiceById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceDto, response.getBody());
        verify(serviceService, times(1)).getServiceById(id);
    }

    @Test
    void createService_ShouldReturnCreatedService() {
        // Arrange
        when(serviceService.createService(any(ServiceDto.class))).thenReturn(serviceDto);

        // Act
        ResponseEntity<ServiceDto> response = serviceController.createService(serviceDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(serviceDto, response.getBody());
        verify(serviceService, times(1)).createService(any(ServiceDto.class));
    }

    @Test
    void updateService_ShouldReturnUpdatedService() {
        // Arrange
        Long id = 1L;
        when(serviceService.updateService(eq(id), any(ServiceDto.class))).thenReturn(serviceDto);

        // Act
        ResponseEntity<ServiceDto> response = serviceController.updateService(id, serviceDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceDto, response.getBody());
        verify(serviceService, times(1)).updateService(eq(id), any(ServiceDto.class));
    }

    @Test
    void updateServiceStatus_ShouldReturnUpdatedService() {
        // Arrange
        Long id = 1L;
        String status = "unavailable";
        when(serviceService.updateServiceStatus(id, status)).thenReturn(serviceDto);

        // Act
        ResponseEntity<ServiceDto> response = serviceController.updateServiceStatus(id, status);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceDto, response.getBody());
        verify(serviceService, times(1)).updateServiceStatus(id, status);
    }

    @Test
    void deleteService_ShouldReturnNoContent() {
        // Arrange
        Long id = 1L;
        doNothing().when(serviceService).deleteService(id);

        // Act
        ResponseEntity<Void> response = serviceController.deleteService(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(serviceService, times(1)).deleteService(id);
    }
}