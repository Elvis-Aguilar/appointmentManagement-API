package com.appointment.management.domain.service.report;


import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DownloadExcelServiceTest {

    @InjectMocks
    private DownloadExcelService downloadExcelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateExcelReport() throws IOException {
        // Datos simulados para el reporte
        List<String> headers = List.of("ID", "Name", "Amount", "Date");
        List<Object> salesData = List.of(1, "Client A", 2500, LocalDate.now(), 2, "Client B", 1500, LocalDate.now());
        String nameReport = "SalesReport";
        BusinessConfigurationDto businessConfig = new BusinessConfigurationDto("Test Company", "http://logo.url");
        String title = "Monthly Sales Report";
        String filtro = "Top Clients";
        String range = "01-01-2024 - 01-31-2024";
        Integer size = salesData.size();

        // Llamar al método a probar
        ResponseEntity<byte[]> response = downloadExcelService.generateExcelReport(
                headers, salesData, nameReport, businessConfig, title, filtro, range, size);

        // Validaciones
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());

        HttpHeaders headersHttp = response.getHeaders();
    }
}


