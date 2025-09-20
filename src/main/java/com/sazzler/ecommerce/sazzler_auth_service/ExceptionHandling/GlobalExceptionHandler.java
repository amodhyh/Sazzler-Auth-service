package com.sazzler.ecommerce.sazzler_auth_service.ExceptionHandling;


import com.sazzler.ecommerce.api_def.auth_service.Exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {


    @ExceptionHandler
    public ResponseEntity<String> userNotFoundhandleEx(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> userAlreadyExistsEx(UserAlreadyExists ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> userTooYoungEx(UserTooYound ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> EmptyLoginCredentials(EmptyLoginCredentials ex) {
        return ResponseEntity.status(401).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> EmptyRegDetails(EmptyRegistrationDetails ex) {
        return ResponseEntity.status(401).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> InvalidCredentials(InvalidCredentials ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}
