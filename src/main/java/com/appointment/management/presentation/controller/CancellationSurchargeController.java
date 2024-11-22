package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.appoinment.CancellationSurchargeDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.report.*;
import com.appointment.management.domain.service.appointmet.CancellationSurchargeService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.report.DownloadExcelService;
import com.appointment.management.domain.service.report.DownloadPdfService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cancellation")
public class CancellationSurchargeController {

    @Autowired
    private CancellationSurchargeService cancellationSurchargeService;

    @Autowired
    private DownloadPdfService downloadPdfService;

    @Autowired
    private BusinessConfigurationService businessConfigurationService;

    @Autowired
    private DownloadExcelService downloadExcelService;


    @GetMapping
    public ResponseEntity<List<CancellationSurchargeDto>> findAll(){
        List<CancellationSurchargeDto> list = this.cancellationSurchargeService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody clietnReportSendDto dto) {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("items", dto.items());
        templateVariables.put("size", dto.items().size());
        templateVariables.put("filter", dto.filtro());
        templateVariables.put("rangeDate", dto.rangeDate());
        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("nameCompany", busines.name());
        templateVariables.put("companyLogo", busines.logoUrl());

        return this.downloadPdfService.downloadPdf("report-customers", templateVariables);
    }

    @PostMapping("/downloadPNG")
    public ResponseEntity<Resource> downloadReportPng(@RequestBody clietnReportSendDto dto) {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("items", dto.items());
        templateVariables.put("size", dto.items().size());
        templateVariables.put("filter", dto.filtro());
        templateVariables.put("rangeDate", dto.rangeDate());
        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("nameCompany", busines.name());
        templateVariables.put("companyLogo", busines.logoUrl());
        // Renderiza el PDF
        byte[] pdfBytes = this.downloadPdfService.generatePdf("report-customers", templateVariables);

        // Convierte el PDF a PNG
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        convertPdfToPng(pdfBytes, pngOutputStream);

        // Devuelve el archivo PNG como respuesta
        ByteArrayResource resource = new ByteArrayResource(pngOutputStream.toByteArray());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.png\"")
                .body(resource);
    }

    // Método para convertir un PDF a PNG
    private void convertPdfToPng(byte[] pdfBytes, ByteArrayOutputStream outputStream) {
        try (PDDocument document =  Loader.loadPDF(pdfBytes)) { // Carga el PDF desde los bytes
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Renderiza la página 0 con una resolución de 300 DPI
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);

            // Escribe la imagen como PNG en el outputStream
            ImageIO.write(bufferedImage, "PNG", outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error al convertir PDF a PNG", e);
        }
    }

    @PostMapping("/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody clietnReportSendDto dto)throws IOException {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();
        List<clienteReportItemDto> items = dto.items();
        List<String> headers = new ArrayList<>();
        headers.add("Nombre");
        headers.add("email");
        headers.add("cui");
        headers.add("Cantidad citas");
        List<Object> userObjects = new ArrayList<>();
        for (clienteReportItemDto item : items) {
            userObjects.add(item.Nombre() == null ? "" : item.Nombre() );
            userObjects.add(item.email() == null ? "" : item.email());
            userObjects.add(item.cui() == null ? "" : item.cui() );
            userObjects.add(item.Cantidad() == null ? "" : item.Cantidad());
        }

        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_Clientes", busines, "Reporte Clientes", dto.filtro(), dto.rangeDate(), dto.items().size());
    }
}
