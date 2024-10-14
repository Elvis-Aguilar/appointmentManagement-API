package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.domain.dto.business.EmployeeAvailabilityDto;
import com.appointment.management.domain.service.business.EmployeeAvailabilityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee/availability")
@RequiredArgsConstructor
public class EmployeeAvailabilityController {

    private final EmployeeAvailabilityService employeeAvailabilityService;

    @GetMapping
    public ResponseEntity<List<EmployeeAvailabilityDto>> getAllAvailabilities() {
        List<EmployeeAvailabilityDto> availabilities = employeeAvailabilityService.getAllAvailabilities();
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeAvailabilityDto>> getAvailabilitiesByEmployeeId(@Valid @PathVariable @Positive Long employeeId) {
        List<EmployeeAvailabilityDto> availabilities = employeeAvailabilityService.getAvailabilitiesByEmployeeId(employeeId);
        return ResponseEntity.ok(availabilities);
    }

    @PostMapping
    public ResponseEntity<EmployeeAvailabilityDto> createAvailability(@Valid @RequestBody EmployeeAvailabilityDto dto) {
        EmployeeAvailabilityDto savedDto = employeeAvailabilityService.createAvailability(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @PostMapping("/weeks-day")
    public ResponseEntity<List<EmployeeAvailabilityDto>> createAvailabilityWeeksDay(@Valid @RequestBody List<EmployeeAvailabilityDto> dtos) {
        List<EmployeeAvailabilityDto> created = this.employeeAvailabilityService.createAllList(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeAvailabilityDto> updateAvailability(@Valid @PathVariable @Positive Long id, @Valid @RequestBody EmployeeAvailabilityDto dto) {
        EmployeeAvailabilityDto updatedDto = employeeAvailabilityService.updateAvailability(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@Valid @PathVariable @Positive Long id) {
        employeeAvailabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
