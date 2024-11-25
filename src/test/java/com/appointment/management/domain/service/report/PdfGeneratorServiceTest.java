package com.appointment.management.domain.service.report;

import static org.junit.jupiter.api.Assertions.*;

import com.lowagie.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PdfGeneratorServiceTest {

    @InjectMocks
    private PdfGeneratorService pdfGeneratorService;

    @Mock
    private ITextRenderer renderer;

    //Variables para el Given Global
    private String htmlContent;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
        htmlContent = "<html><body><h1>Sample PDF</h1></body></html>";
    }

    @Test
    void testGeneratePdfFromHtmlString_Success() throws IOException, DocumentException {

        // When
        doNothing().when(renderer).setDocumentFromString(htmlContent);
        doNothing().when(renderer).layout();
        doNothing().when(renderer).createPDF(any(ByteArrayOutputStream.class));

        // Llamar al método a probar
        byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtmlString(htmlContent);

        // Then
        assertNotNull(pdfBytes);

    }

    @Test
    void testGeneratePdfFromHtmlString_ThrowsIOException() {

        try {
            // When
            pdfGeneratorService.generatePdfFromHtmlString(htmlContent);
        } catch (IOException e) {
            //Then
            assertNotNull(e);
        }
    }
}
