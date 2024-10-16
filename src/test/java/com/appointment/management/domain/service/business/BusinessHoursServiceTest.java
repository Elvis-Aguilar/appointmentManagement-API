package com.appointment.management.domain.service.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.domain.service.business.BusinessHoursService;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.repository.BusinessHoursRepository;
import com.appointment.management.presentation.mapper.business.BusinessHoursMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

class BusinessHoursServiceTest {

    @Mock
    private BusinessHoursRepository businessHoursRepository;

    @Mock
    private BusinessHoursMapper businessHoursMapper;

    @InjectMocks
    private BusinessHoursService businessHoursService;

    private BusinessHoursDto businessHoursDto;
    private BusinessHoursEntity businessHoursEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        businessHoursDto = new BusinessHoursDto(
                1L,
                1L,
                "MONDAY",
                null,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null,
                "AVAILABLE",
                5,
                2
        );

        businessHoursEntity = new BusinessHoursEntity();
        businessHoursEntity.setId(1L);
        businessHoursEntity.setOpeningTime(LocalTime.of(9, 0));
        businessHoursEntity.setClosingTime(LocalTime.of(17, 0));
        businessHoursEntity.setAvailableWorkers(5);
        businessHoursEntity.setAvailableAreas(2);
        // Configurar otros campos según sea necesario
    }

    @Test
    void testCreateBusinessHours() {
        when(businessHoursMapper.toEntity(any(BusinessHoursDto.class))).thenReturn(businessHoursEntity);
        when(businessHoursRepository.save(any(BusinessHoursEntity.class))).thenReturn(businessHoursEntity);
        when(businessHoursMapper.toDto(any(BusinessHoursEntity.class))).thenReturn(businessHoursDto);

        BusinessHoursDto result = businessHoursService.save(businessHoursDto);

        assertNotNull(result);
        assertEquals(businessHoursDto.business(), result.business());
        verify(businessHoursRepository, times(1)).save(businessHoursEntity);
    }

    @Test
    public void testGetByIdThrowsExceptionIfNotFound() {
        Long id = 1L;
        when(businessHoursRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            businessHoursService.getById(id);
        });

        assertEquals("BusinessHours not found with id: " + id, exception.getMessage());
    }

    @Test
    void testUpdateBusinessHours() {
        Long id = 1L;
        when(businessHoursRepository.findById(id)).thenReturn(Optional.of(businessHoursEntity));
        when(businessHoursRepository.save(businessHoursEntity)).thenReturn(businessHoursEntity);
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        BusinessHoursDto updatedDto = businessHoursService.update(id, businessHoursDto);

        assertNotNull(updatedDto);
        verify(businessHoursRepository, times(1)).save(businessHoursEntity);
        verify(businessHoursMapper, times(1)).updateEntityFromDto(businessHoursDto, businessHoursEntity);
    }

    @Test
    void testDeleteBusinessHours() {
        Long id = 1L;
        when(businessHoursRepository.findById(id)).thenReturn(Optional.of(businessHoursEntity));

        businessHoursService.delete(id);

        verify(businessHoursRepository, times(1)).delete(businessHoursEntity);
    }

    @Test
    void testGetAll() {
        when(businessHoursRepository.findAll()).thenReturn(List.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        List<BusinessHoursDto> result = businessHoursService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(businessHoursRepository, times(1)).findAll();
    }

    @Test
    void testGetAllWithNullSpecificDateIs() {
        when(businessHoursRepository.findBySpecificDateIsNull()).thenReturn(List.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        List<BusinessHoursDto> result = businessHoursService.getAllWithNullSpecificDateIs();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(businessHoursRepository, times(1)).findBySpecificDateIsNull();
    }

    @Test
    void testGetBusinessHoursInDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(businessHoursRepository.findBySpecificDateBetween(startDate, endDate))
                .thenReturn(List.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        List<BusinessHoursDto> result = businessHoursService.getBusinessHoursInDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(businessHoursRepository, times(1)).findBySpecificDateBetween(startDate, endDate);
    }
}
