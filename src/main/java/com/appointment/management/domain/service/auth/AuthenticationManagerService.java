package com.appointment.management.domain.service.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.entity.UserPermissionEntity;
import com.appointment.management.persistance.entity.PermissionEntity;

import com.appointment.management.persistance.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


public class AuthenticationManagerService implements AuthenticationManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ConcurrentMap<String, String> signUpCondifmationCodes;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authUser) throws AuthenticationException {
        String email = authUser.getPrincipal().toString();
        String password = authUser.getCredentials().toString();

        if (signUpCondifmationCodes.containsKey(email)) {
            throw new InsufficientAuthenticationException("La cuenta aun no se ha confirmado");
        }

        UserEntity user = userRepository.findByEmail(email)
                .filter(dbUser -> encoder.matches(password, dbUser.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("El email o la contraseña es incorrecta"));

        List<SimpleGrantedAuthority> authorities = user.getUserPermissions()
                .stream()
                .map(UserPermissionEntity::getPermission)
                .map(PermissionEntity::getName)
                .map("PERMISSION_"::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));

        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        return new UsernamePasswordAuthenticationToken(email, password, authorities);
    }
}
