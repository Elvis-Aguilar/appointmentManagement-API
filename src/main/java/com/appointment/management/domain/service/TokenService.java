package com.appointment.management.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${security.jwt.token.expiration-time-min}")
    private long expirationTimeMin;

    private int tempExpirationTimeMin = 5;

    @Autowired
    private Algorithm algorithm;

    private String generateAccessToken(long id, long minutes, boolean temporal) {
        try {
            return JWT.create()
                    .withSubject(String.valueOf(id))
                    .withClaim("temporal", temporal)
                    .withExpiresAt(genAccessExpirationDate(minutes))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String generateAccessToken(long id) {
        return generateAccessToken(id, expirationTimeMin, false);
    }

    public String generateTemporalAccessToken(long id) {
        return generateAccessToken(id, tempExpirationTimeMin, true);
    }

    public boolean isTemporalToken(HttpServletRequest request) {
        try {
            Boolean temporal = JWT.require(algorithm)
                    .build()
                    .verify(recoverToken(request))
                    .getClaim("temporal")
                    .asBoolean();

            return Boolean.TRUE.equals(temporal);
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    public long getIdFromToken(HttpServletRequest request) {
        return getIdFromToken(recoverToken(request));
    }

    public long getIdFromToken(String token) {
        try {
            String id = JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();

            return Long.parseLong(id);
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    private Instant genAccessExpirationDate(long minutes) {
        return LocalDateTime.now()
                .plusMinutes(minutes)
                .toInstant(ZoneOffset.of("-06:00"));
    }

    public String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
