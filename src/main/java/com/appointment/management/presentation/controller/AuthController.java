package com.appointment.management.presentation.controller;


import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.application.exception.FailedAuthenticateException;
import com.appointment.management.application.exception.RequestConflictException;
import com.appointment.management.application.exception.ValueNotFoundException;
import com.appointment.management.domain.dto.auth.*;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.auth.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private AuthConfirmationService authConfirmationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateRendererService templateRendererService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private TokenDto addTokenToUserData(String token, boolean temporal, UserWithGoogleSecretDto user) {
        return new TokenDto(token, user.id(), user.name(), user.email(), temporal, user.role());
    }

    private TokenDto addAccessTokenToUserData(UserWithGoogleSecretDto user) {
        return addTokenToUserData(tokenService.generateAccessToken(user.id()), false, user);
    }

    private TokenDto addTemporalTokenToUserData(UserWithGoogleSecretDto user) {
        return addTokenToUserData(tokenService.generateTemporalAccessToken(user.id()), true, user);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid SignUpDto user) {
        UserDto dbUser = userService.registerUser(user);

        String code = authConfirmationService.generateEmailConfirmationCode(dbUser.email());

        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", dbUser);

        String confirmationHtml = templateRendererService.renderTemplate("sign-up-confirmation", templateVariables);

        try {
            emailService.sendHtmlEmail("Appointment-Management", dbUser.email(),
                    "Confirmacion de usuario en Appointment Management", confirmationHtml);
        } catch (MessagingException e) {
           // System.out.println(e.getMessage());
            throw new RequestConflictException("No se pudo enviar el correo de confirmacion "+e.getMessage());
        }

        return new ResponseEntity<>(dbUser, HttpStatus.CREATED);
    }

    @PutMapping("/sign-up")
    public ResponseEntity<TokenDto> confirmSignUp(@RequestBody @Valid SignUpConfirmationDto user) {
        boolean confirmed = authConfirmationService.confirmUserEmailCode(user.email(), user.code());

        if (!confirmed) {
            throw new FailedAuthenticateException("No se pudo confirmar la cuenta");
        }

        TokenDto token = userService.findUserWithGoogleKeyByEmail(user.email())
                .map(this::addAccessTokenToUserData)
                .orElseThrow(() -> new InsufficientAuthenticationException("No se encontro el registro del usuario"));

        return ResponseEntity.ok(token);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInDto user) {
        var authenticableUser = new UsernamePasswordAuthenticationToken(user.email(), user.password());
        authenticationManager.authenticate(authenticableUser);

        return userService.findUserWithGoogleKeyByEmail(user.email())
                .filter(dbUser -> dbUser.googleAuthKey() == null)
                .map(this::addAccessTokenToUserData)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.accepted().build());
    }

    @PostMapping("/sign-in/2fa")
    public ResponseEntity<TokenDto> signIn2fa(@RequestBody @Valid SignIn2faDto user) {
        UserWithGoogleSecretDto user2fa = userService.findUserWithGoogleKeyByEmail(user.email())
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el registro del usuario"));

        if (user2fa.googleAuthKey() == null) {
            throw new BadRequestException("El usuario debe de tener activada la autenticacion por dos factores");
        }
        if (!googleAuthService.authencateUserWithGoogleAuth(user2fa.googleAuthKey(), user.code())) {
            throw new InsufficientAuthenticationException("La autenticacion en dos factores fallo");
        }

        return ResponseEntity.ok(addAccessTokenToUserData(user2fa));
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody @Valid RecoverPasswordDto user) {
        UserDto dbUser = userService.findUserByEmail(user.email())
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el registro del usuario"));

        String code = authConfirmationService.generateEmailConfirmationCode(dbUser.email());

        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", dbUser);

        String confirmationHtml = templateRendererService.renderTemplate("recover-password", templateVariables);

        try {
            emailService.sendHtmlEmail("Appointment Management", dbUser.email(), "Recuperacion de contraseña en Cloudmerce",
                    confirmationHtml);
        } catch (MessagingException e) {
            throw new RequestConflictException("No se pudo enviar el correo para la recuperacion de contraseña");
        }

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/recover-password")
    public ResponseEntity<TokenDto> confirmSignUp(@RequestBody @Valid RecoverPasswordConfirmationDto user) {
        boolean confirmed = authConfirmationService.confirmUserEmailCode(user.email(), user.code());

        if (!confirmed) {
            throw new RequestConflictException("No se logro confirmar el cambio de contraseña");
        }

        TokenDto token = userService.findUserWithGoogleKeyByEmail(user.email())
                .map(this::addTemporalTokenToUserData)
                .get();

        return ResponseEntity.ok(token);
    }
}
