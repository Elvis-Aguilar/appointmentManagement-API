package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.domain.service.business.ServiceService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

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
}
