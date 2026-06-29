package com.sazzler.ecommerce.sazzler_auth_service.ExceptionHandling;


import com.sazzler.ecommerce.sazzler_api_def.auth_service.Exceptions.UserTooYound;
import com.sazzler.ecommerce.sazzler_api_def.auth_service.Exceptions.EmptyLoginCredentials;
import com.sazzler.ecommerce.sazzler_api_def.auth_service.Exceptions.EmptyRegistrationDetails;
import com.sazzler.ecommerce.sazzler_api_def.auth_service.Exceptions.InvalidCredentials;
import com.sazzler.ecommerce.sazzler_api_def.auth_service.Exceptions.UserAlreadyExists;
import com.sazzler.ecommerce.sazzler_api_def.auth_service.Exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
