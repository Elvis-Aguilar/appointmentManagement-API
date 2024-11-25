package com.appointment.management.domain.service.business;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.enums.BusinessType;
import com.appointment.management.persistance.repository.BusinessConfigurationRepository;
import com.appointment.management.presentation.mapper.business.BusinessConfigurationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class BusinessConfigurationServiceTest {

    @Mock
    private BusinessConfigurationRepository businessConfigurationRepository;

    @Mock
    private BusinessConfigurationMapper businessConfigurationMapper;

    @InjectMocks
    private BusinessConfigurationService businessConfigurationService;

    //variables globales para el Given global
    private BusinessConfigurationDto businessConfigurationDto;
    private BusinessConfigurationEntity businessConfigurationEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given: Global
        businessConfigurationDto = new BusinessConfigurationDto(
                1L,
                "Business Name",
                "http://logo.url",
                1L,
                null,
                "Descripción",
                "SERVICES",
                5,
                12,
                BigDecimal.valueOf(100.00),
                2,
                BigDecimal.valueOf(24.00),
                false
        );
        //Given: Global
        businessConfigurationEntity = new BusinessConfigurationEntity();
        businessConfigurationEntity.setId(1L);
        businessConfigurationEntity.setName("Business Name");
        businessConfigurationEntity.setLogoUrl("http://logo.url");
        businessConfigurationEntity.setBusinessType(BusinessType.SERVICES);
        businessConfigurationEntity.setDescription("Description");
        businessConfigurationEntity.setMaxDaysCancellation(5);
        businessConfigurationEntity.setMaxHoursCancellation(12);
        businessConfigurationEntity.setCancellationSurcharge(BigDecimal.valueOf(100.00));
        businessConfigurationEntity.setMaxDaysUpdate(2);
        businessConfigurationEntity.setMaxHoursUpdate(BigDecimal.valueOf(24.00));
    }

    @DisplayName("Dato una configuracion del negorio"+"Cuando lo guardamos"+ "se espera que el usario se guarde correctamenta")
    @Test
    void testSaveBusinessConfiguration() {

        //When
        when(businessConfigurationMapper.toEntity(any(BusinessConfigurationDto.class)))
                .thenReturn(businessConfigurationEntity);

        when(businessConfigurationRepository.save(any(BusinessConfigurationEntity.class)))
                .thenReturn(businessConfigurationEntity);

        when(businessConfigurationMapper.toDto(any(BusinessConfigurationEntity.class)))
                .thenReturn(businessConfigurationDto);

        BusinessConfigurationDto savedConfig = businessConfigurationService.save(businessConfigurationDto);

        //Then
        assertNotNull(savedConfig);
        assertEquals(this.businessConfigurationDto.name(), savedConfig.name());
        assertEquals(this.businessConfigurationDto.logoUrl(), savedConfig.logoUrl());
        assertEquals(this.businessConfigurationDto.description(), savedConfig.description());
        assertEquals(this.businessConfigurationDto.businessType(), savedConfig.businessType());
        assertEquals(this.businessConfigurationDto.admin(), savedConfig.admin());
        assertEquals(this.businessConfigurationDto.cancellationSurcharge(), savedConfig.cancellationSurcharge());
        assertEquals(this.businessConfigurationDto.maxDaysCancellation(), savedConfig.maxDaysCancellation());
        assertEquals(this.businessConfigurationDto.maxHoursCancellation(), savedConfig.maxHoursCancellation());
        assertEquals(this.businessConfigurationDto.maxDaysUpdate(), savedConfig.maxDaysUpdate());
        assertEquals(this.businessConfigurationDto.maxHoursUpdate(), savedConfig.maxHoursUpdate());
    }

    @DisplayName("Dato un id"+"Cuando lo buscamos"+ "se espera la configuracion correcta")
    @Test
    void shouldReturnBusinessConfigurationWhenIdExists() {
        //Given
        Long id = 1L;

        //When
        when(businessConfigurationRepository.findById(id)).thenReturn(Optional.of(this.businessConfigurationEntity));
        when(businessConfigurationMapper.toDto(this.businessConfigurationEntity)).thenReturn(this.businessConfigurationDto);

        //llamado de la funcion a testear
        BusinessConfigurationDto result = businessConfigurationService.findById(id);

        //Then
        assertNotNull(result);
        assertEquals(this.businessConfigurationDto.name(), result.name());
    }

    @DisplayName("Dato un id inexistente"+"Cuando lo buscamos"+ "se espera una excepcion de ValueNotFoundException")
    @Test
    void shouldThrowExceptionWhenBusinessConfigurationNotFound() {
        //Given
        Long id = 999L;

        //When
        when(businessConfigurationRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException thrown = assertThrows(
                ValueNotFoundException.class,
                () -> businessConfigurationService.findById(id),
                "Expected findById() to throw, but it didn't"
        );

        //Then
        assertTrue(thrown.getMessage().contains("Business configuration not found with ID: " + id));
    }

    @DisplayName("Dato un id y cambio de configuraciones"+"Cuando modificamos"+ "se espera que se actulize la configuracioens correctamente")
    @Test
    void shouldUpdateBusinessConfiguration() {
        // Given
        Long id = 1L;
        BusinessConfigurationEntity updatedEntity = new BusinessConfigurationEntity();
        updatedEntity.setId(id);
        updatedEntity.setName("Updated Business");

        //When
        when(businessConfigurationRepository.findById(id)).thenReturn(Optional.of(this.businessConfigurationEntity));
        when(businessConfigurationMapper.toEntity(this.businessConfigurationDto)).thenReturn(updatedEntity);
        when(businessConfigurationRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(businessConfigurationMapper.toDto(updatedEntity)).thenReturn(this.businessConfigurationDto);

        //Llamado de funcion a testear
        BusinessConfigurationDto result = businessConfigurationService.update(id, this.businessConfigurationDto);

        //Then
        assertNotNull(result);
        assertEquals(this.businessConfigurationDto.name(), result.name());
    }

    @DisplayName("Dato un id invalido y cambio de configuraciones"+"Cuando modificamos"+ "da una excepcion ValueNotFoundException")
    @Test
    void shouldThrowExceptionWhenUpdateBusinessConfigurationNotFount() {
        // Given
        Long id = 1999L;

        //When
        when(businessConfigurationRepository.findById(id)).thenReturn(Optional.empty());

        ValueNotFoundException thrown = assertThrows(
                ValueNotFoundException.class,
                () -> businessConfigurationService.update(id, this.businessConfigurationDto),
                "Expected findById() to throw, but it didn't"
        );

        //Then
        assertTrue(thrown.getMessage().contains("Configuracion del negocio no encontradas con el Id: " + id));
    }

    @DisplayName("Cuando se busca la primera configuración del negocio y existe" +
            " se espera que se devuelva la configuración correcta")
    @Test
    void shouldReturnFirstBusinessConfigurationWhenExists() {
        // Given
        BusinessConfigurationEntity firstEntity = new BusinessConfigurationEntity();
        firstEntity.setId(1L);
        firstEntity.setName("First Business");

        BusinessConfigurationDto firstDto = new BusinessConfigurationDto(
                firstEntity.getName(),
                firstEntity.getLogoUrl()
        );

        // When
        when(businessConfigurationRepository.findFirstByOrderByIdAsc())
                .thenReturn(Optional.of(firstEntity));
        when(businessConfigurationMapper.toDto(firstEntity)).thenReturn(firstDto);

        //Llamado de funcion a testear
        BusinessConfigurationDto result = businessConfigurationService.findFirst();

        // Then
        assertNotNull(result);
        assertEquals(firstDto.name(), result.name());
        assertEquals(firstDto.logoUrl(), result.logoUrl());
        assertEquals(firstDto.description(), result.description());
    }

    @DisplayName("Cuando se busca la primera configuración del negocio y no existe" +
            " se espera una excepción de ValueNotFoundException")
    @Test
    void shouldThrowExceptionWhenNoBusinessConfigurationFound() {
        // When
        when(businessConfigurationRepository.findFirstByOrderByIdAsc())
                .thenReturn(Optional.empty());

        // When
        ValueNotFoundException thrown = assertThrows(
                ValueNotFoundException.class,
                () -> businessConfigurationService.findFirst(),
                "Expected findFirst() to throw, but it didn't"
        );

        //Then
        assertTrue(thrown.getMessage().contains("No business configuration found"));
    }


}