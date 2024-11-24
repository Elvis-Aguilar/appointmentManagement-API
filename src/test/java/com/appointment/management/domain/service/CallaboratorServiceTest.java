package com.appointment.management.domain.service;

import com.appointment.management.application.exception.RequestConflictException;
import com.appointment.management.domain.dto.callaborator.CreateRoleDto;
import com.appointment.management.domain.dto.callaborator.PermissionDTO;
import com.appointment.management.domain.dto.callaborator.RoleDTO;
import com.appointment.management.domain.dto.callaborator.UserUpdateDTO;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.persistance.entity.*;
import com.appointment.management.persistance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
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

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    private UserEntity user;
    private RoleEntity role;
    private PermissionEntity permission;
    private UserUpdateDTO updateDTO;
    private CreateRoleDto createRoleDto;

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

        createRoleDto = new CreateRoleDto("EMPLEADO", "Employee Role", List.of(100L, 101L));

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

    @Test
    void givenValidRoleAndPermissions_whenUpdateRolePermission_thenRoleAndPermissionsAreUpdated() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(100L)).thenReturn(Optional.of(permission));
        when(permissionRepository.findById(101L)).thenReturn(Optional.of(new PermissionEntity("EDIT", "Edit Permission")));

        // When
        callaboratorService.updateRolePermission(createRoleDto, 1L);

        // Then
        assertEquals("EMPLEADO", role.getName());
        assertEquals("Employee Role", role.getDescription());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(role);
        verify(rolePermissionRepository, times(1)).deleteAllByRoleId(1L);
        verify(permissionRepository, times(2)).findById(anyLong());
        verify(rolePermissionRepository, times(2)).save(any(RolePermissionEntity.class));
    }

    @Test
    void givenNonExistentRole_whenUpdateRolePermission_thenThrowRuntimeException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> callaboratorService.updateRolePermission(createRoleDto, 1L));

        assertEquals("role not found", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(rolePermissionRepository, never()).deleteAllByRoleId(anyLong());
        verify(permissionRepository, never()).findById(anyLong());
        verify(rolePermissionRepository, never()).save(any(RolePermissionEntity.class));
    }

    @Test
    void givenNonExistentPermission_whenUpdateRolePermission_thenThrowRuntimeException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(100L)).thenReturn(Optional.of(permission));
        when(permissionRepository.findById(101L)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> callaboratorService.updateRolePermission(createRoleDto, 1L));

        // Verificar el mensaje de la excepción
        assertEquals("Permission not found", exception.getMessage());

        // Verificar interacciones con los mocks
        verify(roleRepository, times(1)).findById(1L);
        verify(permissionRepository, times(2)).findById(anyLong());
        verify(rolePermissionRepository, times(1)).deleteAllByRoleId(1L);
    }

    @Test
    void givenValidPermissions_whenCreateRolePermissions_thenPermissionsAreAssigned() {
        // Given
        RoleEntity role = new RoleEntity("ADMIN", "Administrator Role");
        role.setId(1L);

        PermissionEntity permission1 = new PermissionEntity("VIEW", "View Permission");
        permission1.setId(100L);

        PermissionEntity permission2 = new PermissionEntity("EDIT", "Edit Permission");
        permission2.setId(101L);

        CreateRoleDto createRoleDto = new CreateRoleDto("ADMIN", "Administrator Role", List.of(100L, 101L));

        when(permissionRepository.findById(100L)).thenReturn(Optional.of(permission1));
        when(permissionRepository.findById(101L)).thenReturn(Optional.of(permission2));

        // When
        CreateRoleDto result = callaboratorService.createRolePermissions(createRoleDto, role);

        // Then
        assertNotNull(result);
        assertEquals(createRoleDto, result);

        verify(permissionRepository, times(1)).findById(100L);
        verify(permissionRepository, times(1)).findById(101L);
        verify(rolePermissionRepository, times(2)).save(any(RolePermissionEntity.class));
    }

    @Test
    void givenNonExistentPermission_whenCreateRolePermissions_thenThrowRuntimeException() {
        // Given
        RoleEntity role = new RoleEntity("ADMIN", "Administrator Role");
        role.setId(1L);

        PermissionEntity permission1 = new PermissionEntity("VIEW", "View Permission");
        permission1.setId(100L);

        CreateRoleDto createRoleDto = new CreateRoleDto("ADMIN", "Administrator Role", List.of(100L, 101L));

        when(permissionRepository.findById(100L)).thenReturn(Optional.of(permission1));
        when(permissionRepository.findById(101L)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> callaboratorService.createRolePermissions(createRoleDto, role));

        assertEquals("Permission not found", exception.getMessage());

        verify(permissionRepository, times(1)).findById(100L);
        verify(permissionRepository, times(1)).findById(101L);
        verify(rolePermissionRepository, times(1)).save(any(RolePermissionEntity.class)); // Solo se guarda el primer permiso
        verify(rolePermissionRepository, never()).save(argThat(rolePermission -> rolePermission.getPermission().getId() == 101L));
    }

    @Test
    void givenEmptyPermissionsList_whenCreateRolePermissions_thenNoPermissionsAreAssigned() {
        // Given
        RoleEntity role = new RoleEntity("ADMIN", "Administrator Role");
        role.setId(1L);

        CreateRoleDto createRoleDto = new CreateRoleDto("ADMIN", "Administrator Role", List.of());

        // When
        CreateRoleDto result = callaboratorService.createRolePermissions(createRoleDto, role);

        // Then
        assertNotNull(result);
        assertEquals(createRoleDto, result);

        verify(permissionRepository, never()).findById(anyLong());
        verify(rolePermissionRepository, never()).save(any(RolePermissionEntity.class));
    }

    @Test
    void givenValidRoleId_whenDeletedRole_thenRoleAndPermissionsAreDeleted() {
        // Given
        Long roleId = 1L;
        doNothing().when(rolePermissionRepository).deleteAllByRoleId(roleId);
        doNothing().when(roleRepository).deleteById(roleId);

        // When
        callaboratorService.deletedRole(roleId);

        // Then
        verify(rolePermissionRepository, times(1)).deleteAllByRoleId(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void givenRoleId_whenDeletedRoleThrowsDataIntegrityViolationException_thenThrowRequestConflictException() {
        // Given
        Long roleId = 1L;
        doNothing().when(rolePermissionRepository).deleteAllByRoleId(roleId);
        doThrow(DataIntegrityViolationException.class).when(roleRepository).deleteById(roleId);

        // When / Then
        RequestConflictException exception = assertThrows(RequestConflictException.class,
                () -> callaboratorService.deletedRole(roleId));

        assertEquals("Failed to delete the role due to data integrity constraints.", exception.getMessage());

        verify(rolePermissionRepository, times(1)).deleteAllByRoleId(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void givenNonExistentRoleId_whenDeletedRole_thenNoExceptionIsThrown() {
        // Given
        Long roleId = 999L;
        doNothing().when(rolePermissionRepository).deleteAllByRoleId(roleId);
        doNothing().when(roleRepository).deleteById(roleId);

        // When
        callaboratorService.deletedRole(roleId);

        // Then
        verify(rolePermissionRepository, times(1)).deleteAllByRoleId(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void givenValidUserId_whenGetPermissionsByUserId_thenReturnPermissionDTOList() {
        // Given
        PermissionEntity permission1 = new PermissionEntity("READ", "Permission to read data");
        permission1.setId(1L);
        PermissionEntity permission2 = new PermissionEntity("WRITE", "Permission to write data");
        permission1.setId(2L);
        UserPermissionEntity userPermission1 = new UserPermissionEntity(user, permission1);
        UserPermissionEntity userPermission2 = new UserPermissionEntity(user, permission2);

        List<UserPermissionEntity> userPermissions = List.of(userPermission1, userPermission2);

        when(userPermissionRepository.findAllByUserId(user.getId())).thenReturn(userPermissions);

        // When
        List<PermissionDTO> result = callaboratorService.getPermissionsByUserId(user.getId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("READ", result.get(0).name());
        assertEquals("WRITE", result.get(1).name());

        verify(userPermissionRepository, times(1)).findAllByUserId(user.getId());
    }

    @Test
    void givenUserIdWithoutPermissions_whenGetPermissionsByUserId_thenReturnEmptyList() {
        // Given
        Long userId = 1L;
        when(userPermissionRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        List<PermissionDTO> result = callaboratorService.getPermissionsByUserId(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userPermissionRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void givenInvalidUserId_whenGetPermissionsByUserId_thenReturnEmptyList() {
        // Given
        Long invalidUserId = 999L;
        when(userPermissionRepository.findAllByUserId(invalidUserId)).thenReturn(Collections.emptyList());

        // When
        List<PermissionDTO> result = callaboratorService.getPermissionsByUserId(invalidUserId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userPermissionRepository, times(1)).findAllByUserId(invalidUserId);
    }

    @Test
    void givenValidRoleId_whenGetPermissionsRoleId_thenReturnPermissionDTOList() {
        // Given
        PermissionEntity permission1 = new PermissionEntity("READ", "Permission to read data");
        PermissionEntity permission2 = new PermissionEntity("WRITE", "Permission to write data");
        permission1.setId(1L);
        permission2.setId(2L);
        RolePermissionEntity rolePermission1 = new RolePermissionEntity(role, permission1);
        RolePermissionEntity rolePermission2 = new RolePermissionEntity(role, permission2);

        List<RolePermissionEntity> rolePermissions = List.of(rolePermission1, rolePermission2);

        when(rolePermissionRepository.findAllByRoleId(role.getId())).thenReturn(rolePermissions);

        // When
        List<PermissionDTO> result = callaboratorService.getPermissionsRoleId(role.getId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("READ", result.get(0).name());
        assertEquals("WRITE", result.get(1).name());

        verify(rolePermissionRepository, times(1)).findAllByRoleId(role.getId());
    }

    @Test
    void givenRoleIdWithoutPermissions_whenGetPermissionsRoleId_thenReturnEmptyList() {
        // Given
        Long roleId = 1L;
        when(rolePermissionRepository.findAllByRoleId(roleId)).thenReturn(Collections.emptyList());

        // When
        List<PermissionDTO> result = callaboratorService.getPermissionsRoleId(roleId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(rolePermissionRepository, times(1)).findAllByRoleId(roleId);
    }

    @Test
    void givenInvalidRoleId_whenGetPermissionsRoleId_thenReturnEmptyList() {
        // Given
        Long invalidRoleId = 999L;
        when(rolePermissionRepository.findAllByRoleId(invalidRoleId)).thenReturn(Collections.emptyList());

        // When
        List<PermissionDTO> result = callaboratorService.getPermissionsRoleId(invalidRoleId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(rolePermissionRepository, times(1)).findAllByRoleId(invalidRoleId);
    }

}