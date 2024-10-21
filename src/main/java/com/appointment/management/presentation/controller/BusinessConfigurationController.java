package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/business/config")
public class BusinessConfigurationController {

    @Autowired
    private BusinessConfigurationService businessConfigurationService;

    // Crear una nueva configuración de negocio
    @PostMapping
    public ResponseEntity<BusinessConfigurationDto> createBusinessConfiguration(
            @Valid @RequestBody BusinessConfigurationDto businessConfigurationDto) {

        BusinessConfigurationDto createdConfig = businessConfigurationService.save(businessConfigurationDto);

        return new ResponseEntity<>(createdConfig, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessConfigurationDto> getBusinessConfigurationById(
            @Valid @PathVariable @Positive Long id) {

        BusinessConfigurationDto businessConfiguration = businessConfigurationService.findById(id);

        return new ResponseEntity<>(businessConfiguration, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<BusinessConfigurationDto> getBusinessConfigurationByFirst() {

        BusinessConfigurationDto businessConfiguration = businessConfigurationService.findFirst();

        return new ResponseEntity<>(businessConfiguration, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessConfigurationDto> updateBusinessConfiguration(
            @Valid @PathVariable @Positive Long id,
            @Valid @RequestBody BusinessConfigurationDto businessConfigurationDto) {
        System.out.println(businessConfigurationDto.toString());
        BusinessConfigurationDto updatedConfig = businessConfigurationService.update(id, businessConfigurationDto);

        return new ResponseEntity<>(updatedConfig, HttpStatus.OK);
    }


}
