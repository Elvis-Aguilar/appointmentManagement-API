package com.appointment.management.domain.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ConcurrentMap;

@ExtendWith(MockitoExtension.class)
class AuthConfirmationServiceTest {

    @Mock
    private GoogleAuthenticator googleAuth;

    @Mock
    private ConcurrentMap<String, String> emailConfirmationCodes;

    @InjectMocks
    private AuthConfirmationService authConfirmationService;

    private String email;
    private String code;
    private GoogleAuthenticatorKey credentials;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        code = "123456";
        GoogleAuthenticatorKey credentials = new GoogleAuthenticatorKey.Builder("testKey").build();

        lenient().when(googleAuth.createCredentials()).thenReturn(credentials);
        lenient().when(googleAuth.getTotpPassword(credentials.getKey())).thenReturn(Integer.parseInt(code));
    }


    @Test
    void generateEmailConfirmationCode_ShouldGenerateAndStoreCode() {
        // Act: Genera el código de confirmación de email
        String generatedCode = authConfirmationService.generateEmailConfirmationCode(email);

        // Assert: Verificar que el código es generado correctamente y se almacena
        assertEquals(code, generatedCode);
        verify(emailConfirmationCodes).put(email, generatedCode);
    }

    @Test
    void confirmUserEmailCode_ShouldReturnTrue_WhenCodeMatches() {
        // Act: Llama al método de confirmación con el email y código correcto
        boolean result = authConfirmationService.confirmUserEmailCode(email, code);

        // Assert: Verificar que el código fue confirmado y removido
        assertFalse(result);
        verify(emailConfirmationCodes).remove(email, code);
    }

    @Test
    void confirmUserEmailCode_ShouldReturnFalse_WhenCodeDoesNotMatch() {
        // Act: Llama al método de confirmación con un código incorrecto
        boolean result = authConfirmationService.confirmUserEmailCode(email, "wrongCode");

        // Assert: Verificar que el resultado es false y no se removió ningún código
        assertFalse(result);
        verify(emailConfirmationCodes).remove(email, "wrongCode");
    }

    @Test
    void confirmUserEmailCode_ShouldReturnFalse_WhenEmailNotFound() {
        // Act: Llama al método de confirmación con un email que no tiene código asignado
        boolean result = authConfirmationService.confirmUserEmailCode("unknown@example.com", code);

        // Assert: Verificar que el resultado es false y no se removió ningún código
        assertFalse(result);
        verify(emailConfirmationCodes).remove("unknown@example.com", code);
    }
}
