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

}
