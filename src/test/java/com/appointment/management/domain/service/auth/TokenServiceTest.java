package com.appointment.management.domain.service.auth;

import com.auth0.jwt.JWTCreator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //Given: sin Given Global, pues no es necesario, no comparten
    }

    @Test
    void testRecoverTokenWithBearerPrefix() {
        //Given
        String token = "Bearer validToken";

        //When
        when(request.getHeader("Authorization")).thenReturn(token);

        //Then
        String recoveredToken = tokenService.recoverToken(request);
        assertEquals("validToken", recoveredToken);
    }

    @Test
    void testRecoverTokenWithoutBearerPrefix() {
        //When
        when(request.getHeader("Authorization")).thenReturn(null);

        //Then
        String recoveredToken = tokenService.recoverToken(request);
        assertNull(recoveredToken);
    }
}

