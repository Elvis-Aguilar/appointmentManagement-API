package com.appointment.management.domain.service.report;

import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.domain.dto.report.*;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.appointmet.AppointmetnService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.business.ServiceService;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class DownloadReport {

    @Autowired
    private  DownloadPdfService downloadPdfService;

    @Autowired
    private BusinessConfigurationService businessConfigurationService;

    @Autowired
    private DownloadExcelService downloadExcelService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    private final AppointmetnService appointmentService;

    public DownloadReport(AppointmetnService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * para exportar lo relacionado con appointment
     */

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

    @PostMapping("/appointment/downloadPDF")
    public ResponseEntity<Resource> appointmentDownloadReport(@RequestBody AppointmentReportDto appointmentReportDto) {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("items", appointmentReportDto.items());
        templateVariables.put("total", appointmentReportDto.total());
        templateVariables.put("size", appointmentReportDto.items().size());
        templateVariables.put("filter", appointmentReportDto.filtro());
        templateVariables.put("rangeDate", appointmentReportDto.rangeDate());
        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("nameCompany", busines.name());
        templateVariables.put("companyLogo", busines.logoUrl());

        return this.downloadPdfService.downloadPdf("report-appointment", templateVariables);
    }

    @PostMapping("/appointment/download-excel")
    public ResponseEntity<byte[]> appointmentDownloadReportExcel(@RequestBody AppointmentReportDto dto)throws IOException {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();
        List<AppointmentReportItemDto> items = dto.items();
        List<String> headers = new ArrayList<>();
        headers.add("Cliente");
        headers.add("Servicio");
        headers.add("Fecha");
        headers.add("Hora");
        headers.add("Empleado");
        headers.add("Precio Q");
        List<Object> userObjects = new ArrayList<>();
        for (AppointmentReportItemDto item : items) {
            userObjects.add(item.cliente() == null ? "" : item.cliente());
            userObjects.add(item.servicio() == null ? "" : item.servicio());
            userObjects.add(item.fecha() == null ? "" : item.fecha() );
            userObjects.add(item.horaInicio() == null ? "" : item.horaInicio());
            userObjects.add(item.empleado() == null ? "" : item.empleado());
            userObjects.add(item.price() == null ? "" : item.price());
        }
        userObjects.add("Total General");
        userObjects.add("");
        userObjects.add("");
        userObjects.add("");
        userObjects.add("");
        userObjects.add(dto.total());
        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_Citas", busines, "Reporte Citas", dto.filtro(), dto.rangeDate(), dto.items().size());
    }

    @PostMapping("/appointment/downloadBill/{id}")
    public ResponseEntity<Resource> appointmentDownloadBill(@PathVariable Long id, @RequestBody String status) {

        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();
        AppointmentDto appointmentDto = this.appointmentService.getAppointmentById(id).orElseThrow();
        LocalDate date = LocalDate.now();
        UserDto user = userService.findUserById(appointmentDto.customer()).orElseThrow();
        ServiceDto service = this.serviceService.getServiceById(appointmentDto.service());
        LocalDate dateService = appointmentDto.startDate().toLocalDate();

        String oter = appointmentDto.fine() ? "Multa por cancelar fuera del tiempo permitido": "--";

        BigDecimal fine = appointmentDto.fine() ? busines.cancellationSurcharge() : new BigDecimal("0.00");

        BigDecimal total = fine.add(service.price());

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("rangeDate", date);
        templateVariables.put("nameCompany", busines.name());
        templateVariables.put("companyLogo", busines.logoUrl());
        templateVariables.put("nameCliente", user.name());
        templateVariables.put("nit", user.nit());
        templateVariables.put("servicio", service.name());
        templateVariables.put("fecha", dateService);
        templateVariables.put("price", service.price());
        templateVariables.put("total", total);
        templateVariables.put("oter", oter);
        templateVariables.put("fine", fine);

        return this.downloadPdfService.downloadPdf("bill-download", templateVariables);
    }

    @PostMapping("/appointment/downloadPNG")
    public ResponseEntity<Resource> appointmentDownloadReportPng(@RequestBody AppointmentReportDto appointmentReportDto) {
        // Generar PDF primero (usando el método existente)
        BusinessConfigurationDto business = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("items", appointmentReportDto.items());
        templateVariables.put("total", appointmentReportDto.total());
        templateVariables.put("size", appointmentReportDto.items().size());
        templateVariables.put("filter", appointmentReportDto.filtro());
        templateVariables.put("rangeDate", appointmentReportDto.rangeDate());
        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("nameCompany", business.name());
        templateVariables.put("companyLogo", business.logoUrl());

        // Renderiza el PDF
        byte[] pdfBytes = this.downloadPdfService.generatePdf("report-appointment", templateVariables);

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

    /**
     * para exportar lo relacionado con cancelacion, reporte de clientes y empleados
     */
    @PostMapping("/cancellation/downloadPDF")
    public ResponseEntity<Resource> cancellationDownloadReport(@RequestBody clietnReportSendDto dto) {
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

    @PostMapping("/cancellation/downloadPNG")
    public ResponseEntity<Resource> cancellationDownloadReportPng(@RequestBody clietnReportSendDto dto) {
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

    @PostMapping("/cancellation/download-excel")
    public ResponseEntity<byte[]> cancellationDownloadReportExcel(@RequestBody clietnReportSendDto dto)throws IOException {
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


    /**
     * para exportar lo relacionado con cancelacion
     */
    @PostMapping("/services/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody ServiceSendDto dto) {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("items", dto.items());
        templateVariables.put("total", dto.total());
        templateVariables.put("size", dto.items().size());
        templateVariables.put("filter", dto.filtro());
        templateVariables.put("rangeDate", dto.rangeDate());
        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("nameCompany", busines.name());
        templateVariables.put("companyLogo", busines.logoUrl());

        return this.downloadPdfService.downloadPdf("report-services", templateVariables);
    }

    @PostMapping("/services/downloadPNG")
    public ResponseEntity<Resource> downloadReportPng(@RequestBody ServiceSendDto dto) {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("items", dto.items());
        templateVariables.put("total", dto.total());
        templateVariables.put("size", dto.items().size());
        templateVariables.put("filter", dto.filtro());
        templateVariables.put("rangeDate", dto.rangeDate());
        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("nameCompany", busines.name());
        templateVariables.put("companyLogo", busines.logoUrl());

        // Renderiza el PDF
        byte[] pdfBytes = this.downloadPdfService.generatePdf("report-services", templateVariables);

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

    @PostMapping("/services/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody ServiceSendDto dto)throws IOException {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();
        List<ServiceItemDto> items = dto.items();
        List<String> headers = new ArrayList<>();
        headers.add("Nombre");
        headers.add("Descripcion");
        headers.add("Duracion (H)");
        headers.add("Estado");
        headers.add("Precio Q");
        headers.add("Citas");
        List<Object> userObjects = new ArrayList<>();
        for (ServiceItemDto item : items) {
            userObjects.add(item.name() == null ? "" : item.name());
            userObjects.add(item.description() == null ? "" : item.description());
            userObjects.add(item.duration() == null ? "" : item.duration() );
            userObjects.add(item.status() == null ? "" : item.status());
            userObjects.add(item.price() == null ? "" : item.price());
            userObjects.add(item.citas() == null ? "" : item.citas());
        }
        userObjects.add("Total General");
        userObjects.add("");
        userObjects.add("");
        userObjects.add("");
        userObjects.add("");
        userObjects.add(dto.total());
        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_Servicios", busines, "Reporte Servicios", dto.filtro(), dto.rangeDate(), dto.items().size());
    }
}
