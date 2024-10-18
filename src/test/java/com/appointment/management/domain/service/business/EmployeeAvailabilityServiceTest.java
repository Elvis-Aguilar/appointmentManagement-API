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

    private EmployeeAvailabilityEntity employeeAvailabilityEntity;
    private EmployeeAvailabilityDto employeeAvailabilityDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

         this.employeeAvailabilityDto = new EmployeeAvailabilityDto(
                1L,
                123L,
                "MONDAY",
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null
        );

         this.employeeAvailabilityEntity = new EmployeeAvailabilityEntity();
         this.employeeAvailabilityEntity.setId(1L);
         this.employeeAvailabilityEntity.setEndTime(LocalTime.of(9, 0));
    }

    /*tests para getAllAvailabilities*/
    @Test
    void getAllAvailabilities_ShouldReturnEmptyList_WhenNoAvailabilities() {
        // Arrange
        when(employeeAvailabilityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAllAvailabilities();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verificar que el repositorio fue llamado una vez
        verify(employeeAvailabilityRepository, times(1)).findAll();
        // Verificar que el mapper no fue llamado, ya que no hay entidades
        verifyNoInteractions(employeeAvailabilityMapper);
    }

    @Test
    void getAllAvailabilities_ShouldReturnListOfAvailabilities_WhenThereAreAvailabilities() {
        // Simulamos que el repositorio devuelve una lista con una entidad
        when(employeeAvailabilityRepository.findAll()).thenReturn(List.of(employeeAvailabilityEntity));

        // Simulamos que el mapper convierte la entidad a DTO
        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        // Act
        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAllAvailabilities();

        // Assert
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
        Long employeeId = 123L;

        when(employeeAvailabilityRepository.findAllByEmployeeId(employeeId)).thenReturn(Collections.emptyList());

        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAvailabilitiesByEmployeeId(employeeId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeAvailabilityRepository, times(1)).findAllByEmployeeId(employeeId);
        verifyNoInteractions(employeeAvailabilityMapper);
    }

    @Test
    void getAvailabilitiesByEmployeeId_ShouldReturnListOfAvailabilities_WhenThereAreAvailabilities() {
        Long employeeId = 123L;

        when(employeeAvailabilityRepository.findAllByEmployeeId(employeeId)).thenReturn(List.of(employeeAvailabilityEntity));

        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.getAvailabilitiesByEmployeeId(employeeId);

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
        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);

        when(employeeAvailabilityRepository.save(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityEntity);

        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        EmployeeAvailabilityDto result = employeeAvailabilityService.createAvailability(employeeAvailabilityDto);

        assertNotNull(result);
        assertEquals(employeeAvailabilityDto, result);

        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);

        verify(employeeAvailabilityRepository, times(1)).save(employeeAvailabilityEntity);

        verify(employeeAvailabilityMapper, times(1)).toDto(employeeAvailabilityEntity);
    }

    @Test
    void createAvailability_ShouldThrowException_WhenSaveFails() {
        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);

        when(employeeAvailabilityRepository.save(employeeAvailabilityEntity)).thenThrow(new RuntimeException("Error al guardar la entidad"));

        assertThrows(RuntimeException.class, () -> {
            employeeAvailabilityService.createAvailability(employeeAvailabilityDto);
        });

        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);

        verify(employeeAvailabilityRepository, times(1)).save(employeeAvailabilityEntity);

        verify(employeeAvailabilityMapper, times(0)).toDto(any());
    }

    /*tests para createAllList*/
    @Test
    void createAllList_ShouldReturnDtoList_WhenEntitiesAreSaved() {
        List<EmployeeAvailabilityDto> dtoList = List.of(employeeAvailabilityDto);
        List<EmployeeAvailabilityEntity> entityList = List.of(employeeAvailabilityEntity);

        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);

        when(employeeAvailabilityRepository.saveAll(entityList)).thenReturn(entityList);

        when(employeeAvailabilityMapper.toDto(employeeAvailabilityEntity)).thenReturn(employeeAvailabilityDto);

        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.createAllList(dtoList);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employeeAvailabilityDto, result.getFirst());

        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);
        verify(employeeAvailabilityRepository, times(1)).saveAll(entityList);
        verify(employeeAvailabilityMapper, times(1)).toDto(employeeAvailabilityEntity);
    }

    @Test
    void createAllList_ShouldReturnEmptyList_WhenInputIsEmpty() {
        List<EmployeeAvailabilityDto> emptyDtoList = List.of();

        List<EmployeeAvailabilityDto> result = employeeAvailabilityService.createAllList(emptyDtoList);

        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

    @Test
    void createAllList_ShouldThrowException_WhenSaveFails() {
        List<EmployeeAvailabilityDto> dtoList = List.of(employeeAvailabilityDto);
        List<EmployeeAvailabilityEntity> entityList = List.of(employeeAvailabilityEntity);

        when(employeeAvailabilityMapper.toEntity(employeeAvailabilityDto)).thenReturn(employeeAvailabilityEntity);

        when(employeeAvailabilityRepository.saveAll(entityList)).thenThrow(new RuntimeException("Error al guardar entidades"));

        assertThrows(RuntimeException.class, () -> {
            employeeAvailabilityService.createAllList(dtoList);
        });

        verify(employeeAvailabilityMapper, times(1)).toEntity(employeeAvailabilityDto);
        verify(employeeAvailabilityRepository, times(1)).saveAll(entityList);

        verify(employeeAvailabilityMapper, times(0)).toDto(any());
    }

    /*tests para updateAvailability*/
    @Test
    void updateAvailability_ShouldUpdateEntity_WhenIdExists() {
        // Arrange
        Long id = 1L;
        EmployeeAvailabilityDto dto = employeeAvailabilityDto;
        EmployeeAvailabilityEntity existingEntity = employeeAvailabilityEntity;

        when(employeeAvailabilityRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(employeeAvailabilityMapper).updateEntityFromDto(dto, existingEntity);
        when(employeeAvailabilityRepository.save(existingEntity)).thenReturn(existingEntity);
        when(employeeAvailabilityMapper.toDto(existingEntity)).thenReturn(dto);

        // Act
        EmployeeAvailabilityDto result = employeeAvailabilityService.updateAvailability(id, dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto, result);

        // Verificar que los métodos se llamaron correctamente
        verify(employeeAvailabilityRepository).findById(id);
        verify(employeeAvailabilityMapper).updateEntityFromDto(dto, existingEntity);
        verify(employeeAvailabilityRepository).save(existingEntity);
        verify(employeeAvailabilityMapper).toDto(existingEntity);
    }

    @Test
    void updateAvailability_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        EmployeeAvailabilityDto dto = employeeAvailabilityDto;

        when(employeeAvailabilityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class,
                () -> employeeAvailabilityService.updateAvailability(nonExistentId, dto));

        assertEquals("Availability not found with id: " + nonExistentId, exception.getMessage());

        // Verificar que no se intentó actualizar o guardar cuando el ID no existe
        verify(employeeAvailabilityMapper, never()).updateEntityFromDto(any(), any());
        verify(employeeAvailabilityRepository, never()).save(any());
    }

    /*tests para deleteAvailability*/
    @Test
    void deleteAvailability_ShouldDeleteEntity_WhenIdExists() {
        // Arrange
        Long id = 1L;
        EmployeeAvailabilityEntity existingEntity = employeeAvailabilityEntity; // Entidad inicializada en el setUp

        when(employeeAvailabilityRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        // Act
        employeeAvailabilityService.deleteAvailability(id);

        // Assert
        verify(employeeAvailabilityRepository).findById(id);
        verify(employeeAvailabilityRepository).delete(existingEntity);
    }

    @Test
    void deleteAvailability_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;

        when(employeeAvailabilityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class,
                () -> employeeAvailabilityService.deleteAvailability(nonExistentId));

        assertEquals("Availability not found with id: " + nonExistentId, exception.getMessage());

        // Verificar que no se intentó eliminar ninguna entidad
        verify(employeeAvailabilityRepository, never()).delete(any());
    }

}
