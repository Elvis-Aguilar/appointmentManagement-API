package com.appointment.management.domain.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class CartResponseServiceTest {

    private CartResponseService cartResponseService;

    //Variables globales para el given global
    private String dataValue;
    private String messageValue;
    private HttpStatus status;

    @BeforeEach
    void setUp() {
        cartResponseService = new CartResponseService();
        //Given Global
        dataValue = "Sample data";
        messageValue = "Operation successful";
        status = HttpStatus.OK;
    }

    @Test
    void givenDataAndMessage_whenResponseSuccess_thenReturnValidResponseEntity() {

        // When
        ResponseEntity<Object> response = cartResponseService.responseSuccess(dataValue, messageValue, status);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof HashMap);

        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        assertEquals(dataValue, responseBody.get("data"));
        assertEquals(messageValue, responseBody.get("message"));
        assertFalse(responseBody.containsKey("error"));
    }

    @Test
    void givenMessage_whenResponseError_thenReturnValidResponseEntity() {
        // Given
        String messageValue = "Operation failed";
        status = HttpStatus.BAD_REQUEST;

        // When
        ResponseEntity<Object> response = cartResponseService.responseError(messageValue, status);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof HashMap);

        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        assertEquals(true, responseBody.get("error"));
        assertEquals(messageValue, responseBody.get("message"));
        assertFalse(responseBody.containsKey("data"));
    }

}
