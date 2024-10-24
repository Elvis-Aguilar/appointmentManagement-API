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

import static java.util.function.Predicate.not;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

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

    public Optional<UserWithGoogleSecretDto> findUserWithGoogleKeyByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toUserForGoogleAuth);
    }

    @Transactional
    public UserWithGoogleSecretDto changeUserPassword(Long userId, String password, String repeatedPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        String encryptedPassword = encoder.encode(password);
        if (encoder.matches(repeatedPassword, encryptedPassword)) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }
        user.setPassword(encryptedPassword);
        return toUserForGoogleAuth(userRepository.save(user));
    }

    @Transactional
    public UserDto addGoogleAuthentication(Long userId, String authKey) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.setGoogleAuthKey(authKey);

        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto registerUser(SignUpDto user) {
        if (userRepository.existsByEmail(user.email())) {
            throw new BadRequestException("El email que se intenta registrar ya esta en uso");
        }
        String encryptedPassword = encoder.encode(user.password());

        RoleEntity role = roleRepository.findByName("CLIENTE").or(() -> roleRepository.findById(1L)).orElseThrow();

        UserEntity newUser = new UserEntity(user.name(),user.cui(), encryptedPassword,
                user.nit(), user.email(), user.phone(), role);

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
}
