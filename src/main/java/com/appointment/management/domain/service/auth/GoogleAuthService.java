package com.appointment.management.domain.service.auth;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

    @Autowired
    private GoogleAuthenticator googleAuth;

    public String generateGoogleAuthQrUrl(String companyName, String userName, String googleAuthKey) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(companyName, userName,
                new GoogleAuthenticatorKey.Builder(googleAuthKey).build());
    }

    public boolean authencateUserWithGoogleAuth(String googleAuthKey, int code) {
        return googleAuth.authorize(googleAuthKey, code);
    }

    public String getUserGoogleAuthKey() {
        GoogleAuthenticatorKey credentials = googleAuth.createCredentials();
        return credentials.getKey();
    }
}