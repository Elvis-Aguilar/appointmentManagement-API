package com.appointment.management.presentation.controller;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.domain.service.business.BusinessHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessHoursControllerTest {

    @InjectMocks
    private BusinessHoursController businessHoursController;

    @Mock
    private BusinessHoursService businessHoursService;

    private BusinessHoursDto businessHoursDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
                businessHoursDto = new BusinessHoursDto(
                1L,
                10L,
                "Monday",
                LocalDate.of(2023, 10, 23),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                LocalDateTime.now(),
                "ACTIVE",
                5,
                3
        );
    }

    @Test
    public void createBusinessHours_ShouldReturnCreatedBusinessHours() {
        // Given
        when(businessHoursService.save(any(BusinessHoursDto.class))).thenReturn(businessHoursDto);

        // When
        ResponseEntity<BusinessHoursDto> response = businessHoursController.createBusinessHours(businessHoursDto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(businessHoursDto, response.getBody());
        verify(businessHoursService, times(1)).save(businessHoursDto);
    }


    @Test
    public void createBusinessHoursGeneral_ShouldReturnCreatedBusinessHours() {
        // Given
        List<BusinessHoursDto> dtos = List.of(businessHoursDto);
        when(businessHoursService.createAllList(anyList())).thenReturn(dtos);

        // When
        ResponseEntity<List<BusinessHoursDto>> response = businessHoursController.createBusinessHoursGeneral(dtos);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dtos, response.getBody());
        verify(businessHoursService, times(1)).createAllList(dtos);
    }

    @Test
    public void getBusinessHours_ShouldReturnBusinessHours() {
        // Given
        Long id = 1L;
        when(businessHoursService.getById(id)).thenReturn(businessHoursDto);

        // When
        ResponseEntity<BusinessHoursDto> response = businessHoursController.getBusinessHours(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(businessHoursDto, response.getBody());
        verify(businessHoursService, times(1)).getById(id);
    }

    @Test
    public void getBusinessHours_ShouldThrowValueNotFoundException_WhenIdDoesNotExist() {
        // Given
        Long id = 99L;
        when(businessHoursService.getById(id)).thenThrow(new ValueNotFoundException("BusinessHours not found with id: " + id));

        // When
        Exception exception = assertThrows(ValueNotFoundException.class, () -> {
            businessHoursController.getBusinessHours(id);
        });

        // Then
        assertEquals("BusinessHours not found with id: " + id, exception.getMessage());
    }

    @Test
    public void updateBusinessHours_ShouldReturnUpdatedBusinessHours() {
        // Given
        Long id = 1L;
        when(businessHoursService.update(eq(id), any(BusinessHoursDto.class))).thenReturn(businessHoursDto);

        // When
        ResponseEntity<BusinessHoursDto> response = businessHoursController.updateBusinessHours(id, businessHoursDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(businessHoursDto, response.getBody());
        verify(businessHoursService, times(1)).update(id, businessHoursDto);
    }

    @Test
    public void updateBusinessHours_ShouldThrowValueNotFoundException_WhenIdDoesNotExist() {
        // Given
        Long id = 99L;
        when(businessHoursService.update(eq(id), any(BusinessHoursDto.class)))
                .thenThrow(new ValueNotFoundException("BusinessHours not found with id: " + id));

        // When
        Exception exception = assertThrows(ValueNotFoundException.class, () -> {
            businessHoursController.updateBusinessHours(id, businessHoursDto);
        });

        // Then
        assertEquals("BusinessHours not found with id: " + id, exception.getMessage());
    }

    @Test
    public void deleteBusinessHours_ShouldDeleteBusinessHours() {
        // Given
        Long id = 1L;

        // When
        ResponseEntity<Void> response = businessHoursController.deleteBusinessHours(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(businessHoursService, times(1)).delete(id);
    }

    @Test
    public void deleteBusinessHours_ShouldThrowValueNotFoundException_WhenIdDoesNotExist() {
        // Given
        Long id = 99L;
        doThrow(new ValueNotFoundException("BusinessHours not found with id: " + id)).when(businessHoursService).delete(id);

        // When
        Exception exception = assertThrows(ValueNotFoundException.class, () -> {
            businessHoursController.deleteBusinessHours(id);
        });

        // Then
        assertEquals("BusinessHours not found with id: " + id, exception.getMessage());
    }

    @Test
    public void getAllBusinessHours_ShouldReturnListOfBusinessHours() {
        // Given
        List<BusinessHoursDto> businessHoursList = List.of(businessHoursDto);
        when(businessHoursService.getAll()).thenReturn(businessHoursList);

        // When
        ResponseEntity<List<BusinessHoursDto>> response = businessHoursController.getAllBusinessHours();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(businessHoursList, response.getBody());
        verify(businessHoursService, times(1)).getAll();
    }
    @Test
    public void getAllBusinessHoursWithNullSpecificDateIs_ShouldReturnListOfBusinessHoursWithNullSpecificDate() {
        // Given
        List<BusinessHoursDto> businessHoursList = List.of(businessHoursDto);
        when(businessHoursService.getAllWithNullSpecificDateIs()).thenReturn(businessHoursList);

        // When
        ResponseEntity<List<BusinessHoursDto>> response = businessHoursController.getAllBusinessHoursWithNullSpecificDateIs();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(businessHoursList, response.getBody());
        verify(businessHoursService, times(1)).getAllWithNullSpecificDateIs();
    }

    @Test
    public void getAllBusinessHoursWithNotNullSpecificDateIs_ShouldReturnListOfBusinessHoursWithNotNullSpecificDate() {
        // Given
        List<BusinessHoursDto> businessHoursList = List.of(businessHoursDto);
        when(businessHoursService.getAllWithNotNullSpecificDate()).thenReturn(businessHoursList);

        // When
        ResponseEntity<List<BusinessHoursDto>> response = businessHoursController.getAllBusinessHoursWithNotNullSpecificDateIs();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(businessHoursList, response.getBody());
        verify(businessHoursService, times(1)).getAllWithNotNullSpecificDate();
    }

    @Test
    public void getAllBusinessHoursSpecificDateIsRange_ShouldReturnListOfBusinessHoursInDateRange() {
        // Given
        String startDateString = "2023-01-01";
        String endDateString = "2023-12-31";
        List<BusinessHoursDto> businessHoursList = List.of(businessHoursDto);
        when(businessHoursService.getBusinessHoursInDateRange(LocalDate.parse(startDateString), LocalDate.parse(endDateString)))
                .thenReturn(businessHoursList);

        // When
        ResponseEntity<List<BusinessHoursDto>> response = businessHoursController.getAllBusinessHoursSpecificDateIsRange(startDateString, endDateString);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(businessHoursList, response.getBody());
        verify(businessHoursService, times(1)).getBusinessHoursInDateRange(LocalDate.parse(startDateString), LocalDate.parse(endDateString));
    }



}