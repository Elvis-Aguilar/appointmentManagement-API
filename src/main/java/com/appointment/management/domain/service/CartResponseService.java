package com.appointment.management.domain.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class CartResponseService {
    private HashMap<String,Object> response = new HashMap<>();
    private final String DATA = "data";
    private final String ERROR = "error";
    private final String MESSAGE = "message";

    public ResponseEntity<Object> responseSuccess(Object dataValue, String messageValue, HttpStatus status) {
        this.response.clear();
        this.response.put(DATA, dataValue);
        this.response.put(MESSAGE, messageValue);
        return new ResponseEntity<>(this.response, status);
    }

    public ResponseEntity<Object> responseError(String messageValue, HttpStatus status) {
        this.response.clear();
        this.response.put(ERROR, true);
        this.response.put(MESSAGE, messageValue);
        return new ResponseEntity<>(this.response, status);
    }


}
