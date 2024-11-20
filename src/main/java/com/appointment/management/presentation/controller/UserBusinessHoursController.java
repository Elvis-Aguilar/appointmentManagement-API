package com.appointment.management.presentation.controller;


import com.appointment.management.domain.dto.business.UpdateUserBusinessHours;
import com.appointment.management.domain.service.business.UserBusinessHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business/hours/users")
public class UserBusinessHoursController {

    @Autowired
    private UserBusinessHoursService userBusinessHoursService;

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUserBusinessHours(@RequestBody UpdateUserBusinessHours updateUserBusinessHours, @PathVariable Long id) {
        this.userBusinessHoursService.updateUserBusinessHours(updateUserBusinessHours, id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserBusinessHours(@PathVariable Long id) {
        return ResponseEntity.ok(this.userBusinessHoursService.getUserBusinessHours(id));
    }

}
