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

    //Varivales globales para el given global
    private BusinessConfigurationDto validDto;
    private Validator validator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        //Given global
        validDto = new BusinessConfigurationDto(1L, "Test Business", "logo-url", 1L, null, "Test Description",
                "SERVICES", 7, 2, BigDecimal.valueOf(100), 7, BigDecimal.valueOf(2), false);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    void shouldCreateBusinessConfiguration() {
        // When
        when(businessConfigurationService.save(any(BusinessConfigurationDto.class)))
                .thenReturn(validDto);

        //Ejecutando el metodo del controlador a testear
        ResponseEntity<BusinessConfigurationDto> response = businessConfigurationController
                .createBusinessConfiguration(validDto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Business", response.getBody().name());
        assertEquals("SERVICES", response.getBody().businessType());

        verify(businessConfigurationService).save(any(BusinessConfigurationDto.class));
    }

    @Test
    void shouldGetBusinessConfigurationById() {
        // When
        when(businessConfigurationService.findById(1L)).thenReturn(validDto);

        //Ejecutando el metodo del controlador a testear
        ResponseEntity<BusinessConfigurationDto> response = businessConfigurationController
                .getBusinessConfigurationById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Test Business", response.getBody().name());

        verify(businessConfigurationService).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBusinessConfigurationNotFound() {
        // When
        when(businessConfigurationService.findById(1L))
                .thenThrow(new ValueNotFoundException("Business configuration not found"));

        //Ejecutando el metodo del controlador a testear
        ValueNotFoundException thrown = assertThrows(ValueNotFoundException.class, () -> {
            businessConfigurationController.getBusinessConfigurationById(1L);
        });

        //Then
        assertEquals("Business configuration not found", thrown.getMessage());
        verify(businessConfigurationService).findById(1L);
    }

    @Test
    void shouldUpdateBusinessConfiguration() {
        // Given
        when(businessConfigurationService.update(eq(1L), any(BusinessConfigurationDto.class)))
                .thenReturn(validDto);

        // When
        ResponseEntity<BusinessConfigurationDto> response = businessConfigurationController
                .updateBusinessConfiguration(1L, validDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Test Business", response.getBody().name());

        verify(businessConfigurationService).update(eq(1L), any(BusinessConfigurationDto.class));
    }

    @Test
    void shouldThrowValidationExceptionForInvalidDto() {
        // Given
        BusinessConfigurationDto invalidDto = new BusinessConfigurationDto(
                -1L, "afdfdfa", "invalid-url", 1L, null, "dfadfadfasdf", "SERVICES",
                1, 2, BigDecimal.valueOf(100), 7, BigDecimal.valueOf(2), false
        );

        Set<ConstraintViolation<BusinessConfigurationDto>> violations = validator.validate(invalidDto);

        assertFalse(violations.isEmpty());

        //When
        String actualMessage = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        String expectedMessage = "id: debe ser mayor que 0";

        //Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getBusinessConfigurationByFirst_ShouldReturnBusinessConfiguration_WhenFound() {
        // Given
        when(businessConfigurationService.findFirst()).thenReturn(validDto);

        // When
        ResponseEntity<BusinessConfigurationDto> response = businessConfigurationController.getBusinessConfigurationByFirst();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validDto, response.getBody());
    }




}
