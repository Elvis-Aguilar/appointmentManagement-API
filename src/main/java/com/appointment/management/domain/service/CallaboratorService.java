package com.appointment.management.domain.service;

import com.appointment.management.application.exception.RequestConflictException;
import com.appointment.management.domain.dto.callaborator.CreateRoleDto;
import com.appointment.management.domain.dto.callaborator.PermissionDTO;
import com.appointment.management.domain.dto.callaborator.UserUpdateDTO;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.persistance.entity.*;
import com.appointment.management.persistance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallaboratorService {

    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserPermissionRepository userPermissionRepository;

    private final RolePermissionRepository rolePermissionRepository;

    @Transactional
    public UserUpdateDTO updateUserPermissionRole(UserUpdateDTO updateDTO) {
        UserEntity user = userRepository.findById(updateDTO.idUser())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Actualizar el rol del usuario
        this.updateUserRole(user, updateDTO.role().id());

        // Si el rol es "ayudante", asignar permisos
        if (updateDTO.role().name().equalsIgnoreCase("EMPLEADO")) {
            userPermissionRepository.deleteAllByUserId(updateDTO.idUser());

            // Asignar nuevos permisos
            for (Long permissionId : updateDTO.permissions()) {
                PermissionEntity permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RuntimeException("Permission not found"));

                UserPermissionEntity userPermission = new UserPermissionEntity();
                userPermission.setUser(user);
                userPermission.setPermission(permission);

                userPermissionRepository.save(userPermission);
            }
        }
        return updateDTO;
    }

    @Transactional
    public void updateRolePermission(CreateRoleDto role, Long roleId) {
        RoleEntity roleEntity = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("role not found"));

        roleEntity.setName(role.name());
        roleEntity.setDescription(role.description());
        this.roleRepository.save(roleEntity);

        //eliminar los permisos del rol
        rolePermissionRepository.deleteAllByRoleId(roleId);

        // Asignar nuevos permisos
        for (Long permissionId : role.permissions()) {
            PermissionEntity permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found"));

            RolePermissionEntity rolePermission = new RolePermissionEntity(roleEntity, permission);

            rolePermissionRepository.save(rolePermission);
        }
    }

    @Transactional
    public CreateRoleDto createRolePermissions(CreateRoleDto createRoleDto, RoleEntity role) {
        // Asignar nuevos permisos
        for (Long permissionId : createRoleDto.permissions()) {
            PermissionEntity permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found"));

            RolePermissionEntity rolePermission = new RolePermissionEntity(role, permission);

            rolePermissionRepository.save(rolePermission);
        }
        return createRoleDto;
    }

    public void updateUserRole(UserEntity user, Long newRoleId) {

        // Encontrar el nuevo rol por su ID
        RoleEntity newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Actualizar el rol del usuario
        user.setRole(newRole);

        // Guardar los cambios
        userRepository.save(user);
    }

    public List<UserDto> getUsersByRoleId(Long roleId) {
        return userRepository.findAllByRoleId(roleId).stream().map(this::toUserDto).toList();
    }

    public List<PermissionDTO> getAllPermision(){
        return this.permissionRepository.findAll().stream().map(this::convertToPermissionDTO).toList();
    }

    private PermissionDTO convertToPermissionDTO(PermissionEntity permissionEntity) {
       return new PermissionDTO(permissionEntity.getId(),permissionEntity.getName(),permissionEntity.getDescription());
    }

    private UserDto toUserDto(UserEntity user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getNit(),
                user.getCui(), // Asegúrate de incluir esto
                user.getPhone(), // Asegúrate de incluir esto
                user.getCreatedAt(),
                user.getImageUrl(), // Asegúrate de incluir esto
                user.getGoogleAuthKey() != null, // Cambiado a booleano
                user.getRole().getName(),
                user.getUserPermissions() == null
                        ? List.of() // Devuelve una lista vacía si es null
                        : user.getUserPermissions()
                        .stream()
                        .map(UserPermissionEntity::getPermission)
                        .map(PermissionEntity::getName)
                        .collect(Collectors.toList()) // Devuelve una lista de nombres
        );
    }

    public List<PermissionDTO> getPermissionsByUserId(Long userId) {
        List<UserPermissionEntity> userPermissions = userPermissionRepository.findAllByUserId(userId);

        // Convertir UserPermissionEntity a PermissionDTO
        return userPermissions.stream()
                .map(userPermission -> convertToPermissionDTO(userPermission.getPermission()))
                .collect(Collectors.toList());
    }

    public List<PermissionDTO> getPermissionsRoleId(Long roleId) {
        List<RolePermissionEntity> userPermissions = this.rolePermissionRepository.findAllByRoleId(roleId);

        // Convertir UserPermissionEntity a PermissionDTO
        return userPermissions.stream()
                .map(userPermission -> convertToPermissionDTO(userPermission.getPermission()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletedRole(Long roleId) {
        // Eliminar todas las relaciones del rol con los permisos
        rolePermissionRepository.deleteAllByRoleId(roleId);

        // Eliminar el rol
        try {
            roleRepository.deleteById(roleId);
        } catch (DataIntegrityViolationException e) {
            throw new RequestConflictException("Failed to delete the role due to data integrity constraints.");
        }
    }
}
