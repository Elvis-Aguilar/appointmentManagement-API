package com.appointment.management.domain.service;

import com.appointment.management.domain.dto.callaborator.PermissionDTO;
import com.appointment.management.domain.dto.callaborator.RoleDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CallaboratorServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @InjectMocks
    private CallaboratorService callaboratorService;

    private UserEntity user;
    private RoleEntity role;
    private PermissionEntity permission;
    private UserUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role = new RoleEntity("EMPLEADO", "Employee Role");
        role.setId(1L);

        user = new UserEntity();
        user.setId(1L);
        user.setRole(role);

        permission = new PermissionEntity("VIEW", "View Permission");

        updateDTO = new UserUpdateDTO(1L, new RoleDTO(1L, "EMPLEADO", "Employee Role"), List.of(1L));
    }

    @Test
    void givenValidUserAndRole_whenUpdateUserPermissionRole_thenRoleAndPermissionsAreUpdated() {
        // Given
        when(userRepository.findById(updateDTO.idUser())).thenReturn(Optional.of(user));
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(permission));

        // When
        UserUpdateDTO result = callaboratorService.updateUserPermissionRole(updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(updateDTO, result);
        verify(userRepository, times(1)).findById(updateDTO.idUser());
        verify(roleRepository, times(1)).findById(updateDTO.role().id());
        verify(userRepository, times(1)).save(user);
        verify(userPermissionRepository, times(1)).deleteAllByUserId(updateDTO.idUser());
        verify(userPermissionRepository, times(1)).save(any(UserPermissionEntity.class));
    }

    @Test
    void givenNonExistentUser_whenUpdateUserPermissionRole_thenThrowException() {
        // Given
        when(userRepository.findById(updateDTO.idUser())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> callaboratorService.updateUserPermissionRole(updateDTO));
        verify(userRepository, times(1)).findById(updateDTO.idUser());
        verify(roleRepository, never()).findById(anyLong());
        verify(permissionRepository, never()).findById(anyLong());
    }

    @Test
    void givenValidUserAndRoleId_whenUpdateUserRole_thenRoleIsUpdated() {
        // Given
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));

        // When
        callaboratorService.updateUserRole(user, role.getId());

        // Then
        assertEquals(role, user.getRole());
        verify(roleRepository, times(1)).findById(role.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenRoleId_whenGetUsersByRoleId_thenReturnListOfUserDtos() {
        // Given
        when(userRepository.findAllByRoleId(role.getId())).thenReturn(List.of(user));

        // When
        List<UserDto> result = callaboratorService.getUsersByRoleId(role.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).id());
        verify(userRepository, times(1)).findAllByRoleId(role.getId());
    }

    @Test
    void whenGetAllPermissions_thenReturnPermissionDTOList() {
        // Given
        when(permissionRepository.findAll()).thenReturn(List.of(permission));

        // When
        List<PermissionDTO> result = callaboratorService.getAllPermision();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(permission.getId(), result.get(0).id());
        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    void givenUserId_whenGetPermissionsByUserId_thenReturnListOfPermissionDtos() {
        // Given
        UserPermissionEntity userPermission = new UserPermissionEntity(user, permission);
        when(userPermissionRepository.findAllByUserId(user.getId())).thenReturn(List.of(userPermission));

        // When
        List<PermissionDTO> result = callaboratorService.getPermissionsByUserId(user.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(permission.getId(), result.get(0).id());
        verify(userPermissionRepository, times(1)).findAllByUserId(user.getId());
    }
}