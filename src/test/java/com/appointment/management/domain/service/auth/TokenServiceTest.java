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

    }


    @Test
    void testRecoverTokenWithBearerPrefix() {
        String token = "Bearer validToken";
        when(request.getHeader("Authorization")).thenReturn(token);

        String recoveredToken = tokenService.recoverToken(request);
        assertEquals("validToken", recoveredToken);
    }

    @Test
    void testRecoverTokenWithoutBearerPrefix() {
        when(request.getHeader("Authorization")).thenReturn(null);

        String recoveredToken = tokenService.recoverToken(request);
        assertNull(recoveredToken);
    }
}

