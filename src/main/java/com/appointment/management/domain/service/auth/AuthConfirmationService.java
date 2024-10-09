package com.appointment.management.domain.service.auth;

import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
public class AuthConfirmationService {

    @Autowired
    private GoogleAuthenticator googleAuth;

    @Autowired
    private ConcurrentMap<String, String> emailConfirmationCodes;

    public String generateEmailConfirmationCode(String email) {
        GoogleAuthenticatorKey credentials = googleAuth.createCredentials();
        String code = String.format("%06d", googleAuth.getTotpPassword(credentials.getKey()));
        emailConfirmationCodes.put(email, code);
        return code;
    }

    public boolean confirmUserEmailCode(String email, String code) {
        return emailConfirmationCodes.remove(email, code);
    }
}