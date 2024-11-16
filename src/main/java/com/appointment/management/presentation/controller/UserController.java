package com.appointment.management.presentation.controller;

import static java.util.function.Predicate.not;

import com.appointment.management.application.exception.BadRequestException;
import com.appointment.management.domain.dto.auth.*;
import com.appointment.management.domain.dto.user.PasswordChangeDto;
import com.appointment.management.domain.dto.user.UserChangeDto;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.dto.user.UserProfileDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.auth.GoogleAuthService;
import com.appointment.management.domain.service.auth.TokenService;
import com.appointment.management.persistance.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PatchMapping("/disable-a2f")
    public ResponseEntity<UserDto> disableA2F(@NonNull HttpServletRequest request) {
        long id = tokenService.getIdFromToken(request);

        UserDto user = userService.deletedGoogleAuthentication(id);

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

    @PutMapping("/change-password/{id}")
    public ResponseEntity<TokenDto> changePasswordRecover(@PathVariable Long id,
                                                   @RequestBody @Valid RecoverNewPassword user) {

        UserWithGoogleSecretDto dbUser = userService.changeUserPasswordRecover(id, user.newPassword());

        return ResponseEntity.ok(addTokenToUserData(dbUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TokenDto> changePassword(@PathVariable Long id,
                                                   @RequestBody @Valid PasswordChangeDto user) {

        UserWithGoogleSecretDto dbUser = userService.changeUserPassword(id,  user.repeatedPassword(), user.password());

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

    @GetMapping("/role-excluding")
    public ResponseEntity<List<UserDto>> getUsersWithRoleExclusive() {
        List<Long> excludedRoles = List.of(1L, 2L);
        return ResponseEntity.ok(userService.getUsersExcludingRoles(excludedRoles));
    }

    @PatchMapping("/prob/{userId}/role/{newRoleId}")
    public ResponseEntity<UserDto> changeUserRole(@PathVariable Long userId, @PathVariable Long newRoleId) {
        try {
            UserDto updatedUser = userService.changeUserRole(userId, newRoleId);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyInformation(@NonNull HttpServletRequest request) {
        long id = tokenService.getIdFromToken(request);

        return ResponseEntity.of(userService.findUserById(id));
    }

}
