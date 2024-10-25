package com.appointment.management.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.domain.service.appointmet.AppointmetnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @InjectMocks
    private AppointmentController appointmentController;

    @Mock
    private AppointmetnService appointmentService;

    private AppointmentDto appointmentDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentDto = new AppointmentDto(
                1L,
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "CONFIRMED",
                "CREDIT_CARD"
        );
    }

    @Test
    public void getAllAppointments_ShouldReturnAppointments() {
        // Given
        List<AppointmentDto> appointments = Arrays.asList(appointmentDto);
        when(appointmentService.getAllAppointments()).thenReturn(appointments);

        // When
        ResponseEntity<List<AppointmentDto>> response = appointmentController.getAllAppointments();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
    }

    @Test
    public void getAppointmentById_ShouldReturnAppointment_WhenExists() {
        // Given
        when(appointmentService.getAppointmentById(anyLong())).thenReturn(Optional.of(appointmentDto));

        // When
        ResponseEntity<AppointmentDto> response = appointmentController.getAppointmentById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDto, response.getBody());
    }

    @Test
    public void getAppointmentById_ShouldReturnNotFound_WhenDoesNotExist() {
        // Given
        when(appointmentService.getAppointmentById(anyLong())).thenReturn(Optional.empty());

        // When
        ResponseEntity<AppointmentDto> response = appointmentController.getAppointmentById(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createAppointment_ShouldReturnCreatedAppointment() {
        // Given
        when(appointmentService.createAppointment(any(AppointmentDto.class))).thenReturn(appointmentDto);

        // When
        ResponseEntity<AppointmentDto> response = appointmentController.createAppointment(appointmentDto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(appointmentDto, response.getBody());
    }

    @Test
    public void updateAppointment_ShouldReturnUpdatedAppointment() {
        // Given
        when(appointmentService.updateAppointment(anyLong(), any(AppointmentDto.class))).thenReturn(appointmentDto);

        // When
        ResponseEntity<AppointmentDto> response = appointmentController.updateAppointment(1L, appointmentDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDto, response.getBody());
    }

    @Test
    public void deleteAppointment_ShouldReturnNoContent() {
        // Given
        doNothing().when(appointmentService).deleteAppointment(anyLong());

        // When
        ResponseEntity<Void> response = appointmentController.deleteAppointment(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
