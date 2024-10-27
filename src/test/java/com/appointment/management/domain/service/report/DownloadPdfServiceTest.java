package com.appointment.management.domain.service.report;

import static org.junit.jupiter.api.Assertions.*;

import com.appointment.management.domain.service.auth.TemplateRendererService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DownloadPdfServiceTest {

    @InjectMocks
    private DownloadPdfService downloadPdfService;

    @Mock
    private TemplateRendererService templateRendererService;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDownloadPdf_Success() throws IOException {
        // Configurar los datos de prueba
        String templateName = "report-template";
        Map<String, Object> templateVariables = Map.of("key", "value");
        String renderedHtml = "<html><body><h1>Test Report</h1></body></html>";
        byte[] pdfBytes = new byte[]{1, 2, 3};

        // Configurar el comportamiento simulado de las dependencias
        when(templateRendererService.renderTemplate(templateName, templateVariables)).thenReturn(renderedHtml);
        when(pdfGeneratorService.generatePdfFromHtmlString(renderedHtml)).thenReturn(pdfBytes);

        // Llamar al método a probar
        ResponseEntity<Resource> response = downloadPdfService.downloadPdf(templateName, templateVariables);

        // Validaciones
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("attachment; filename=\"pdf-test.pdf\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertNotNull(response.getBody());

        // Verificar las interacciones con las dependencias
        verify(templateRendererService).renderTemplate(templateName, templateVariables);
        verify(pdfGeneratorService).generatePdfFromHtmlString(renderedHtml);
    }

    @Test
    void testDownloadPdf_Failure() throws IOException {
        // Configurar los datos de prueba
        String templateName = "report-template";
        Map<String, Object> templateVariables = Map.of("key", "value");
        String renderedHtml = "<html><body><h1>Test Report</h1></body></html>";

        // Configurar el comportamiento simulado de las dependencias para lanzar una excepción
        when(templateRendererService.renderTemplate(templateName, templateVariables)).thenReturn(renderedHtml);
        when(pdfGeneratorService.generatePdfFromHtmlString(renderedHtml)).thenThrow(new IOException("PDF generation error"));

        // Llamar al método a probar
        ResponseEntity<Resource> response = downloadPdfService.downloadPdf(templateName, templateVariables);

        // Validaciones
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        // Verificar las interacciones con las dependencias
        verify(templateRendererService).renderTemplate(templateName, templateVariables);
        verify(pdfGeneratorService).generatePdfFromHtmlString(renderedHtml);
    }
}

