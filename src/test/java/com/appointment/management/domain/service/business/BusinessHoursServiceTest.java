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

    //Variables para el Given Global
    private BusinessHoursDto businessHoursDto;
    private BusinessHoursEntity businessHoursEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
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

        //Given Global
        businessHoursEntity = new BusinessHoursEntity();
        businessHoursEntity.setId(1L);
        businessHoursEntity.setOpeningTime(LocalTime.of(9, 0));
        businessHoursEntity.setClosingTime(LocalTime.of(17, 0));
        businessHoursEntity.setAvailableWorkers(5);
        businessHoursEntity.setAvailableAreas(2);
    }

    @Test
    void testCreateBusinessHours() {
        //When
        when(businessHoursMapper.toEntity(any(BusinessHoursDto.class))).thenReturn(businessHoursEntity);
        when(businessHoursRepository.save(any(BusinessHoursEntity.class))).thenReturn(businessHoursEntity);
        when(businessHoursMapper.toDto(any(BusinessHoursEntity.class))).thenReturn(businessHoursDto);

        //Llamada a la funcion a testear
        BusinessHoursDto result = businessHoursService.save(businessHoursDto);

        //Then
        assertNotNull(result);
        assertEquals(businessHoursDto.business(), result.business());
        verify(businessHoursRepository, times(1)).save(businessHoursEntity);
    }

    @Test
    public void testGetByIdThrowsExceptionIfNotFound() {
        //Given
        Long id = 1L;

        //When
        when(businessHoursRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> {
            businessHoursService.getById(id);
        });

        //Then
        assertEquals("BusinessHours not found with id: " + id, exception.getMessage());
    }

    @Test
    void shouldReturnBusinessConfigurationHoursWhenIdExists() {
        //Given
        Long id = 1L;

        //When
        when(businessHoursRepository.findById(id)).thenReturn(Optional.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        //Llamando la funcion a testear
        BusinessHoursDto result = businessHoursService.getById(id);

        //Then
        assertNotNull(result);
        assertEquals(businessHoursDto.id(), result.id());
    }

    @Test
    void testUpdateBusinessHours() {
        //Given
        Long id = 1L;

        //When
        when(businessHoursRepository.findById(id)).thenReturn(Optional.of(businessHoursEntity));
        when(businessHoursRepository.save(businessHoursEntity)).thenReturn(businessHoursEntity);
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        //Llamando a la funcion a testear
        BusinessHoursDto updatedDto = businessHoursService.update(id, businessHoursDto);

        //Then
        assertNotNull(updatedDto);
        verify(businessHoursRepository, times(1)).save(businessHoursEntity);
        verify(businessHoursMapper, times(1)).updateEntityFromDto(businessHoursDto, businessHoursEntity);
    }

    @Test
    void shouldThrowExceptionWhenUpdateBusinessHoursNotFount() {
        //Given
        Long id = 1999L;

        //When
        when(businessHoursRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException thrown = assertThrows(
                ValueNotFoundException.class,
                () -> businessHoursService.update(id, this.businessHoursDto),
                "Expected findById() to throw, but it didn't"
        );

        //Then
        assertTrue(thrown.getMessage().contains("BusinessHours not found with id: " + id));

    }

    @Test
    void testDeleteBusinessHours() {
        //Given
        Long id = 1L;

        //When
        when(businessHoursRepository.findById(id)).thenReturn(Optional.of(businessHoursEntity));

        //Llamando a la funcion a testar
        businessHoursService.delete(id);

        //Then
        verify(businessHoursRepository, times(1)).delete(businessHoursEntity);
    }

    @Test
    void shouldThrowExceptionWhenDeleteBusinessHoursNotFount() {
        //Given
        Long id = 1999L;

        //When
        when(businessHoursRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException thrown = assertThrows(
                ValueNotFoundException.class,
                () -> businessHoursService.delete(id),
                "Expected findById() to throw, but it didn't"
        );

        //Then
        assertTrue(thrown.getMessage().contains("BusinessHours not found with id: " + id));

    }

    @Test
    void testGetAll() {
        //When
        when(businessHoursRepository.findAll()).thenReturn(List.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        //llamando a la funcion a testear
        List<BusinessHoursDto> result = businessHoursService.getAll();

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(businessHoursRepository, times(1)).findAll();
    }

    @Test
    void testGetAllWithNullSpecificDateIs() {
        //When
        when(businessHoursRepository.findBySpecificDateIsNull()).thenReturn(List.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        //llamando a la funcion a testear
        List<BusinessHoursDto> result = businessHoursService.getAllWithNullSpecificDateIs();

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(businessHoursRepository, times(1)).findBySpecificDateIsNull();
    }

    @Test
    void testGetBusinessHoursInDateRange() {
        //Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        //When
        when(businessHoursRepository.findBySpecificDateBetween(startDate, endDate))
                .thenReturn(List.of(businessHoursEntity));
        when(businessHoursMapper.toDto(businessHoursEntity)).thenReturn(businessHoursDto);

        //Llamando a al funcion a testear
        List<BusinessHoursDto> result = businessHoursService.getBusinessHoursInDateRange(startDate, endDate);

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(businessHoursRepository, times(1)).findBySpecificDateBetween(startDate, endDate);
    }

    @Test
    void shouldCreateAllBusinessHours() {
        //Given
        List<BusinessHoursDto> dtoList = List.of(businessHoursDto);
        List<BusinessHoursEntity> entityList = List.of(businessHoursEntity);

        //When
        when(businessHoursMapper.toEntity(any(BusinessHoursDto.class))).thenReturn(businessHoursEntity);
        when(businessHoursRepository.saveAll(anyList())).thenReturn(entityList);
        when(businessHoursMapper.toDto(any(BusinessHoursEntity.class))).thenReturn(businessHoursDto);

        //Llamando a la funcio a testear
        List<BusinessHoursDto> result = businessHoursService.createAllList(dtoList);

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(businessHoursDto, result.getFirst());
        verify(businessHoursMapper, times(1)).toEntity(businessHoursDto);
        verify(businessHoursRepository, times(1)).saveAll(anyList());
        verify(businessHoursMapper, times(1)).toDto(businessHoursEntity);
    }

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        //Given
        List<BusinessHoursDto> emptyDtoList = List.of();

        //When
        List<BusinessHoursDto> result = businessHoursService.createAllList(emptyDtoList);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(businessHoursMapper, never()).toEntity(any());
        verify(businessHoursRepository, never()).saveAll(anyList());
        verify(businessHoursMapper, never()).toDto(any());
    }

    @Test
    void shouldHandleMultipleBusinessHoursDtos() {
        // Given
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

        //When
        when(businessHoursMapper.toEntity(any(BusinessHoursDto.class)))
                .thenReturn(businessHoursEntity)
                .thenReturn(businessHoursEntity2);
        when(businessHoursRepository.saveAll(anyList())).thenReturn(entityList);
        when(businessHoursMapper.toDto(any(BusinessHoursEntity.class)))
                .thenReturn(businessHoursDto)
                .thenReturn(businessHoursDto2);

        //Llamando a la funcion a testear
        List<BusinessHoursDto> result = businessHoursService.createAllList(dtoList);

        //Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(businessHoursDto, result.get(0));
        assertEquals(businessHoursDto2, result.get(1));
        verify(businessHoursMapper, times(2)).toEntity(any(BusinessHoursDto.class));
        verify(businessHoursRepository, times(1)).saveAll(anyList());
        verify(businessHoursMapper, times(2)).toDto(any(BusinessHoursEntity.class));
    }

    @Test
    void testGetAllWithNotNullSpecificDate() {
        // Given
        BusinessHoursEntity businessHoursEntityWithSpecificDate = new BusinessHoursEntity();
        businessHoursEntityWithSpecificDate.setId(1L);
        businessHoursEntityWithSpecificDate.setOpeningTime(LocalTime.of(9, 0));
        businessHoursEntityWithSpecificDate.setClosingTime(LocalTime.of(17, 0));
        businessHoursEntityWithSpecificDate.setAvailableWorkers(5);
        businessHoursEntityWithSpecificDate.setAvailableAreas(2);
        businessHoursEntityWithSpecificDate.setSpecificDate(LocalDate.now()); // Suponiendo que este campo existe

        List<BusinessHoursEntity> entities = List.of(businessHoursEntityWithSpecificDate);

        // When
        when(businessHoursRepository.findBySpecificDateIsNotNull()).thenReturn(entities);
        when(businessHoursMapper.toDto(businessHoursEntityWithSpecificDate)).thenReturn(businessHoursDto);

        // llamado de funcion a testear
        List<BusinessHoursDto> result = businessHoursService.getAllWithNotNullSpecificDate();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(businessHoursDto, result.get(0));
        verify(businessHoursRepository, times(1)).findBySpecificDateIsNotNull();
        verify(businessHoursMapper, times(1)).toDto(businessHoursEntityWithSpecificDate);
    }

}
