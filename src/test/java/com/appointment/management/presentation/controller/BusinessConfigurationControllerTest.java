package com.appointment.management.presentation.controller;

import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BusinessConfigurationController.class)
class BusinessConfigurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BusinessConfigurationService businessConfigurationService;

    @InjectMocks
    private BusinessConfigurationController businessConfigurationController;

    @Autowired
    private ObjectMapper objectMapper;

    private BusinessConfigurationDto validDto;

    @BeforeEach
    void setup() {
        validDto = new BusinessConfigurationDto(1L, "Test Business", "logo-url", 1L, null, "Test Description",
                "CORPORATION", 7, 2, BigDecimal.valueOf(100), 7, BigDecimal.valueOf(2));
    }

    @Test
    void shouldCreateBusinessConfiguration() throws Exception {
        when(businessConfigurationService.save(any(BusinessConfigurationDto.class)))
                .thenReturn(validDto);

        mockMvc.perform(post("/business/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Business"))
                .andExpect(jsonPath("$.businessType").value("CORPORATION"));

        verify(businessConfigurationService).save(any(BusinessConfigurationDto.class));
    }

    @Test
    void shouldGetBusinessConfigurationById() throws Exception {
        when(businessConfigurationService.findById(1L)).thenReturn(validDto);

        mockMvc.perform(get("/business/config/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Business"));

        verify(businessConfigurationService).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBusinessConfigurationNotFound() throws Exception {
        when(businessConfigurationService.findById(1L))
                .thenThrow(new ValueNotFoundException("Business configuration not found"));

        mockMvc.perform(get("/business/config/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Business configuration not found"));

        verify(businessConfigurationService).findById(1L);
    }

    @Test
    void shouldUpdateBusinessConfiguration() throws Exception {
        when(businessConfigurationService.update(eq(1L), any(BusinessConfigurationDto.class)))
                .thenReturn(validDto);

        mockMvc.perform(put("/business/config/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Business"));

        verify(businessConfigurationService).update(eq(1L), any(BusinessConfigurationDto.class));
    }
}