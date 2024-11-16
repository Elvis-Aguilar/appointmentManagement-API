package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.callaborator.CreateRoleDto;
import com.appointment.management.domain.dto.callaborator.PermissionDTO;
import com.appointment.management.domain.dto.callaborator.UserUpdateDTO;
import com.appointment.management.domain.service.CallaboratorService;
import com.appointment.management.domain.service.CartResponseService;
import com.appointment.management.domain.service.RoleService;
import com.appointment.management.persistance.entity.RoleEntity;
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

    @PostMapping
    public ResponseEntity<CreateRoleDto> createRole(@RequestBody CreateRoleDto role){
        RoleEntity roleEntity = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.collaboratorService.createRolePermissions(role, roleEntity));
    }

    @PutMapping
    public ResponseEntity<Object>updateUserPermissionRole(@RequestBody UserUpdateDTO updateDTO) {
        this.collaboratorService.updateUserPermissionRole(updateDTO);
        return this.cartResponseService.responseSuccess(updateDTO,"all permission", HttpStatus.OK);
    }

    @PutMapping("/update-role/{roleId}")
    public ResponseEntity<Object>updateRolePermission(@RequestBody CreateRoleDto role, @PathVariable Long roleId) {
        this.collaboratorService.updateRolePermission(role, roleId);
        return this.cartResponseService.responseSuccess(role,"all permission", HttpStatus.OK);
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

    @GetMapping("/role-permissions/{roleId}")
    public ResponseEntity<Object> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<PermissionDTO> permissions = collaboratorService.getPermissionsRoleId(roleId);
        return cartResponseService.responseSuccess(permissions, "User permissions", HttpStatus.OK);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deletedRole(@PathVariable Long roleId) {
        this.collaboratorService.deletedRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
