package com.appointment.management.domain.service;

import com.appointment.management.domain.dto.callaborator.RoleDTO;
import com.appointment.management.persistance.entity.RoleEntity;
import com.appointment.management.persistance.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private RoleEntity roleEntity;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        roleEntity = new RoleEntity("ADMIN", "Administrator Role");
        roleEntity.setId(1L);

        roleDTO = new RoleDTO(1L, "ADMIN", "Administrator Role");
    }

    @Test
    void givenExistingRoleId_whenFindRoleById_thenReturnRoleEntity() {
        // Given
        Long id = 1L;
        when(roleRepository.findById(id)).thenReturn(Optional.of(roleEntity));

        // When
        Optional<RoleEntity> result = roleService.findRoleById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(roleEntity, result.get());
        verify(roleRepository, times(1)).findById(id);
    }

    @Test
    void givenNonExistingRoleId_whenFindRoleById_thenReturnEmptyOptional() {
        // Given
        Long id = 99L;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<RoleEntity> result = roleService.findRoleById(id);

        // Then
        assertTrue(result.isEmpty());
        verify(roleRepository, times(1)).findById(id);
    }

    @Test
    void givenRolesInRepository_whenFindAllRoles_thenReturnRoleDTOList() {
        // Given
        List<RoleEntity> entities = List.of(roleEntity);
        when(roleRepository.findAll()).thenReturn(entities);

        // When
        List<RoleDTO> result = roleService.findAllRoles();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roleDTO, result.get(0));
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void givenNoRolesInRepository_whenFindAllRoles_thenReturnEmptyList() {
        // Given
        when(roleRepository.findAll()).thenReturn(List.of());

        // When
        List<RoleDTO> result = roleService.findAllRoles();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roleRepository, times(1)).findAll();
    }
}