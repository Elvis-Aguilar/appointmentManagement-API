package com.appointment.management.domain.service;

import com.appointment.management.domain.dto.callaborator.PermissionDTO;
import com.appointment.management.domain.dto.callaborator.UserUpdateDTO;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.persistance.entity.PermissionEntity;
import com.appointment.management.persistance.entity.RoleEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.entity.UserPermissionEntity;
import com.appointment.management.persistance.repository.PermissionRepository;
import com.appointment.management.persistance.repository.RoleRepository;
import com.appointment.management.persistance.repository.UserPermissionRepository;
import com.appointment.management.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
}
