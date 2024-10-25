package com.appointment.management.presentation.controller;

import static java.util.function.Predicate.not;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.auth.GoogleAuthDto;
import com.appointment.management.domain.dto.auth.GoogleAuthKeyDto;
import com.appointment.management.domain.dto.auth.TokenDto;
import com.appointment.management.domain.dto.auth.UserWithGoogleSecretDto;
import com.appointment.management.domain.dto.user.PasswordChangeDto;
import com.appointment.management.domain.dto.user.UserChangeDto;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.dto.user.UserProfileDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.auth.GoogleAuthService;
import com.appointment.management.domain.service.auth.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (!googleAuthService.authencateUserWithGoogleAuth(googleKey.authKey(), googleKey.code())) {
            throw new BadRequestException("El codigo no es valido");
        }
        UserDto user = userService.addGoogleAuthentication(id, googleKey.authKey());

        return ResponseEntity.ok(user);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getMyInformation(@PathVariable Long id) {
        return ResponseEntity.of(userService.findUserById(id));
    }

    @PutMapping
    public ResponseEntity<TokenDto> changePassword(@NonNull HttpServletRequest request,
                                                       @RequestBody @Valid UserChangeDto user) {
        long id = tokenService.getIdFromToken(request);
        boolean temporal = tokenService.isTemporalToken(request);

        UserWithGoogleSecretDto dbUser = userService.changeUserInfo(id, user, temporal);

        return ResponseEntity.ok(addTokenToUserData(dbUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TokenDto> changePassword(@PathVariable Long id,
                                                   @RequestBody @Valid PasswordChangeDto user) {

        UserWithGoogleSecretDto dbUser = userService.changeUserPassword(id, user.password(), user.repeatedPassword());

        return ResponseEntity.ok(addTokenToUserData(dbUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody @Valid UserProfileDto user) {
        UserDto userUpdate = this.userService.updateUser(id, user);
        return ResponseEntity.ok(userUpdate);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<List<UserDto>> getUsersWithRole(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getAllUsersWithRole(id));
    }


}
