package com.appointment.management.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.report.AppointmentReportDto;
import com.appointment.management.domain.dto.report.AppointmentReportItemDto;
import com.appointment.management.domain.dto.report.clienteReportItemDto;
import com.appointment.management.domain.service.appointmet.AppointmetnService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.report.DownloadPdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {



    @Mock
    private AppointmetnService appointmentService;

    @Mock
    private BusinessConfigurationService businessConfigurationService;

    @Mock
    private DownloadPdfService downloadPdfService;


    @InjectMocks
    private AppointmentController appointmentController;

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
                "CREDIT_CARD", false
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

    @Test
    public void updateServiceStatusCompleted_ShouldReturnUpdatedAppointment() {
        // Given
        String status = "COMPLETED";
        when(appointmentService.completedAppointment(anyLong())).thenReturn(appointmentDto);

        // When
        ResponseEntity<AppointmentDto> response = appointmentController.updateServiceStatusCompleted(1L, status);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDto, response.getBody());
    }

    @Test
    public void updateServiceStatusCanceled_ShouldReturnUpdatedAppointment() {
        // Given
        String status = "CANCELED";
        when(appointmentService.canceledAppointment(anyLong())).thenReturn(appointmentDto);

        // When
        ResponseEntity<AppointmentDto> response = appointmentController.updateServiceStatusCanceled(1L, status);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDto, response.getBody());
    }

 /*
 *    @Test
    void testDownloadReport() {
        // Given
        AppointmentReportDto appointmentReportDto = new AppointmentReportDto(
                List.of(new AppointmentReportItemDto("afd","fadf","afdf","adfa","adfa","afdfad", 5)),  // Simulando los items
                150,        // Simulando el total
                "Test Filter", // Simulando el filtro
                "01-10-2024 - 01-11-2024" // Simulando el rango de fechas
        );

        BusinessConfigurationDto businessConfig = new BusinessConfigurationDto("Test Company", "http://logo.url");

        when(businessConfigurationService.findFirst()).thenReturn(businessConfig);

        Map<String, Object> expectedVariables = Map.of(
                "items", appointmentReportDto.items(),
                "total", appointmentReportDto.total(),
                "size", appointmentReportDto.items().size(),
                "filter", appointmentReportDto.filtro(),
                "rangeDate", appointmentReportDto.rangeDate(),
                "dateReport", LocalDate.now(),
                "nameCompany", businessConfig.name(),
                "companyLogo", businessConfig.logoUrl()
        );

        // Simulación del recurso PDF
        ByteArrayResource resource = new ByteArrayResource(new byte[]{1, 2, 3});
        ResponseEntity<Resource> mockResponse = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-appointment.pdf")
                .body(resource);

        when(downloadPdfService.downloadPdf("report-appointment", expectedVariables)).thenReturn(mockResponse);

        // When
        ResponseEntity<Resource> response = appointmentController.downloadReport(appointmentReportDto);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals(resource, response.getBody());

        verify(businessConfigurationService).findFirst();
        verify(downloadPdfService).downloadPdf("report-appointment", expectedVariables);
    }*/

}
