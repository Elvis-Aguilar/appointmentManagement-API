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

    //Variables globales para el Given global
    private String templateName;
    private Map<String, Object> templateVariables;
    private String renderedHtml;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //Given Global
        templateName = "report-template";
        templateVariables = Map.of("key", "value");
        renderedHtml = "<html><body><h1>Test Report</h1></body></html>";

    }

    @Test
    void testDownloadPdf_Success() throws IOException {
        // Given
        byte[] pdfBytes = new byte[]{1, 2, 3};

        //  When
        when(templateRendererService.renderTemplate(templateName, templateVariables)).thenReturn(renderedHtml);
        when(pdfGeneratorService.generatePdfFromHtmlString(renderedHtml)).thenReturn(pdfBytes);

        // Llamar al método a Testear
        ResponseEntity<Resource> response = downloadPdfService.downloadPdf(templateName, templateVariables);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("attachment; filename=\"pdf-test.pdf\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertNotNull(response.getBody());

        verify(templateRendererService).renderTemplate(templateName, templateVariables);
        verify(pdfGeneratorService).generatePdfFromHtmlString(renderedHtml);
    }

    @Test
    void testDownloadPdf_Failure() throws IOException {

        // When
        when(templateRendererService.renderTemplate(templateName, templateVariables)).thenReturn(renderedHtml);
        when(pdfGeneratorService.generatePdfFromHtmlString(renderedHtml)).thenThrow(new IOException("PDF generation error"));

        // Llamar al método a probar
        ResponseEntity<Resource> response = downloadPdfService.downloadPdf(templateName, templateVariables);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        verify(templateRendererService).renderTemplate(templateName, templateVariables);
        verify(pdfGeneratorService).generatePdfFromHtmlString(renderedHtml);
    }
}

