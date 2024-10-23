package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.business.BusinessHoursDto;
import com.appointment.management.domain.service.business.BusinessHoursService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
    @RequestMapping("/business/hours")
public class BusinessHoursController {

    private final BusinessHoursService businessHoursService;

    @Autowired
    public BusinessHoursController(BusinessHoursService businessHoursService) {
        this.businessHoursService = businessHoursService;
    }

    @PostMapping
    public ResponseEntity<BusinessHoursDto> createBusinessHours(@Valid @RequestBody BusinessHoursDto dto) {
        BusinessHoursDto created = businessHoursService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/weeks-day")
    public ResponseEntity<List<BusinessHoursDto>> createBusinessHoursGeneral(@Valid @RequestBody List<BusinessHoursDto> dtos) {
        List<BusinessHoursDto> created = businessHoursService.createAllList(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessHoursDto> getBusinessHours(@Valid @PathVariable @Positive Long id) {
        BusinessHoursDto businessHours = businessHoursService.getById(id);
        return ResponseEntity.ok(businessHours);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessHoursDto> updateBusinessHours(@Valid @PathVariable @Positive  Long id, @Valid @RequestBody BusinessHoursDto dto) {
        BusinessHoursDto updated = businessHoursService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusinessHours(@Valid @PathVariable @Positive Long id) {
        businessHoursService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BusinessHoursDto>> getAllBusinessHours() {
        List<BusinessHoursDto> businessHoursList = businessHoursService.getAll();
        return ResponseEntity.ok(businessHoursList);
    }

    //funcion para obtener los horarios generales de la semana
    @GetMapping("/all-general")
    public ResponseEntity<List<BusinessHoursDto>> getAllBusinessHoursWithNullSpecificDateIs() {
        List<BusinessHoursDto> businessHoursList = businessHoursService.getAllWithNullSpecificDateIs();
        return ResponseEntity.ok(businessHoursList);
    }

    @GetMapping("/all-specific")
    public ResponseEntity<List<BusinessHoursDto>> getAllBusinessHoursWithNotNullSpecificDateIs() {
        List<BusinessHoursDto> businessHoursList = businessHoursService.getAllWithNotNullSpecificDate();
        return ResponseEntity.ok(businessHoursList);
    }

    //funcion para ir a traer horarios en un rango segun la variable **LocalDate specificDate**
    @GetMapping("/range")
    public ResponseEntity<List<BusinessHoursDto>> getAllBusinessHoursSpecificDateIsRange(
            @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDateString,
            @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDateString
    ) {
        LocalDate startDate = LocalDate.parse(startDateString);
        LocalDate endDate = LocalDate.parse(endDateString);

        List<BusinessHoursDto> businessHoursDtos = businessHoursService.getBusinessHoursInDateRange(startDate, endDate);

        return ResponseEntity.ok(businessHoursDtos);
    }

}


