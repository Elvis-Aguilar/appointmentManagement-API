package com.appointment.management.domain.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.appointment.management.persistance.entity.PermissionEntity;
import com.appointment.management.persistance.entity.RoleEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.entity.UserPermissionEntity;
import com.appointment.management.persistance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationManagerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private ConcurrentMap<String, String> signUpConfirmationCodes;

    @InjectMocks
    private AuthenticationManagerService authenticationManagerService;

    //globales
    private String email;
    private String password;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //Given
        email = "test@example.com";
        password = "password";
        user = new UserEntity("John Doe", "123456789",
                "FADFA", "987654321", email, "555-5555", new RoleEntity("CLIENTE","afdfds"));
        user.setUserPermissions(Collections.singleton(new UserPermissionEntity(user, new PermissionEntity("READ", "Read permission"))));

    }

    @Test
    void authenticate_ShouldReturnAuthentication_WhenCredentialsAreValid() {
        // When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(encoder.matches(password, user.getPassword())).thenReturn(true);
        when(signUpConfirmationCodes.containsKey(email)).thenReturn(false);

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        //llamada la funcion a testear
        Authentication result = authenticationManagerService.authenticate(auth);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getPrincipal());
        assertEquals(2, result.getAuthorities().size());
    }

    @Test
    void authenticate_ShouldThrowBadCredentialsException_WhenEmailIsInvalid() {
        // Given
        String email = "test@example.com";
        String password = "password";

        //When
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        // Then
        assertThrows(BadCredentialsException.class, () -> authenticationManagerService.authenticate(auth));
    }

    @Test
    void authenticate_ShouldThrowBadCredentialsException_WhenPasswordIsInvalid() {
        // Given
        UserEntity user = new UserEntity("John Doe", "123456789",
                "FADFA", "987654321", email, "555-5555", new RoleEntity("CLIENTE","afdfds"));

        // When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(encoder.matches(password, user.getPassword())).thenReturn(false);
        when(signUpConfirmationCodes.containsKey(email)).thenReturn(false);

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authenticationManagerService.authenticate(auth));
    }

    @Test
    void authenticate_ShouldThrowInsufficientAuthenticationException_WhenAccountNotConfirmed() {
        // When
        when(signUpConfirmationCodes.containsKey(email)).thenReturn(true);

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        // Then
        assertThrows(InsufficientAuthenticationException.class, () -> authenticationManagerService.authenticate(auth));
    }
}
