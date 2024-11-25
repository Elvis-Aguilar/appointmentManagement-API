package com.appointment.management.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.auth.SignUpDto;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.dto.user.UserProfileDto;
import com.appointment.management.persistance.entity.RoleEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.repository.RoleRepository;
import com.appointment.management.persistance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    private RoleEntity role;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
        role = new RoleEntity("ROLE_USER", "User role");
        userEntity = new UserEntity("Jane Doe", "1234567890123", "password123", "1234567890", "jane.doe@example.com", "555-5678", role);
        userEntity.setId(2L);
        userEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testFindUserById_UserExists() {
        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Llamando al metodo a testear
        Optional<UserDto> result = userService.findUserById(1L);

        // Then
        assertTrue(result.isPresent());
        UserDto userDto = result.get();
        assertEquals(userEntity.getId(), userDto.id());
        assertEquals(userEntity.getName(), userDto.name());
        assertEquals(userEntity.getEmail(), userDto.email());
        assertEquals(userEntity.getNit(), userDto.nit());
        assertEquals(userEntity.getCui(), userDto.cui());
        assertEquals(userEntity.getPhone(), userDto.phone());
        assertEquals(userEntity.getCreatedAt(), userDto.createdAt());
        assertEquals(userEntity.getRole().getName(), userDto.role());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUserById_UserDoesNotExist() {
        // When
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // LLamando al metodo a testear
        Optional<UserDto> result = userService.findUserById(1L);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUserByEmail_UserExists() {

        // When
        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(userEntity));

        // LLamando al emtodo a testear
        Optional<UserDto> result = userService.findUserByEmail("jane.doe@example.com");

        // Then
        assertTrue(result.isPresent());
        UserDto userDto = result.get();
        assertEquals(userEntity.getId(), userDto.id());


        // Verificar interacciones con el repositorio
        verify(userRepository, times(1)).findByEmail("jane.doe@example.com");
    }

    @Test
    void testFindUserByEmail_UserDoesNotExist() {
        // When
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // LLamando al metodo a testear
        Optional<UserDto> result = userService.findUserByEmail("unknown@example.com");

        // Then
        assertTrue(result.isEmpty());

        // Then
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    void testFindUserByIdEntity_UserExists() {
        // Given
        RoleEntity role = new RoleEntity("ROLE_ADMIN", "Admin role");
        UserEntity userEntity = new UserEntity("John Smith", "9876543210987", "securepassword", "9876543210", "john.smith@example.com", "555-4321", role);
        userEntity.setId(3L);

        // When
        when(userRepository.findById(3L)).thenReturn(Optional.of(userEntity));

        // Ejecutando el metodo a testear
        UserEntity result = userService.findUserByIdEntity(3L);

        // Then
        assertEquals(userEntity, result);
        verify(userRepository, times(1)).findById(3L);
    }

    @Test
    void testFindUserByIdEntity_UserDoesNotExist() {
        // When
        when(userRepository.findById(4L)).thenReturn(Optional.empty());

        // Ejecutando el metodo a testear
        UserEntity result = userService.findUserByIdEntity(4L);

        // Then
        assertEquals(new UserEntity(), result);
        verify(userRepository, times(1)).findById(4L);
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");
        RoleEntity role = new RoleEntity("CLIENTE", "Client role");
        UserEntity savedUser = new UserEntity("John Doe", "1234567890123", "encryptedPassword", "1234567890", "john.doe@example.com", "555-1234", role);

        // When
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(false);
        when(userRepository.existsByNit(userDto.nit())).thenReturn(false);
        when(userRepository.existsByPhone(userDto.phone())).thenReturn(false);
        when(encoder.encode(userDto.password())).thenReturn("encryptedPassword");
        when(roleRepository.findByName("CLIENTE")).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Ejecutando el metodo a tesetear
        UserDto result = userService.registerUser(userDto);

        // Then
        assertEquals(savedUser.getName(), result.name());
        assertEquals(savedUser.getEmail(), result.email());
        assertEquals(savedUser.getPhone(), result.phone());
        assertEquals(savedUser.getRole().getName(), result.role());

        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, times(1)).existsByCui(userDto.cui());
        verify(userRepository, times(1)).existsByNit(userDto.nit());
        verify(userRepository, times(1)).existsByPhone(userDto.phone());
        verify(encoder, times(1)).encode(userDto.password());
        verify(roleRepository, times(1)).findByName("CLIENTE");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Given
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // When
        when(userRepository.existsByEmail(userDto.email())).thenReturn(true);

        // Ejecutando el metodo a tesetear
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));
        assertEquals("El email que se intenta registrar ya está en uso", exception.getMessage());

        //  Then
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_CuiAlreadyExists() {
        // Given
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // When
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(true);

        // Ejecutando el metodo a tesetear
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));
        assertEquals("El CUI que se intenta registrar ya está en uso", exception.getMessage());

        // Then
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, times(1)).existsByCui(userDto.cui());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_NitAlreadyExists() {
        // Given
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // When
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(false);
        when(userRepository.existsByNit(userDto.nit())).thenReturn(true);

        // Ejecutando el metodo a tesetear
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));

        //Then
        assertEquals("El NIT que se intenta registrar ya está en uso", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, times(1)).existsByCui(userDto.cui());
        verify(userRepository, times(1)).existsByNit(userDto.nit());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_PhoneAlreadyExists() {
        // Given
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // When
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(false);
        when(userRepository.existsByNit(userDto.nit())).thenReturn(false);
        when(userRepository.existsByPhone(userDto.phone())).thenReturn(true);

        // Ejecutando el metodo a tesetear
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));

        //Then
        assertEquals("El número de teléfono que se intenta registrar ya está en uso", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, times(1)).existsByCui(userDto.cui());
        verify(userRepository, times(1)).existsByNit(userDto.nit());
        verify(userRepository, times(1)).existsByPhone(userDto.phone());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        Long userId = 1L;
        UserProfileDto userProfileDto = new UserProfileDto(1L,"Updated Name", "1234567890123", "updated.email@example.com", "555-4321", "1234567890", "newImageUrl");

        UserEntity existingEntity = new UserEntity();
        existingEntity.setId(userId);
        existingEntity.setName("Old Name");
        existingEntity.setCui("0987654321098");
        existingEntity.setEmail("old.email@example.com");
        existingEntity.setPhone("555-1234");
        existingEntity.setNit("0987654321");
        existingEntity.setImageUrl("oldImageUrl");

        // Asignar un rol al usuario existente
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("CLIENTE");
        existingEntity.setRole(role);

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutando el metodo a tesetear
        UserDto result = userService.updateUser(userId, userProfileDto);

        // Then
        assertEquals(userProfileDto.name(), result.name());
        assertEquals(userProfileDto.cui(), result.cui());
        assertEquals(userProfileDto.email(), result.email());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingEntity);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        Long userId = 1L;
        UserProfileDto userProfileDto = new UserProfileDto(1L,"Updated Name", "1234567890123", "updated.email@example.com", "555-4321", "1234567890", "newImageUrl");

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Ejecutando el metodo a tesetear
        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> userService.updateUser(userId, userProfileDto));

        //Then
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
