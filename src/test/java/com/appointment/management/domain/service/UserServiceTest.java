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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserById_UserExists() {
        // Datos de prueba
        RoleEntity role = new RoleEntity("ROLE_USER", "User role");
        UserEntity userEntity = new UserEntity("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234", role);
        userEntity.setId(1L);
        userEntity.setCreatedAt(LocalDateTime.now());

        // Simulaciones
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Ejecutar
        Optional<UserDto> result = userService.findUserById(1L);

        // Verificaciones
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

        // Verificar interacciones con el repositorio
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUserById_UserDoesNotExist() {
        // Simular que el usuario no existe
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Ejecutar
        Optional<UserDto> result = userService.findUserById(1L);

        // Verificar que no se encuentra el usuario
        assertTrue(result.isEmpty());

        // Verificar interacciones con el repositorio
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUserByEmail_UserExists() {
        // Datos de prueba
        RoleEntity role = new RoleEntity("ROLE_USER", "User role");
        UserEntity userEntity = new UserEntity("Jane Doe", "1234567890123", "password123", "1234567890", "jane.doe@example.com", "555-5678", role);
        userEntity.setId(2L);
        userEntity.setCreatedAt(LocalDateTime.now());

        // Simulaciones
        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(userEntity));

        // Ejecutar
        Optional<UserDto> result = userService.findUserByEmail("jane.doe@example.com");

        // Verificaciones
        assertTrue(result.isPresent());
        UserDto userDto = result.get();
        assertEquals(userEntity.getId(), userDto.id());


        // Verificar interacciones con el repositorio
        verify(userRepository, times(1)).findByEmail("jane.doe@example.com");
    }

    @Test
    void testFindUserByEmail_UserDoesNotExist() {
        // Simular que el usuario no existe
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Ejecutar
        Optional<UserDto> result = userService.findUserByEmail("unknown@example.com");

        // Verificar que no se encuentra el usuario
        assertTrue(result.isEmpty());

        // Verificar interacciones con el repositorio
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    void testFindUserByIdEntity_UserExists() {
        // Datos de prueba
        RoleEntity role = new RoleEntity("ROLE_ADMIN", "Admin role");
        UserEntity userEntity = new UserEntity("John Smith", "9876543210987", "securepassword", "9876543210", "john.smith@example.com", "555-4321", role);
        userEntity.setId(3L);

        // Simulaciones
        when(userRepository.findById(3L)).thenReturn(Optional.of(userEntity));

        // Ejecutar
        UserEntity result = userService.findUserByIdEntity(3L);

        // Verificación
        assertEquals(userEntity, result);

        // Verificar interacciones con el repositorio
        verify(userRepository, times(1)).findById(3L);
    }

    @Test
    void testFindUserByIdEntity_UserDoesNotExist() {
        // Simular que el usuario no existe
        when(userRepository.findById(4L)).thenReturn(Optional.empty());

        // Ejecutar
        UserEntity result = userService.findUserByIdEntity(4L);

        // Verificación: debe devolver una nueva instancia de UserEntity
        assertEquals(new UserEntity(), result);

        // Verificar interacciones con el repositorio
        verify(userRepository, times(1)).findById(4L);
    }

    @Test
    void testRegisterUser_Success() {
        // Datos de prueba
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");
        RoleEntity role = new RoleEntity("CLIENTE", "Client role");
        UserEntity savedUser = new UserEntity("John Doe", "1234567890123", "encryptedPassword", "1234567890", "john.doe@example.com", "555-1234", role);

        // Simulaciones
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(false);
        when(userRepository.existsByNit(userDto.nit())).thenReturn(false);
        when(userRepository.existsByPhone(userDto.phone())).thenReturn(false);
        when(encoder.encode(userDto.password())).thenReturn("encryptedPassword");
        when(roleRepository.findByName("CLIENTE")).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Ejecutar
        UserDto result = userService.registerUser(userDto);

        // Verificación
        assertEquals(savedUser.getName(), result.name());
        assertEquals(savedUser.getEmail(), result.email());
        assertEquals(savedUser.getPhone(), result.phone());
        assertEquals(savedUser.getRole().getName(), result.role());

        // Verificar interacciones con los repositorios
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
        // Datos de prueba
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // Simulación
        when(userRepository.existsByEmail(userDto.email())).thenReturn(true);

        // Ejecutar y verificar excepción
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));
        assertEquals("El email que se intenta registrar ya está en uso", exception.getMessage());

        // Verificar interacciones
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_CuiAlreadyExists() {
        // Datos de prueba
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // Simulación
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(true);

        // Ejecutar y verificar excepción
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));
        assertEquals("El CUI que se intenta registrar ya está en uso", exception.getMessage());

        // Verificar interacciones
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, times(1)).existsByCui(userDto.cui());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_NitAlreadyExists() {
        // Datos de prueba
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // Simulación
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(false);
        when(userRepository.existsByNit(userDto.nit())).thenReturn(true);

        // Ejecutar y verificar excepción
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));
        assertEquals("El NIT que se intenta registrar ya está en uso", exception.getMessage());

        // Verificar interacciones
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, times(1)).existsByCui(userDto.cui());
        verify(userRepository, times(1)).existsByNit(userDto.nit());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_PhoneAlreadyExists() {
        // Datos de prueba
        SignUpDto userDto = new SignUpDto("John Doe", "1234567890123", "password123", "1234567890", "john.doe@example.com", "555-1234");

        // Simulación
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.existsByCui(userDto.cui())).thenReturn(false);
        when(userRepository.existsByNit(userDto.nit())).thenReturn(false);
        when(userRepository.existsByPhone(userDto.phone())).thenReturn(true);

        // Ejecutar y verificar excepción
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));
        assertEquals("El número de teléfono que se intenta registrar ya está en uso", exception.getMessage());

        // Verificar interacciones
        verify(userRepository, times(1)).existsByEmail(userDto.email());
        verify(userRepository, times(1)).existsByCui(userDto.cui());
        verify(userRepository, times(1)).existsByNit(userDto.nit());
        verify(userRepository, times(1)).existsByPhone(userDto.phone());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_Success() {
        // Datos de prueba
        Long userId = 1L;
        UserProfileDto userProfileDto = new UserProfileDto(1L,"Updated Name", "1234567890123", "updated.email@example.com", "555-4321", "1234567890", "newImageUrl");

        // Crear entidad de usuario existente con rol asignado
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

        // Simulación de búsqueda y guardado
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar
        UserDto result = userService.updateUser(userId, userProfileDto);

        // Verificaciones
        assertEquals(userProfileDto.name(), result.name());
        assertEquals(userProfileDto.cui(), result.cui());
        assertEquals(userProfileDto.email(), result.email());

        // Verificar que se haya llamado a los métodos esperados
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingEntity);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Datos de prueba
        Long userId = 1L;
        UserProfileDto userProfileDto = new UserProfileDto(1L,"Updated Name", "1234567890123", "updated.email@example.com", "555-4321", "1234567890", "newImageUrl");

        // Simulación de búsqueda sin resultados
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Ejecutar y verificar excepción
        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> userService.updateUser(userId, userProfileDto));
        assertEquals("User not found with id: " + userId, exception.getMessage());

        // Verificar que el método save no se llama si el usuario no es encontrado
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
