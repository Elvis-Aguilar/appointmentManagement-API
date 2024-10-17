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

        businessConfigurationEntity = new BusinessConfigurationEntity();
        businessConfigurationEntity.setId(1L);
        businessConfigurationEntity.setName("Default Configuration");
    }

    @Test
    void shouldReturnBusinessConfigurationWhenIdIsValidAndExists() {
        when(businessConfigurationRepository.findById(1L)).thenReturn(Optional.of(businessConfigurationEntity));

        BusinessConfigurationEntity result = businessConfigurationMapperHelper.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Default Configuration", result.getName());

        verify(businessConfigurationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenBusinessConfigurationDoesNotExist() {
        when(businessConfigurationRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessConfigurationEntity result = businessConfigurationMapperHelper.findById(1L);

        assertNull(result);

        verify(businessConfigurationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenIdIsNull() {
        BusinessConfigurationEntity result = businessConfigurationMapperHelper.findById(null);

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
