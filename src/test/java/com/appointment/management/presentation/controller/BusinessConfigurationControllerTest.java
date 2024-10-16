package com.appointment.management.presentation.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

class BusinessConfigurationControllerTest {

    @Mock
    private BusinessConfigurationService businessConfigurationService;

    @InjectMocks
    private BusinessConfigurationController businessConfigurationController;

    private BusinessConfigurationDto validDto;
    private Validator validator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        validDto = new BusinessConfigurationDto(1L, "Test Business", "logo-url", 1L, null, "Test Description",
                "SERVICES", 7, 2, BigDecimal.valueOf(100), 7, BigDecimal.valueOf(2));
        // Inicializar el validador correctamente
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    void shouldCreateBusinessConfiguration() {
        // Arrange
        when(businessConfigurationService.save(any(BusinessConfigurationDto.class)))
                .thenReturn(validDto);

        // Act
        ResponseEntity<BusinessConfigurationDto> response = businessConfigurationController
                .createBusinessConfiguration(validDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Business", response.getBody().name());
        assertEquals("SERVICES", response.getBody().businessType());

        verify(businessConfigurationService).save(any(BusinessConfigurationDto.class));
    }

    @Test
    void shouldGetBusinessConfigurationById() {
        // Arrange
        when(businessConfigurationService.findById(1L)).thenReturn(validDto);

        // Act
        ResponseEntity<BusinessConfigurationDto> response = businessConfigurationController
                .getBusinessConfigurationById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Test Business", response.getBody().name());

        verify(businessConfigurationService).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBusinessConfigurationNotFound() {
        // Arrange
        when(businessConfigurationService.findById(1L))
                .thenThrow(new ValueNotFoundException("Business configuration not found"));

        // Act & Assert
        ValueNotFoundException thrown = assertThrows(ValueNotFoundException.class, () -> {
            businessConfigurationController.getBusinessConfigurationById(1L);
        });

        assertEquals("Business configuration not found", thrown.getMessage());
        verify(businessConfigurationService).findById(1L);
    }

    @Test
    void shouldUpdateBusinessConfiguration() {
        // Arrange
        when(businessConfigurationService.update(eq(1L), any(BusinessConfigurationDto.class)))
                .thenReturn(validDto);

        // Act
        ResponseEntity<BusinessConfigurationDto> response = businessConfigurationController
                .updateBusinessConfiguration(1L, validDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Test Business", response.getBody().name());

        verify(businessConfigurationService).update(eq(1L), any(BusinessConfigurationDto.class));
    }

    @Test
    void shouldThrowValidationExceptionForInvalidDto() {
        // Crear DTO inválido
        BusinessConfigurationDto invalidDto = new BusinessConfigurationDto(
                -1L, "", "invalid-url", 1L, null, "dfadfadfasdf", "SERVICES",
                1, 2, BigDecimal.valueOf(100), 7, BigDecimal.valueOf(2)
        );

        Set<ConstraintViolation<BusinessConfigurationDto>> violations = validator.validate(invalidDto);

        assertFalse(violations.isEmpty());

        String actualMessage = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        String expectedMessage = "name: no debe estar vacío; id: debe ser mayor que 0";

        assertEquals(expectedMessage, actualMessage);
    }


}
