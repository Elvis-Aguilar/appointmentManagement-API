package com.appointment.management.domain.service;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.auth.SignUpDto;
import com.appointment.management.domain.dto.auth.UserWithGoogleSecretDto;
import com.appointment.management.domain.dto.user.UserChangeDto;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.dto.user.UserProfileDto;
import com.appointment.management.persistance.entity.*;
import com.appointment.management.persistance.repository.PermissionRepository;
import com.appointment.management.persistance.repository.RoleRepository;
import com.appointment.management.persistance.repository.UserPermissionRepository;
import com.appointment.management.persistance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    private UserWithGoogleSecretDto toUserForGoogleAuth(UserEntity user) {
        return new UserWithGoogleSecretDto(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(),user.getGoogleAuthKey());
    }

    private UserDto toUserDto(UserEntity user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getNit(), user.getCui(), user.getPhone(),
                user.getCreatedAt(), user.getImageUrl(), user.getGoogleAuthKey() != null, user.getRole().getName(),
                user.getUserPermissions() == null
                        ? List.of()
                        : user.getUserPermissions()
                        .stream()
                        .map(UserPermissionEntity::getPermission)
                        .map(PermissionEntity::getName)
                        .toList());
    }

    public Optional<UserDto> findUserById(Long userId) {
        return userRepository.findById(userId).map(this::toUserDto);
    }

    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toUserDto);
    }

    public UserEntity findUserByIdEntity(Long userId) {
        return userRepository.findById(userId).orElse(new UserEntity());
    }

    public Optional<UserWithGoogleSecretDto> findUserWithGoogleKeyByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toUserForGoogleAuth);
    }

    @Transactional
    public UserWithGoogleSecretDto changeUserPassword(Long userId, String currentPassword, String newPassword) {
        // Busca al usuario por su ID
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        // Verifica que la contraseña actual ingresada coincida con la almacenada
        if (!encoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("La contraseña actual no es correcta");
        }

        // Encripta la nueva contraseña y actualiza al usuario
        user.setPassword(encoder.encode(newPassword));
        UserEntity updatedUser = userRepository.save(user);

        // Retorna el usuario actualizado en el formato deseado
        return toUserForGoogleAuth(updatedUser);
    }


    public UserWithGoogleSecretDto changeUserPasswordRecover(Long userId, String password) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.setPassword(encoder.encode(password));

        return toUserForGoogleAuth(userRepository.save(user));
    }

    public List<UserDto> getAllUsersWithRole(Long roleId) {
        List<UserEntity> usersWithRole = userRepository.findAllByRoleId(roleId);
        return usersWithRole.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersExcludingRoles(List<Long> excludedRoleIds) {
        List<UserEntity> users = userRepository.findAllByRoleIdNotIn(excludedRoleIds);
        return users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public UserDto addGoogleAuthentication(Long userId, String authKey) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.setGoogleAuthKey(authKey);

        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto deletedGoogleAuthentication(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.setGoogleAuthKey(null);

        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto registerUser(SignUpDto user) {
        // Validar si el email ya está en uso
        if (userRepository.existsByEmail(user.email())) {
            throw new BadRequestException("El email que se intenta registrar ya está en uso");
        }

        // Validar si el CUI ya está en uso
        if (userRepository.existsByCui(user.cui())) {
            throw new BadRequestException("El CUI que se intenta registrar ya está en uso");
        }

        // Validar si el NIT ya está en uso
        if (userRepository.existsByNit(user.nit())) {
            throw new BadRequestException("El NIT que se intenta registrar ya está en uso");
        }

        // Validar si el teléfono ya está en uso
        if (userRepository.existsByPhone(user.phone())) {
            throw new BadRequestException("El número de teléfono que se intenta registrar ya está en uso");
        }

        // Encriptar la contraseña
        String encryptedPassword = encoder.encode(user.password());

        // Buscar el rol "CLIENTE" o usar el ID 1 por defecto
        RoleEntity role = roleRepository.findByName("CLIENTE")
                .or(() -> roleRepository.findById(1L))
                .orElseThrow();

        // Crear el nuevo usuario
        UserEntity newUser = new UserEntity(
                user.name(),
                user.cui(),
                encryptedPassword,
                user.nit(),
                user.email(),
                user.phone(),
                role
        );

        // Guardar y devolver el DTO del usuario registrado
        return toUserDto(userRepository.save(newUser));
    }

    @Transactional
    public UserDto updateUser(Long id, UserProfileDto user){
        UserEntity existingEntity = this.userRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("User not found with id: " + id));

        existingEntity.setName(user.name());
        existingEntity.setCui(user.cui());
        existingEntity.setEmail(user.email());
        existingEntity.setPhone(user.phone());
        existingEntity.setNit(user.nit());
        existingEntity.setImageUrl(user.imageUrl());

        return this.toUserDto(this.userRepository.save(existingEntity));
    }

    @Transactional
    public UserWithGoogleSecretDto changeUserInfo(Long userId, UserChangeDto user, boolean matchesInactive) {
        UserEntity dbUser = userRepository.findById(userId).get();

        user.currentPassword()
                .filter(not(ObjectUtils::isEmpty))
                .filter(passwd -> matchesInactive || encoder.matches(passwd, dbUser.getPassword()))
                .flatMap(passwd -> user.newPassword())
                .filter(not(ObjectUtils::isEmpty))
                .map(encoder::encode)
                .ifPresent(dbUser::setPassword);

        return toUserForGoogleAuth(userRepository.save(dbUser));
    }

    @Transactional
    public UserDto changeUserRole(Long userId, Long newRoleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el usuario"));

        RoleEntity newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el rol"));

        user.setRole(newRole);
        return toUserDto(userRepository.save(user));
    }
}
