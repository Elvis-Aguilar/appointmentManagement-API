package com.appointment.management.presentation.mapper.helpers;

import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.repository.BusinessConfigurationRepository;
import com.appointment.management.presentation.mapper.helpers.BusinessConfigurationMapperHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessConfigurationMapperHelperTest {

    @Mock
    private BusinessConfigurationRepository businessConfigurationRepository;

    @InjectMocks
    private BusinessConfigurationMapperHelper businessConfigurationMapperHelper;

    private BusinessConfigurationEntity businessConfigurationEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
        businessConfigurationEntity = new BusinessConfigurationEntity();
        businessConfigurationEntity.setId(1L);
        businessConfigurationEntity.setName("Default Configuration");
    }

    @Test
    void shouldReturnBusinessConfigurationWhenIdIsValidAndExists() {
        // When
        when(businessConfigurationRepository.findById(1L)).thenReturn(Optional.of(businessConfigurationEntity));

        // Ejecucion del metodo a testear
        BusinessConfigurationEntity result = businessConfigurationMapperHelper.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Default Configuration", result.getName());

        verify(businessConfigurationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenBusinessConfigurationDoesNotExist() {
        // When
        when(businessConfigurationRepository.findById(1L)).thenReturn(Optional.empty());

        // Ejecucion del metodo a testear
        BusinessConfigurationEntity result = businessConfigurationMapperHelper.findById(1L);

        // Then
        assertNull(result);
        verify(businessConfigurationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenIdIsNull() {

        // When
        BusinessConfigurationEntity result = businessConfigurationMapperHelper.findById(null);

        // Then
        assertNull(result);
        verify(businessConfigurationRepository, never()).findById(any());
    }

    @Test
    void shouldReturnIdWhenBusinessConfigurationEntityIsNotNull() {
        Long result = businessConfigurationMapperHelper.toId(businessConfigurationEntity);

        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    void shouldReturnNullWhenBusinessConfigurationEntityIsNull() {
        Long result = businessConfigurationMapperHelper.toId(null);

        assertNull(result);
    }
}
