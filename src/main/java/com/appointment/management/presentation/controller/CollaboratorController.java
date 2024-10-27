package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.callaborator.PermissionDTO;
import com.appointment.management.domain.dto.callaborator.UserUpdateDTO;
import com.appointment.management.domain.service.CallaboratorService;
import com.appointment.management.domain.service.CartResponseService;
import com.appointment.management.domain.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/callaborator")
public class CollaboratorController {

    @Autowired
    private CallaboratorService collaboratorService;
    @Autowired
    private CartResponseService cartResponseService;
    @Autowired
    private RoleService roleService;

    @PutMapping
    public ResponseEntity<Object>updateUserPermissionRole(@RequestBody UserUpdateDTO updateDTO) {
        this.collaboratorService.updateUserPermissionRole(updateDTO);
        return this.cartResponseService.responseSuccess(updateDTO,"all permission", HttpStatus.OK);
    }

     @GetMapping("/permissions")
    public ResponseEntity<Object> getAllPermissions() {
        List<PermissionDTO> permissions = this.collaboratorService.getAllPermision();
        return this.cartResponseService.responseSuccess(permissions,"all permission", HttpStatus.OK);
    }

    @GetMapping("/users/{roleId}")
    public ResponseEntity<Object>  getUsersByRole(@PathVariable Long roleId) {
        return this.cartResponseService.responseSuccess(this.collaboratorService.getUsersByRoleId(roleId),"all permission", HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Object>  getAllRoles() {
        return this.cartResponseService.responseSuccess(this.roleService.findAllRoles(),"all permission", HttpStatus.OK);
    }

    @GetMapping("/permissions/{userId}")
    public ResponseEntity<Object> getPermissionsByUserId(@PathVariable Long userId) {
        List<PermissionDTO> permissions = collaboratorService.getPermissionsByUserId(userId);
        return cartResponseService.responseSuccess(permissions, "User permissions", HttpStatus.OK);
    }
}
