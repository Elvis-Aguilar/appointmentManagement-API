package com.appointment.management.presentation.mapper.business;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.presentation.mapper.helpers.UserMapperHelper;
import com.appointment.management.persistance.enums.BusinessType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessConfigurationMapperImplTest {

    @Mock
    private UserMapperHelper userMapperHelper;

    @InjectMocks
    private BusinessConfigurationMapperImpl businessConfigurationMapperImpl;

    private UserEntity admin;
    private BusinessConfigurationDto dto;
    private BusinessConfigurationEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
        admin = new UserEntity();
        admin.setId(1L);
        admin.setName("Admin User");

        //Given Global
        dto = new BusinessConfigurationDto(
                1L,
                "Business Name",
                "https://logo.url",
                1L,
                null,
                "Business Description",
                "SERVICES",
                3,
                12,
                BigDecimal.valueOf(15.00),
                2,
                BigDecimal.valueOf(15.00),
                false
        );

        //Given Global
        entity = new BusinessConfigurationEntity();
        entity.setId(1L);
        entity.setAdmin(admin);
        entity.setName("Business Name");
        entity.setLogoUrl("https://logo.url");
        entity.setDescription("Business Description");
        entity.setBusinessType(BusinessType.SERVICES);
        entity.setMaxDaysCancellation(3);
        entity.setMaxHoursCancellation(12);
        entity.setCancellationSurcharge(BigDecimal.valueOf(15.00));
        entity.setMaxDaysUpdate(2);
        entity.setMaxHoursUpdate(BigDecimal.valueOf(15.00));
        entity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldConvertDtoToEntityWhenDtoIsValid() {

        // When
        when(userMapperHelper.findById(1L)).thenReturn(admin);

        // Ejecucion del metodo a testear
        BusinessConfigurationEntity result = businessConfigurationMapperImpl.toEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(admin, result.getAdmin());
        assertEquals("Business Name", result.getName());
        assertEquals("https://logo.url", result.getLogoUrl());
        assertEquals("Business Description", result.getDescription());
        assertEquals(BusinessType.SERVICES, result.getBusinessType());
        assertEquals(3, result.getMaxDaysCancellation());
        assertEquals(12, result.getMaxHoursCancellation());
        assertEquals(BigDecimal.valueOf(15.00), result.getCancellationSurcharge());
        assertEquals(2, result.getMaxDaysUpdate());

        verify(userMapperHelper, times(1)).findById(1L);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenAdminNotFound() {

        // When
        when(userMapperHelper.findById(1L)).thenReturn(null);

        // Ejecucion del metodo a testear
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            businessConfigurationMapperImpl.toEntity(dto);
        });

        // Then
        assertEquals("Admin not found with id: 1", exception.getMessage());

        verify(userMapperHelper, times(1)).findById(1L);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenBusinessTypeIsInvalid() {

        // Given
        BusinessConfigurationDto invalidDto = new BusinessConfigurationDto(
                1L,
                "Business Name",
                "https://logo.url",
                1L,
                null,
                "Business Description",
                "INVALID_TYPE",
                3,
                12,
                BigDecimal.valueOf(15.00),
                2,
                BigDecimal.valueOf(15.00),
                false
        );

        // When
        when(userMapperHelper.findById(1L)).thenReturn(admin);

        // Ejecucion del metodo a testear
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            businessConfigurationMapperImpl.toEntity(invalidDto);
        });

        // Then
        assertEquals("Invalid business type: INVALID_TYPE", exception.getMessage());
        verify(userMapperHelper, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenDtoIsNull() {

        // When
        BusinessConfigurationEntity result = businessConfigurationMapperImpl.toEntity(null);

        // Then
        assertNull(result);

        verify(userMapperHelper, times(0)).findById(anyLong());
    }

    @Test
    void shouldConvertEntityToDtoWhenEntityIsValid() {

        // When
        when(userMapperHelper.toId(admin)).thenReturn(1L);

        // Ejecucion del metodo a testear
        BusinessConfigurationDto result = businessConfigurationMapperImpl.toDto(entity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.admin());
        assertEquals("Business Name", result.name());
        assertEquals("https://logo.url", result.logoUrl());
        assertEquals("Business Description", result.description());
        assertEquals("SERVICES", result.businessType());
        assertEquals(3, result.maxDaysCancellation());
        assertEquals(12, result.maxHoursCancellation());
        assertEquals(BigDecimal.valueOf(15.00), result.cancellationSurcharge());
        assertEquals(2, result.maxDaysUpdate());
        assertNotNull(result.createdAt());

        // Verificamos que el UserMapperHelper fue llamado correctamente
        verify(userMapperHelper, times(1)).toId(admin);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
         // When
        BusinessConfigurationDto result = businessConfigurationMapperImpl.toDto(null);

        // Then
        assertNull(result);
        verify(userMapperHelper, times(0)).toId(any(UserEntity.class));
    }


}
