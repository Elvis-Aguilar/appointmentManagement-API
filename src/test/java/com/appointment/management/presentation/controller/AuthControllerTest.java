package com.appointment.management.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.application.exception.FailedAuthenticateException;
import com.appointment.management.application.exception.RequestConflictException;
import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.auth.*;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.auth.*;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private AuthConfirmationService authConfirmationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private TemplateRendererService templateRendererService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        // Given
        SignUpDto signUpDto = new SignUpDto("testUser", "test@example.com", "password123","fadfad00","2515","afadf");
        UserDto expectedUserDto = new UserDto(1L, "testUser", "test@example.com", "USER", "fsd", "555", LocalDateTime.now(), "fadsf", false, "adfd", new ArrayList<>());
        String confirmationCode = "123456";
        String confirmationHtml = "<html><body>Confirmation Email</body></html>";

        given(userService.registerUser(any(SignUpDto.class))).willReturn(expectedUserDto);
        given(authConfirmationService.generateEmailConfirmationCode(expectedUserDto.email())).willReturn(confirmationCode);
        given(templateRendererService.renderTemplate(any(String.class), any(Map.class))).willReturn(confirmationHtml);

        // Simulamos el envío del correo electrónico


        // When
        ResponseEntity<UserDto> response = authController.signUp(signUpDto);

        // Then
        verify(userService, times(1)).registerUser(signUpDto);
        verify(authConfirmationService, times(1)).generateEmailConfirmationCode(expectedUserDto.email());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUserDto, response.getBody());
    }

    @Test
    void testSignUpEmailSendFailure()  {
        // Given
        SignUpDto signUpDto = new SignUpDto("testUser", "test@example.com", "password123","fadfad00","2515","afadf");
        UserDto expectedUserDto = new UserDto(1L, "testUser", "test@example.com", "USER", "fsd", "555", LocalDateTime.now(), "fadsf", false, "adfd", new ArrayList<>());
        String confirmationCode = "123456";
        String confirmationHtml = "<html><body>Confirmation Email</body></html>";

        given(userService.registerUser(any(SignUpDto.class))).willReturn(expectedUserDto);
        given(authConfirmationService.generateEmailConfirmationCode(expectedUserDto.email())).willReturn(confirmationCode);
        given(templateRendererService.renderTemplate(any(String.class), any(Map.class))).willReturn(confirmationHtml);

    }

    @Test
    void testConfirmSignUp() {
        // Given
        SignUpConfirmationDto confirmationDto = new SignUpConfirmationDto("test@example.com", "123456");
        TokenDto expectedToken = new TokenDto("token123", 1L, "testUser", "test@example.com", false, "USER");

        // Simular el comportamiento de confirmación de correo electrónico
        given(authConfirmationService.confirmUserEmailCode(confirmationDto.email(), confirmationDto.code())).willReturn(true);
        // Simular la búsqueda del usuario con clave de Google
        given(userService.findUserWithGoogleKeyByEmail(confirmationDto.email())).willReturn(Optional.of(new UserWithGoogleSecretDto(1L, "testUser", "test@example.com", "USER", "googleKey")));
        // Simular la creación del token
        given(tokenService.generateAccessToken(any(Long.class))).willReturn("token123");

        // When
        ResponseEntity<TokenDto> response = authController.confirmSignUp(confirmationDto);

        // Then
        verify(authConfirmationService, times(1)).confirmUserEmailCode(confirmationDto.email(), confirmationDto.code());
        verify(userService, times(1)).findUserWithGoogleKeyByEmail(confirmationDto.email());
        verify(tokenService, times(1)).generateAccessToken(1L);

        assertEquals(ResponseEntity.ok(expectedToken), response);
    }

    @Test
    void testConfirmSignUpFailOnEmailConfirmation() {
        // Given
        SignUpConfirmationDto confirmationDto = new SignUpConfirmationDto("test@example.com", "wrongCode");

        // Simular que la confirmación de correo electrónico falla
        given(authConfirmationService.confirmUserEmailCode(confirmationDto.email(), confirmationDto.code())).willReturn(false);

        // When / Then
        assertThrows(FailedAuthenticateException.class, () -> authController.confirmSignUp(confirmationDto));
    }

    @Test
    void testConfirmSignUpUserNotFound() {
        // Given
        SignUpConfirmationDto confirmationDto = new SignUpConfirmationDto("test@example.com", "123456");

        // Simular la confirmación de correo electrónico
        given(authConfirmationService.confirmUserEmailCode(confirmationDto.email(), confirmationDto.code())).willReturn(true);
        // Simular que no se encuentra el usuario
        given(userService.findUserWithGoogleKeyByEmail(confirmationDto.email())).willReturn(Optional.empty());

        // When / Then
        assertThrows(InsufficientAuthenticationException.class, () -> authController.confirmSignUp(confirmationDto));
    }

    @Test
    void testSignInSuccess() {
        // Given
        SignInDto signInDto = new SignInDto("test@example.com", "password123");
        UserWithGoogleSecretDto expectedUser = new UserWithGoogleSecretDto(1L, "testUser", "test@example.com", "USER", null);
        TokenDto expectedToken = new TokenDto("token123", expectedUser.id(), expectedUser.name(), expectedUser.email(), false, expectedUser.role());

        // Simular autenticación
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(mock(Authentication.class));
        // Simular búsqueda del usuario
        given(userService.findUserWithGoogleKeyByEmail(signInDto.email())).willReturn(Optional.of(expectedUser));
        // Simular generación del token
        given(tokenService.generateAccessToken(expectedUser.id())).willReturn("token123");

        // When
        ResponseEntity<?> response = authController.signIn(signInDto);

        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).findUserWithGoogleKeyByEmail(signInDto.email());
        verify(tokenService, times(1)).generateAccessToken(expectedUser.id());

        assertEquals(ResponseEntity.ok(expectedToken), response);
    }

    @Test
    void testSignInWithGoogleAuthKey() {
        // Given
        SignInDto signInDto = new SignInDto("test@example.com", "password123");
        UserWithGoogleSecretDto expectedUser = new UserWithGoogleSecretDto(1L, "testUser", "test@example.com", "USER", "googleKey");

        // Simular autenticación
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(mock(Authentication.class));
        // Simular búsqueda del usuario
        given(userService.findUserWithGoogleKeyByEmail(signInDto.email())).willReturn(Optional.of(expectedUser));

        // When
        ResponseEntity<?> response = authController.signIn(signInDto);

        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).findUserWithGoogleKeyByEmail(signInDto.email());

        assertEquals(ResponseEntity.accepted().build(), response);
    }

    @Test
    void testSignInFailInvalidCredentials() {
        // Given
        SignInDto signInDto = new SignInDto("test@example.com", "wrongPassword");

        // Simular que la autenticación lanza una excepción
        doThrow(new RuntimeException("Authentication failed")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // When / Then
        assertThrows(RuntimeException.class, () -> authController.signIn(signInDto));
    }

    @Test
    void testSignInUserNotFound() {
        // Given
        SignInDto signInDto = new SignInDto("test@example.com", "password123");

        // Simular autenticación
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(mock(Authentication.class));
        // Simular que no se encuentra el usuario
        given(userService.findUserWithGoogleKeyByEmail(signInDto.email())).willReturn(Optional.empty());

        // When
        ResponseEntity<?> response = authController.signIn(signInDto);

        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).findUserWithGoogleKeyByEmail(signInDto.email());

        assertEquals(ResponseEntity.accepted().build(), response);
    }

    @Test
    void testSignIn2faSuccess() {
        // Given
        SignIn2faDto signIn2faDto = new SignIn2faDto("test@example.com", 1234);
        UserWithGoogleSecretDto user2fa = new UserWithGoogleSecretDto(1L, "testUser", "test@example.com", "USER", "googleAuthKey");
        TokenDto expectedToken = new TokenDto("token123", user2fa.id(), user2fa.name(), user2fa.email(), false, user2fa.role());

        // Simular búsqueda del usuario
        given(userService.findUserWithGoogleKeyByEmail(signIn2faDto.email())).willReturn(Optional.of(user2fa));
        // Simular validación del código de Google Auth
        given(googleAuthService.authencateUserWithGoogleAuth(user2fa.googleAuthKey(), signIn2faDto.code())).willReturn(true);
        // Simular generación del token
        given(tokenService.generateAccessToken(user2fa.id())).willReturn("token123");

        // When
        ResponseEntity<TokenDto> response = authController.signIn2fa(signIn2faDto);

        // Then
        verify(userService, times(1)).findUserWithGoogleKeyByEmail(signIn2faDto.email());
        verify(googleAuthService, times(1)).authencateUserWithGoogleAuth(user2fa.googleAuthKey(), signIn2faDto.code());
        verify(tokenService, times(1)).generateAccessToken(user2fa.id());

        assertEquals(ResponseEntity.ok(expectedToken), response);
    }

    @Test
    void testSignIn2faUserNotFound() {
        // Given
        SignIn2faDto signIn2faDto = new SignIn2faDto("test@example.com", 1234);

        // Simular búsqueda del usuario
        given(userService.findUserWithGoogleKeyByEmail(signIn2faDto.email())).willReturn(Optional.empty());

        // When / Then
        assertThrows(ValueNotFoundException.class, () -> authController.signIn2fa(signIn2faDto));

        verify(userService, times(1)).findUserWithGoogleKeyByEmail(signIn2faDto.email());
    }

    @Test
    void testSignIn2faWithoutGoogleAuth() {
        // Given
        SignIn2faDto signIn2faDto = new SignIn2faDto("test@example.com", 1234);
        UserWithGoogleSecretDto user2fa = new UserWithGoogleSecretDto(1L, "testUser", "test@example.com", "USER", null);

        // Simular búsqueda del usuario
        given(userService.findUserWithGoogleKeyByEmail(signIn2faDto.email())).willReturn(Optional.of(user2fa));

        // When / Then
        assertThrows(BadRequestException.class, () -> authController.signIn2fa(signIn2faDto));

        verify(userService, times(1)).findUserWithGoogleKeyByEmail(signIn2faDto.email());
    }

    @Test
    void testSignIn2faFailedAuthentication() {
        // Given
        SignIn2faDto signIn2faDto = new SignIn2faDto("test@example.com", 1234);
        UserWithGoogleSecretDto user2fa = new UserWithGoogleSecretDto(1L, "testUser", "test@example.com", "USER", "googleAuthKey");

        // Simular búsqueda del usuario
        given(userService.findUserWithGoogleKeyByEmail(signIn2faDto.email())).willReturn(Optional.of(user2fa));
        // Simular fallo en la autenticación de Google Auth
        given(googleAuthService.authencateUserWithGoogleAuth(user2fa.googleAuthKey(), signIn2faDto.code())).willReturn(false);

        // When / Then
        assertThrows(InsufficientAuthenticationException.class, () -> authController.signIn2fa(signIn2faDto));

        verify(userService, times(1)).findUserWithGoogleKeyByEmail(signIn2faDto.email());
        verify(googleAuthService, times(1)).authencateUserWithGoogleAuth(user2fa.googleAuthKey(), signIn2faDto.code());
    }

    @Test
    void testRecoverPasswordSuccess() {
        // Given
        RecoverPasswordDto recoverPasswordDto = new RecoverPasswordDto("test@example.com");
        UserDto dbUser = new UserDto(1L, "testUser", "test@example.com", "USER", "fsd", "555", LocalDateTime.now(), "fadsf", false, "adfd", new ArrayList<>());
        String confirmationCode = "123456";
        String confirmationHtml = "<html><body>Confirmation Code: 123456</body></html>";

        // Simular búsqueda del usuario
        given(userService.findUserByEmail(recoverPasswordDto.email())).willReturn(Optional.of(dbUser));
        // Simular generación del código de confirmación
        given(authConfirmationService.generateEmailConfirmationCode(dbUser.email())).willReturn(confirmationCode);
        // Simular renderizado de la plantilla
        given(templateRendererService.renderTemplate("recover-password", Map.of("code", confirmationCode.toCharArray(), "user", dbUser)))
                .willReturn(confirmationHtml);


        // When
        ResponseEntity<?> response = authController.recoverPassword(recoverPasswordDto);

        // Then
        verify(userService, times(1)).findUserByEmail(recoverPasswordDto.email());
        verify(authConfirmationService, times(1)).generateEmailConfirmationCode(dbUser.email());

    }

    @Test
    void testRecoverPasswordUserNotFound() {
        // Given
        RecoverPasswordDto recoverPasswordDto = new RecoverPasswordDto("test@example.com");

        // Simular búsqueda del usuario
        given(userService.findUserByEmail(recoverPasswordDto.email())).willReturn(Optional.empty());

        // When / Then
        assertThrows(ValueNotFoundException.class, () -> authController.recoverPassword(recoverPasswordDto));

        verify(userService, times(1)).findUserByEmail(recoverPasswordDto.email());
    }

    @Test
    void testRecoverPasswordEmailSendingFailure() {
        // Given
        RecoverPasswordDto recoverPasswordDto = new RecoverPasswordDto("test@example.com");
        UserDto dbUser = new UserDto(1L, "testUser", "test@example.com", "USER", "fsd", "555", LocalDateTime.now(), "fadsf", false, "adfd", new ArrayList<>());
        String confirmationCode = "123456";
        String confirmationHtml = "<html><body>Confirmation Code: 123456</body></html>";

        // Simular búsqueda del usuario
        given(userService.findUserByEmail(recoverPasswordDto.email())).willReturn(Optional.of(dbUser));
        // Simular generación del código de confirmación
        given(authConfirmationService.generateEmailConfirmationCode(dbUser.email())).willReturn(confirmationCode);
        // Simular renderizado de la plantilla
        given(templateRendererService.renderTemplate("recover-password", Map.of("code", confirmationCode.toCharArray(), "user", dbUser)))
                .willReturn(confirmationHtml);


        // When / Then

    }

    @Test
    void testConfirmSignUpSuccess() {
        // Given
        RecoverPasswordConfirmationDto recoverPasswordConfirmationDto = new RecoverPasswordConfirmationDto("test@example.com", "123456");
        boolean isConfirmed = true;
        UserWithGoogleSecretDto userWithGoogleSecret = new UserWithGoogleSecretDto(1L, "testUser", "test@example.com", "CLIENTE", null);
        String token = "temp-token";

        // Simular confirmación del código de correo
        given(authConfirmationService.confirmUserEmailCode(recoverPasswordConfirmationDto.email(), recoverPasswordConfirmationDto.code())).willReturn(isConfirmed);
        // Simular búsqueda del usuario
        given(userService.findUserWithGoogleKeyByEmail(recoverPasswordConfirmationDto.email())).willReturn(Optional.of(userWithGoogleSecret));
        // Simular generación del token temporal
        given(tokenService.generateTemporalAccessToken(userWithGoogleSecret.id())).willReturn(token);

        // When
        ResponseEntity<TokenDto> response = authController.confirmSignUp(recoverPasswordConfirmationDto);

        // Then
        verify(authConfirmationService, times(1)).confirmUserEmailCode(recoverPasswordConfirmationDto.email(), recoverPasswordConfirmationDto.code());
        verify(userService, times(1)).findUserWithGoogleKeyByEmail(recoverPasswordConfirmationDto.email());
        verify(tokenService, times(1)).generateTemporalAccessToken(userWithGoogleSecret.id());

        assertEquals(ResponseEntity.ok(new TokenDto(token, userWithGoogleSecret.id(), userWithGoogleSecret.name(), userWithGoogleSecret.email(), true, userWithGoogleSecret.role())), response);
    }

    @Test
    void testConfirmSignUpFailure() {
        // Given
        RecoverPasswordConfirmationDto recoverPasswordConfirmationDto = new RecoverPasswordConfirmationDto("test@example.com", "123456");
        boolean isConfirmed = false;

        // Simular que la confirmación falla
        given(authConfirmationService.confirmUserEmailCode(recoverPasswordConfirmationDto.email(), recoverPasswordConfirmationDto.code())).willReturn(isConfirmed);

        // When / Then
        assertThrows(RequestConflictException.class, () -> authController.confirmSignUp(recoverPasswordConfirmationDto));

        verify(authConfirmationService, times(1)).confirmUserEmailCode(recoverPasswordConfirmationDto.email(), recoverPasswordConfirmationDto.code());
        verify(userService, times(0)).findUserWithGoogleKeyByEmail(any());
    }

    @Test
    void testConfirmSignUpUserNotFounds() {
        // Given
        RecoverPasswordConfirmationDto recoverPasswordConfirmationDto = new RecoverPasswordConfirmationDto("test@example.com", "123456");
        boolean isConfirmed = true;

        // Simular confirmación exitosa del código de correo
        given(authConfirmationService.confirmUserEmailCode(recoverPasswordConfirmationDto.email(), recoverPasswordConfirmationDto.code())).willReturn(isConfirmed);
        // Simular que no se encuentra el usuario
        given(userService.findUserWithGoogleKeyByEmail(recoverPasswordConfirmationDto.email())).willReturn(Optional.empty());

        // When / Then

    }

}