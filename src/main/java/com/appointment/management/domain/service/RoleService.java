package com.appointment.management.domain.service;

import java.util.List;
import java.util.Optional;


import com.appointment.management.application.exception.RequestConflictException;
import com.appointment.management.domain.dto.callaborator.CreateRoleDto;
import com.appointment.management.domain.dto.callaborator.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.management.persistance.entity.RoleEntity;
import com.appointment.management.persistance.repository.RoleRepository;

@Service
public class RoleService {


    @Autowired
    private RoleRepository roleRepository;

    public RoleEntity createRole(CreateRoleDto roleDto) {
        String roleName = roleDto.name().trim().toLowerCase();

        if (roleRepository.findByName(roleName).isPresent()) {
            throw new RequestConflictException("El rol con nombre '" + roleDto.name() + "' ya existe.");
        }

        RoleEntity newRole = new RoleEntity(roleDto.name(), roleDto.description());

        return roleRepository.save(newRole);
    }

    public Optional<RoleEntity> findRoleById(long id) {
        return roleRepository.findById(id);
    }

    public List<RoleDTO> findAllRoles() {
        return roleRepository.findAll().stream().map(this::convertToRoleDTO).toList();
    }

    private RoleDTO convertToRoleDTO(RoleEntity roleEntity) {
        return new RoleDTO(roleEntity.getId(), roleEntity.getName(), roleEntity.getDescription());
    }
}
