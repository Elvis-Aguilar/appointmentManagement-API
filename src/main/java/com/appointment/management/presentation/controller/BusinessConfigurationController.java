package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/Business-Config")
public class BusinessConfigurationController {

    @Autowired
    private BusinessConfigurationService businessConfigurationService;

    // Crear una nueva configuración de negocio
    @PostMapping
    public ResponseEntity<BusinessConfigurationDto> createBusinessConfiguration(
            @RequestBody BusinessConfigurationDto businessConfigurationDto) {
        System.out.println(businessConfigurationDto.maxHoursUpdate());
        BusinessConfigurationDto createdConfig = businessConfigurationService.save(businessConfigurationDto);

        return new ResponseEntity<>(createdConfig, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessConfigurationDto> getBusinessConfigurationById(@PathVariable Long id) {

        BusinessConfigurationDto businessConfiguration = businessConfigurationService.findById(id);

        return new ResponseEntity<>(businessConfiguration, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessConfigurationDto> updateBusinessConfiguration(
            @PathVariable Long id,
            @RequestBody BusinessConfigurationDto businessConfigurationDto) {

        BusinessConfigurationDto updatedConfig = businessConfigurationService.update(id, businessConfigurationDto);

        return new ResponseEntity<>(updatedConfig, HttpStatus.OK);
    }


}
