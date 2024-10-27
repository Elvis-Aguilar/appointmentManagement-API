package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.domain.dto.report.AppointmentReportDto;
import com.appointment.management.domain.dto.report.ServiceItemDto;
import com.appointment.management.domain.dto.report.ServiceSendDto;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.business.ServiceService;
import com.appointment.management.domain.service.report.DownloadExcelService;
import com.appointment.management.domain.service.report.DownloadPdfService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;
    private final DownloadPdfService downloadPdfService;
    private final BusinessConfigurationService businessConfigurationService;
    private final DownloadExcelService downloadExcelService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        List<ServiceDto> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ServiceDto>> getAvailableServices() {
        List<ServiceDto> services = serviceService.getAllServicesAvailable();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/unavailable")
    public ResponseEntity<List<ServiceDto>> getUnavailableServices() {
        List<ServiceDto> services = serviceService.getAllServicesUnavailable();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable Long id) {
        ServiceDto serviceDto = serviceService.getServiceById(id);
        return ResponseEntity.ok(serviceDto);
    }

    @PostMapping
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody ServiceDto serviceDto) {
        ServiceDto createdService = serviceService.createService(serviceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDto serviceDto) {
        ServiceDto updatedService = serviceService.updateService(id, serviceDto);
        return ResponseEntity.ok(updatedService);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServiceDto> updateServiceStatus(@PathVariable Long id, @RequestBody String status) {
        ServiceDto updatedService = serviceService.updateServiceStatus(id, status);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody ServiceSendDto dto) {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = Map.of(
                "items",dto.items(),
                "total", dto.total(),
                "size", dto.items().size(),
                "filter", dto.filtro(),
                "rangeDate", dto.rangeDate(),
                "dateReport", LocalDate.now(),
                "nameCompany", busines.name(),
                "companyLogo", busines.logoUrl()
        );
        return this.downloadPdfService.downloadPdf("report-services", templateVariables);
    }

    @PostMapping("/download-excel")
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
