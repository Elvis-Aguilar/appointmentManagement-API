package com.appointment.management.presentation.mapper.business;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.business.EmployeeAvailabilityDto;
import com.appointment.management.persistance.entity.EmployeeAvailabilityEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.DayOfWeek;
import com.appointment.management.presentation.mapper.helpers.UserMapperHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeAvailabilityMapperImplTest {

    @Mock
    private UserMapperHelper userMapperHelper;

    @InjectMocks
    private EmployeeAvailabilityMapperImpl employeeAvailabilityMapperImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnNullWhenDtoIsNull() {
        EmployeeAvailabilityEntity result = employeeAvailabilityMapperImpl.toEntity(null);

        assertNull(result);
    }

    @Test
    void shouldMapDtoToEntitySuccessfully() {
        Long employeeId = 1L;
        UserEntity mockUser = new UserEntity();
        mockUser.setId(employeeId);

        EmployeeAvailabilityDto dto = new EmployeeAvailabilityDto(
                1L,
                employeeId,
                "MONDAY",
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null
        );

        when(userMapperHelper.findById(employeeId)).thenReturn(mockUser);

        EmployeeAvailabilityEntity result = employeeAvailabilityMapperImpl.toEntity(dto);

        assertNotNull(result);
        assertEquals(dto.id(), result.getId());
        assertEquals(mockUser, result.getEmployee());
        assertEquals(DayOfWeek.MONDAY.toString(), result.getDayOfWeek().toString());
        assertEquals(dto.startTime(), result.getStartTime());
        assertEquals(dto.endTime(), result.getEndTime());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        Long employeeId = 1L;
        EmployeeAvailabilityDto dto = new EmployeeAvailabilityDto(
                1L,
                employeeId,
                "MONDAY",
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null
        );

        when(userMapperHelper.findById(employeeId)).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            employeeAvailabilityMapperImpl.toEntity(dto);
        });
        assertEquals("Admin not found with id: 1", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDayOfWeekIsInvalid() {
        Long employeeId = 1L;
        UserEntity mockUser = new UserEntity();
        mockUser.setId(employeeId);

        EmployeeAvailabilityDto dto = new EmployeeAvailabilityDto(
                1L,
                employeeId,
                "INVALID_DAY",
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null
        );

        when(userMapperHelper.findById(employeeId)).thenReturn(mockUser);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            employeeAvailabilityMapperImpl.toEntity(dto);
        });
        assertEquals("Invalid Day Week: INVALID_DAY", exception.getMessage());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        EmployeeAvailabilityDto result = employeeAvailabilityMapperImpl.toDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapEntityToDtoSuccessfully() {
        Long employeeId = 1L;
        LocalDateTime createdAt = LocalDateTime.now();
        UserEntity mockUser = new UserEntity();
        mockUser.setId(employeeId);

        EmployeeAvailabilityEntity entity = new EmployeeAvailabilityEntity();
        entity.setId(1L);
        entity.setEmployee(mockUser);
        entity.setDayOfWeek(DayOfWeek.MONDAY);
        entity.setStartTime(LocalTime.of(9, 0));
        entity.setEndTime(LocalTime.of(17, 0));
        entity.setCreatedAt(createdAt);

        when(userMapperHelper.toId(mockUser)).thenReturn(employeeId);

        // Act
        EmployeeAvailabilityDto result = employeeAvailabilityMapperImpl.toDto(entity);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.id());
        assertEquals(employeeId, result.employee());
        assertEquals("MONDAY", result.dayOfWeek());
        assertEquals(entity.getStartTime(), result.startTime());
        assertEquals(entity.getEndTime(), result.endTime());
        assertEquals(entity.getCreatedAt(), result.createdAt());
    }

    @Test
    void shouldDoNothingWhenDtoIsNull() {
        // Arrange
        EmployeeAvailabilityEntity entity = new EmployeeAvailabilityEntity();
        entity.setDayOfWeek(DayOfWeek.MONDAY);

        // Act
        employeeAvailabilityMapperImpl.updateEntityFromDto(null, entity);

        // Assert
        assertEquals(DayOfWeek.MONDAY,entity.getDayOfWeek());
    }

    @Test
    void shouldUpdateEntityFromDtoSuccessfully() {
        // Arrange
        Long employeeId = 1L;
        UserEntity mockUser = new UserEntity();
        mockUser.setId(employeeId);

        EmployeeAvailabilityDto dto = new EmployeeAvailabilityDto(
                1L,
                employeeId,
                "MONDAY",
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null
        );

        EmployeeAvailabilityEntity entity = new EmployeeAvailabilityEntity();
        when(userMapperHelper.findById(employeeId)).thenReturn(mockUser);

        // Act
        employeeAvailabilityMapperImpl.updateEntityFromDto(dto, entity);

        // Assert
        assertEquals(mockUser, entity.getEmployee());
        assertEquals(dto.dayOfWeek(), entity.getDayOfWeek().name());
        assertEquals(dto.startTime(), entity.getStartTime());
        assertEquals(dto.endTime(), entity.getEndTime());
    }



}
