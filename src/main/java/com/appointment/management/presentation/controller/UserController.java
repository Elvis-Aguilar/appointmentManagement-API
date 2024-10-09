package com.appointment.management.presentation.controller;

import static java.util.function.Predicate.not;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.auth.GoogleAuthDto;
import com.appointment.management.domain.dto.auth.GoogleAuthKeyDto;
import com.appointment.management.domain.dto.auth.TokenDto;
import com.appointment.management.domain.dto.auth.UserWithGoogleSecretDto;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.auth.GoogleAuthService;
import com.appointment.management.domain.service.auth.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private TokenService tokenService;

    private TokenDto addTokenToUserData(UserWithGoogleSecretDto user) {
        String token = tokenService.generateAccessToken(user.id());
        return new TokenDto(token, user.id(), user.name(), user.email(), false, user.role());
    }

    @GetMapping("/multifactor-authentication")
    public ResponseEntity<GoogleAuthDto> generateMultiFactorAuthentication(@NonNull HttpServletRequest request) {
        long id = tokenService.getIdFromToken(request);

        GoogleAuthDto googleAuth = userService.findUserById(id)
                .filter(not(UserDto::hasMultiFactorAuth))
                .map(user -> {
                    String googleAuthKey = googleAuthService.getUserGoogleAuthKey();
                    String qrUrl = googleAuthService.generateGoogleAuthQrUrl("Appointment-Management", user.name(), googleAuthKey);
                    return new GoogleAuthDto(qrUrl, googleAuthKey);
                })
                .orElseThrow(() -> new BadRequestException(
                        "El usuario ya tiene activada la autenticacion por dos factores"));

        return ResponseEntity.ok(googleAuth);
    }

    @PatchMapping("/multifactor-authentication")
    public ResponseEntity<UserDto> addMultiFactorAuthentication(@NonNull HttpServletRequest request,
                                                                @RequestBody GoogleAuthKeyDto googleKey) {
        long id = tokenService.getIdFromToken(request);
        System.out.println(id);
        System.out.println(googleKey);
        if (!googleAuthService.authencateUserWithGoogleAuth(googleKey.authKey(), googleKey.code())) {
            throw new BadRequestException("El codigo no es valido");
        }
        UserDto user = userService.addGoogleAuthentication(id, googleKey.authKey());

        return ResponseEntity.ok(user);
    }

}
