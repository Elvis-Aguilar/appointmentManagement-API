package com.appointment.management.domain.service.business;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.EmployeeAvailabilityDto;
import com.appointment.management.persistance.entity.EmployeeAvailabilityEntity;
import com.appointment.management.persistance.repository.EmployeeAvailabilityRepository;
import com.appointment.management.presentation.mapper.business.EmployeeAvailabilityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeAvailabilityServiceTest {

    @Mock
    private EmployeeAvailabilityRepository employeeAvailabilityRepository;

    @Mock
    private EmployeeAvailabilityMapper employeeAvailabilityMapper;

    @InjectMocks
    private EmployeeAvailabilityService employeeAvailabilityService;

    //variable globales para el Given global
    private EmployeeAvailabilityEntity employeeAvailabilityEntity;
    private EmployeeAvailabilityDto employeeAvailabilityDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
         this.employeeAvailabilityDto = new EmployeeAvailabilityDto(
                1L,
                123L,
                "MONDAY",
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null
        );
        //Given Global
         this.employeeAvailabilityEntity = new EmployeeAvailabilityEntity();
         this.employeeAvailabilityEntity.setId(1L);
         this.employeeAvailabilityEntity.setEndTime(LocalTime.of(9, 0));
    }

    /*tests para getAllAvailabilities*/
    @Test
    void getAllAvailabilities_ShouldReturnEmptyList_WhenNoAvailabilities() {
        // When
        when(employeeAvailabilityRepository.findAll()).thenReturn(Collections.emptyList());

        // Llamando la funcion a testear
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAllAvailabilities();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeAvailabilityRepository, times(1)).findAll();
        verifyNoInteractions(employeeAvailabilityMapper);
    }

    @Test
    void getAllAvailabilities_ShouldReturnListOfAvailabilities_WhenThereAreAvailabilities() {
        // When
        when(employeeAvailabilityRepository.findAll()).thenReturn(List.of(employeeAvailabilityEntity));
        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        // Llamando la funcion a testar
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAllAvailabilities();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(employeeAvailabilityDto, result.getFirst());
        verify(employeeAvailabilityRepository, times(1)).findAll();
        verify(employeeAvailabilityMapper, times(1)).toDto(employeeAvailabilityEntity);
    }

    /*tests para getAvailabilitiesByEmployeeId*/
    @Test
    void getAvailabilitiesByEmployeeId_ShouldReturnEmptyList_WhenNoAvailabilities() {
        //Given
        Long employeeId = 123L;

        //When
        when(employeeAvailabilityRepository.findAllByEmployeeId(employeeId)).thenReturn(Collections.emptyList());

        //Llamando la funcion a testear
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAvailabilitiesByEmployeeId(employeeId);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeAvailabilityRepository, times(1)).findAllByEmployeeId(employeeId);
        verifyNoInteractions(employeeAvailabilityMapper);
    }

    @Test
    void getAvailabilitiesByEmployeeId_ShouldReturnListOfAvailabilities_WhenThereAreAvailabilities() {
        //Given
        Long employeeId = 123L;

        //When
        when(employeeAvailabilityRepository.findAllByEmployeeId(employeeId)).thenReturn(List.of(employeeAvailabilityEntity));
        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        //Llmando la funcion a testear
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAvailabilitiesByEmployeeId(employeeId);

        //Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(employeeAvailabilityDto, result.getFirst());
        verify(employeeAvailabilityRepository, times(1)).findAllByEmployeeId(employeeId);
        verify(employeeAvailabilityMapper, times(1)).toDto(employeeAvailabilityEntity);
    }

    /*Tests para createAvailability*/
    @Test
    void createAvailability_ShouldReturnDto_WhenEntityIsSaved() {
        //When
        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);
        when(employeeAvailabilityRepository.save(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityEntity);
        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        //Llamando la funcion a testear
        EmployeeAvailabilityDto result = employeeAvailabilityService.createAvailability(employeeAvailabilityDto);

        //Then
        assertNotNull(result);
        assertEquals(employeeAvailabilityDto, result);
        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);
        verify(employeeAvailabilityRepository, times(1)).save(employeeAvailabilityEntity);
        verify(employeeAvailabilityMapper, times(1)).toDto(employeeAvailabilityEntity);
    }

    @Test
    void createAvailability_ShouldThrowException_WhenSaveFails() {
        //When
        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);
        when(employeeAvailabilityRepository.save(employeeAvailabilityEntity)).thenThrow(new RuntimeException("Error al guardar la entidad"));

        assertThrows(RuntimeException.class, () -> {
            employeeAvailabilityService.createAvailability(employeeAvailabilityDto);
        });

        //Then
        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);
        verify(employeeAvailabilityRepository, times(1)).save(employeeAvailabilityEntity);
        verify(employeeAvailabilityMapper, times(0)).toDto(any());
    }

    /*tests para createAllList*/
    @Test
    void createAllList_ShouldReturnDtoList_WhenEntitiesAreSaved() {
        //Given
        List<EmployeeAvailabilityDto> dtoList = List.of(employeeAvailabilityDto);
        List<EmployeeAvailabilityEntity> entityList = List.of(employeeAvailabilityEntity);

        //When
        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);
        when(employeeAvailabilityRepository.saveAll(entityList)).thenReturn(entityList);
        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        //Llamando a la funcion a testar
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.createAllList(dtoList);

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employeeAvailabilityDto, result.getFirst());
        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);
        verify(employeeAvailabilityRepository, times(1)).saveAll(entityList);
        verify(employeeAvailabilityMapper, times(1)).toDto(employeeAvailabilityEntity);
    }

    @Test
    void createAllList_ShouldReturnEmptyList_WhenInputIsEmpty() {
        //Given
        List<EmployeeAvailabilityDto> emptyDtoList = List.of();

        //When
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.createAllList(emptyDtoList);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

    @Test
    void createAllList_ShouldThrowException_WhenSaveFails() {

        //Given
        List<EmployeeAvailabilityDto> dtoList = List.of(employeeAvailabilityDto);
        List<EmployeeAvailabilityEntity> entityList = List.of(employeeAvailabilityEntity);

        //When
        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);
        when(employeeAvailabilityRepository.saveAll(entityList)).thenThrow(new RuntimeException("Error al guardar entidades"));

        assertThrows(RuntimeException.class, () -> {
            employeeAvailabilityService.createAllList(dtoList);
        });

        //Then
        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);
        verify(employeeAvailabilityRepository, times(1)).saveAll(entityList);
        verify(employeeAvailabilityMapper, times(0)).toDto(any());
    }

    /*tests para updateAvailability*/
    @Test
    void updateAvailability_ShouldUpdateEntity_WhenIdExists() {
        // Given
        Long id = 1L;
        EmployeeAvailabilityDto dto = employeeAvailabilityDto;
        EmployeeAvailabilityEntity existingEntity = employeeAvailabilityEntity;

        //When
        when(employeeAvailabilityRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(employeeAvailabilityMapper).updateEntityFromDto(dto, existingEntity);
        when(employeeAvailabilityRepository.save(existingEntity)).thenReturn(existingEntity);
        when(employeeAvailabilityMapper.toDto(existingEntity)).thenReturn(dto);

        // Llamando a la funcion a testear
        EmployeeAvailabilityDto result = employeeAvailabilityService.updateAvailability(id, dto);

        // Then
        assertNotNull(result);
        assertEquals(dto, result);
        verify(employeeAvailabilityRepository).findById(id);
        verify(employeeAvailabilityMapper).updateEntityFromDto(dto, existingEntity);
        verify(employeeAvailabilityRepository).save(existingEntity);
        verify(employeeAvailabilityMapper).toDto(existingEntity);
    }

    @Test
    void updateAvailability_ShouldThrowException_WhenIdDoesNotExist() {
        // Given
        Long nonExistentId = 999L;
        EmployeeAvailabilityDto dto = employeeAvailabilityDto;

        //When
        when(employeeAvailabilityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class,
                () -> employeeAvailabilityService.updateAvailability(nonExistentId, dto));

        //Then
        assertEquals("Availability not found with id: " + nonExistentId, exception.getMessage());
        verify(employeeAvailabilityMapper, never()).updateEntityFromDto(any(), any());
        verify(employeeAvailabilityRepository, never()).save(any());
    }

    /*tests para deleteAvailability*/
    @Test
    void deleteAvailability_ShouldDeleteEntity_WhenIdExists() {
        // Given
        Long id = 1L;
        EmployeeAvailabilityEntity existingEntity = employeeAvailabilityEntity;

        //When
        when(employeeAvailabilityRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        // Llamando a la funcion a testear
        employeeAvailabilityService.deleteAvailability(id);

        // Then
        verify(employeeAvailabilityRepository).findById(id);
        verify(employeeAvailabilityRepository).delete(existingEntity);
    }

    @Test
    void deleteAvailability_ShouldThrowException_WhenIdDoesNotExist() {
        // Given
        Long nonExistentId = 999L;

        //When
        when(employeeAvailabilityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class,
                () -> employeeAvailabilityService.deleteAvailability(nonExistentId));

        //Them
        assertEquals("Availability not found with id: " + nonExistentId, exception.getMessage());
        verify(employeeAvailabilityRepository, never()).delete(any());
    }

}
