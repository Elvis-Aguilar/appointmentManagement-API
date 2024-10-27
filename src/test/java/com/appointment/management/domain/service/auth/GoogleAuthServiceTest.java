package com.appointment.management.domain.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    @Mock
    private GoogleAuthenticator googleAuth;

    @InjectMocks
    private GoogleAuthService googleAuthService;

    private String googleAuthKey;
    private int validCode;
    private String companyName;
    private String userName;

    @BeforeEach
    void setUp() {
        googleAuthKey = "testAuthKey";
        validCode = 123456;
        companyName = "TestCompany";
        userName = "testUser";
    }

    @Test
    void generateGoogleAuthQrUrl_ShouldReturnCorrectUrl_WhenParametersAreValid() {
        // Given
        GoogleAuthenticatorKey credentials = new GoogleAuthenticatorKey.Builder(googleAuthKey).build();
        String expectedUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(companyName, userName, credentials);

        // When
        String result = googleAuthService.generateGoogleAuthQrUrl(companyName, userName, googleAuthKey);

        // Then
        assertEquals(expectedUrl, result);
    }

    @Test
    void authenticateUserWithGoogleAuth_ShouldReturnTrue_WhenCodeIsValid() {
        // Given
        when(googleAuth.authorize(googleAuthKey, validCode)).thenReturn(true);

        // When
        boolean result = googleAuthService.authencateUserWithGoogleAuth(googleAuthKey, validCode);

        // Then
        assertTrue(result);
        verify(googleAuth).authorize(googleAuthKey, validCode);
    }

    @Test
    void getUserGoogleAuthKey_ShouldReturnGeneratedKey() {
        // Given
        GoogleAuthenticatorKey credentials = new GoogleAuthenticatorKey.Builder(googleAuthKey).build();
        when(googleAuth.createCredentials()).thenReturn(credentials);

        // When
        String result = googleAuthService.getUserGoogleAuthKey();

        // Then
        assertEquals(googleAuthKey, result);
        verify(googleAuth).createCredentials();
    }
}
