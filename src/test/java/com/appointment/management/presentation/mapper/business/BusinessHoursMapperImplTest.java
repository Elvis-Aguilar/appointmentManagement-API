package com.appointment.management.presentation.mapper.business;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.presentation.mapper.helpers.BusinessConfigurationMapperHelper;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import com.appointment.management.persistance.enums.DayOfWeek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessHoursMapperImplTest {

    @Mock
    private BusinessConfigurationMapperHelper businessConfigurationMapperHelper;

    @InjectMocks
    private BusinessHoursMapperImpl businessHoursMapperImpl;

    private BusinessHoursDto businessHoursDto;
    private BusinessConfigurationEntity businessConfigurationEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        businessConfigurationEntity = new BusinessConfigurationEntity();
        businessConfigurationEntity.setId(1L);

        businessHoursDto = new BusinessHoursDto(
                1L,
                1L,
                "MONDAY",
                LocalDate.of(2024, 10, 15),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                LocalDateTime.now(),
                "AVAILABLE",
                5,
                3
        );
    }

    @Test
    void shouldConvertDtoToEntityWhenDtoIsValid() {
        when(businessConfigurationMapperHelper.findById(1L)).thenReturn(businessConfigurationEntity);

        BusinessHoursEntity result = businessHoursMapperImpl.toEntity(businessHoursDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(businessConfigurationEntity, result.getBusiness());
        assertEquals(LocalDate.of(2024, 10, 15), result.getSpecificDate());
        assertEquals(LocalTime.of(9, 0), result.getOpeningTime());
        assertEquals(LocalTime.of(17, 0), result.getClosingTime());
        assertEquals(StatusBusinessHours.AVAILABLE, result.getStatus());
        assertEquals(5, result.getAvailableWorkers());
        assertEquals(3, result.getAvailableAreas());

        verify(businessConfigurationMapperHelper, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBusinessNotFound() {
        when(businessConfigurationMapperHelper.findById(1L)).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            businessHoursMapperImpl.toEntity(businessHoursDto);
        });
        assertEquals("Business Configuration not found with id: 1", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStatusIsInvalid() {
        when(businessConfigurationMapperHelper.findById(1L)).thenReturn(new BusinessConfigurationEntity());

        BusinessHoursDto invalidStatusDto = new BusinessHoursDto(
                1L,
                1L,
                "MONDAY",
                LocalDate.of(2024, 10, 15),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                LocalDateTime.now(),
                "INVALID_STATUS",
                5,
                3
        );

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            businessHoursMapperImpl.toEntity(invalidStatusDto);
        });
        assertEquals("Invalid business Hours type: INVALID_STATUS", exception.getMessage());
    }

    @Test
    void shouldConvertEntityToDtoWhenEntityIsValid() {
        BusinessHoursEntity entity = new BusinessHoursEntity();
        entity.setId(1L);
        entity.setBusiness(businessConfigurationEntity);
        entity.setDayOfWeek(DayOfWeek.FRIDAY);
        entity.setSpecificDate(LocalDate.of(2024, 10, 15));
        entity.setOpeningTime(LocalTime.of(9, 0));
        entity.setClosingTime(LocalTime.of(17, 0));
        entity.setStatus(StatusBusinessHours.AVAILABLE);
        entity.setAvailableWorkers(5);
        entity.setAvailableAreas(3);
        entity.setCreatedAt(LocalDateTime.now());

        when(businessConfigurationMapperHelper.toId(businessConfigurationEntity)).thenReturn(1L);

        BusinessHoursDto result = businessHoursMapperImpl.toDto(entity);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.business());
        assertEquals("FRIDAY", result.dayOfWeek());
        assertEquals(LocalDate.of(2024, 10, 15), result.specificDate());
        assertEquals(LocalTime.of(9, 0), result.openingTime());
        assertEquals(LocalTime.of(17, 0), result.closingTime());
        assertEquals("AVAILABLE", result.status());
        assertEquals(5, result.availableWorkers());
        assertEquals(3, result.availableAreas());

        verify(businessConfigurationMapperHelper, times(1)).toId(businessConfigurationEntity);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        BusinessHoursDto result = businessHoursMapperImpl.toDto(null);

        assertNull(result);
    }

    @Test
    void shouldUpdateEntityFromDto() {
        BusinessHoursEntity entity = new BusinessHoursEntity();
        entity.setId(1L);

        when(businessConfigurationMapperHelper.findById(1L)).thenReturn(businessConfigurationEntity);

        businessHoursMapperImpl.updateEntityFromDto(businessHoursDto, entity);

        assertEquals(businessConfigurationEntity, entity.getBusiness());
        assertEquals(DayOfWeek.MONDAY, entity.getDayOfWeek());
        assertEquals(LocalDate.of(2024, 10, 15), entity.getSpecificDate());
        assertEquals(LocalTime.of(9, 0), entity.getOpeningTime());
        assertEquals(LocalTime.of(17, 0), entity.getClosingTime());
        assertEquals(StatusBusinessHours.AVAILABLE, entity.getStatus());
        assertEquals(5, entity.getAvailableWorkers());
        assertEquals(3, entity.getAvailableAreas());

        verify(businessConfigurationMapperHelper, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDayOfWeekIsInvalid() {
        when(businessConfigurationMapperHelper.findById(1L)).thenReturn(new BusinessConfigurationEntity());
        BusinessHoursDto invalidDayOfWeekDto = new BusinessHoursDto(
                1L,
                1L,
                "INVALID_DAY",
                LocalDate.of(2024, 10, 15),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                LocalDateTime.now(),
                "AVAILABLE",
                5,
                3
        );

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            businessHoursMapperImpl.toEntity(invalidDayOfWeekDto);
        });
        assertEquals("Invalid Day Week: INVALID_DAY", exception.getMessage());
    }

    @Test
    void shouldReturnNullWhenDtoIsNull() {
        BusinessHoursEntity result = businessHoursMapperImpl.toEntity(null);

        assertNull(result);
    }

    @Test
    void shouldNotUpdateEntityWhenDtoIsNull() {
        BusinessHoursEntity entity = new BusinessHoursEntity();
        entity.setId(1L);
        entity.setDayOfWeek(DayOfWeek.MONDAY);
        entity.setSpecificDate(LocalDate.of(2024, 10, 15));
        entity.setOpeningTime(LocalTime.of(9, 0));
        entity.setClosingTime(LocalTime.of(17, 0));
        entity.setStatus(StatusBusinessHours.AVAILABLE);
        entity.setAvailableWorkers(5);
        entity.setAvailableAreas(3);

        businessHoursMapperImpl.updateEntityFromDto(null, entity);

        assertEquals(1L, entity.getId());
        assertEquals(DayOfWeek.MONDAY, entity.getDayOfWeek());
        assertEquals(LocalDate.of(2024, 10, 15), entity.getSpecificDate());
        assertEquals(LocalTime.of(9, 0), entity.getOpeningTime());
        assertEquals(LocalTime.of(17, 0), entity.getClosingTime());
        assertEquals(StatusBusinessHours.AVAILABLE, entity.getStatus());
        assertEquals(5, entity.getAvailableWorkers());
        assertEquals(3, entity.getAvailableAreas());
    }
}
