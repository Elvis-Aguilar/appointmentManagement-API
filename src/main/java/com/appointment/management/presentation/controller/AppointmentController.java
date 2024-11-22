package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.domain.dto.report.AppointmentReportDto;
import com.appointment.management.domain.dto.report.AppointmentReportItemDto;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.appointmet.AppointmetnService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.business.ServiceService;
import com.appointment.management.domain.service.report.DownloadExcelService;
import com.appointment.management.domain.service.report.DownloadPdfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.Loader;



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
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmetnService appointmentService;

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


    public AppointmentController(AppointmetnService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointments(@PathVariable Long id) {
        List<AppointmentDto> appointments = appointmentService.getAllAppointmentsByEmployeeId(id);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody @Valid AppointmentDto appointmentDto) {
        AppointmentDto createdAppointment = appointmentService.createAppointment(appointmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable Long id,
                                                            @RequestBody @Valid AppointmentDto appointmentDto) {
        AppointmentDto updatedAppointment = appointmentService.updateAppointment(id, appointmentDto);
        return ResponseEntity.ok(updatedAppointment);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateServiceStatusCompleted(@PathVariable Long id, @RequestBody String status) {
        AppointmentDto result = this.appointmentService.completedAppointment(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/canceled/{id}")
    public ResponseEntity<AppointmentDto> updateServiceStatusCanceled(@PathVariable Long id, @RequestBody String status) {
        AppointmentDto result = this.appointmentService.canceledAppointment(id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody AppointmentReportDto appointmentReportDto) {
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

    @PostMapping("/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody AppointmentReportDto dto)throws IOException {
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

    @PostMapping("/downloadBill/{id}")
    public ResponseEntity<Resource> downloadBill(@PathVariable Long id, @RequestBody String status) {

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

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable Long id) {
        AppointmentDto canceledAppointment = appointmentService.stateCancelAppointment(id);
        return ResponseEntity.ok(canceledAppointment);
    }

    @PostMapping("/downloadPNG")
    public ResponseEntity<Resource> downloadReportPng(@RequestBody AppointmentReportDto appointmentReportDto) {
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


}
