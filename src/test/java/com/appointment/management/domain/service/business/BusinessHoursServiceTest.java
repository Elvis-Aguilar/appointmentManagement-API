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
    void shouldReturnBusinessConfigurationHoursWhenIdExists() {
        Long id = 1L;

        when(businessHoursRepository.findById(id)).thenReturn(Optional.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        BusinessHoursDto result = businessHoursService.getById(id);

        assertNotNull(result);
        assertEquals(businessHoursDto.id(), result.id());
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
    void shouldThrowExceptionWhenUpdateBusinessHoursNotFount() {
        Long id = 1999L;
        when(businessHoursRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException thrown = assertThrows(
                ValueNotFoundException.class,
                () -> businessHoursService.update(id, this.businessHoursDto),
                "Expected findById() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("BusinessHours not found with id: " + id));

    }

    @Test
    void testDeleteBusinessHours() {
        Long id = 1L;
        when(businessHoursRepository.findById(id)).thenReturn(Optional.of(businessHoursEntity));

        businessHoursService.delete(id);

        verify(businessHoursRepository, times(1)).delete(businessHoursEntity);
    }

    @Test
    void shouldThrowExceptionWhenDeleteBusinessHoursNotFount() {
        Long id = 1999L;
        when(businessHoursRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException thrown = assertThrows(
                ValueNotFoundException.class,
                () -> businessHoursService.delete(id),
                "Expected findById() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("BusinessHours not found with id: " + id));

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

    @Test
    void shouldCreateAllBusinessHours() {
        List<BusinessHoursDto> dtoList = List.of(businessHoursDto);
        List<BusinessHoursEntity> entityList = List.of(businessHoursEntity);

        when(businessHoursMapper.toEntity(any(BusinessHoursDto.class))).thenReturn(businessHoursEntity);

        when(businessHoursRepository.saveAll(anyList())).thenReturn(entityList);

        when(businessHoursMapper.toDto(any(BusinessHoursEntity.class))).thenReturn(businessHoursDto);

        List<BusinessHoursDto> result = businessHoursService.createAllList(dtoList);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(businessHoursDto, result.getFirst());

        // Verificar que los métodos del mapper y repositorio fueron llamados
        verify(businessHoursMapper, times(1)).toEntity(businessHoursDto);
        verify(businessHoursRepository, times(1)).saveAll(anyList());
        verify(businessHoursMapper, times(1)).toDto(businessHoursEntity);
    }

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        List<BusinessHoursDto> emptyDtoList = List.of();

        List<BusinessHoursDto> result = businessHoursService.createAllList(emptyDtoList);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(businessHoursMapper, never()).toEntity(any());
        verify(businessHoursRepository, never()).saveAll(anyList());
        verify(businessHoursMapper, never()).toDto(any());
    }

    @Test
    void shouldHandleMultipleBusinessHoursDtos() {
        // Arrange
        BusinessHoursDto businessHoursDto2 = new BusinessHoursDto(
                2L,
                2L,
                "TUESDAY",
                null,
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                null,
                "AVAILABLE",
                4,
                3
        );

        BusinessHoursEntity businessHoursEntity2 = new BusinessHoursEntity();
        businessHoursEntity2.setId(2L);
        businessHoursEntity2.setOpeningTime(LocalTime.of(10, 0));
        businessHoursEntity2.setClosingTime(LocalTime.of(18, 0));
        businessHoursEntity2.setAvailableWorkers(4);
        businessHoursEntity2.setAvailableAreas(3);

        List<BusinessHoursDto> dtoList = List.of(businessHoursDto, businessHoursDto2);
        List<BusinessHoursEntity> entityList = List.of(businessHoursEntity, businessHoursEntity2);

        when(businessHoursMapper.toEntity(any(BusinessHoursDto.class)))
                .thenReturn(businessHoursEntity)
                .thenReturn(businessHoursEntity2);
        when(businessHoursRepository.saveAll(anyList())).thenReturn(entityList);
        when(businessHoursMapper.toDto(any(BusinessHoursEntity.class)))
                .thenReturn(businessHoursDto)
                .thenReturn(businessHoursDto2);

        List<BusinessHoursDto> result = businessHoursService.createAllList(dtoList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(businessHoursDto, result.get(0));
        assertEquals(businessHoursDto2, result.get(1));

        verify(businessHoursMapper, times(2)).toEntity(any(BusinessHoursDto.class));
        verify(businessHoursRepository, times(1)).saveAll(anyList());
        verify(businessHoursMapper, times(2)).toDto(any(BusinessHoursEntity.class));
    }
}
