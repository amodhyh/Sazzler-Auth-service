package com.sazzler.ecommerce.sazzler_auth_service.ExceptionHandling;


import com.sazzler.ecommerce.api_def.auth_service.Exceptions.InvalidCredentials;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserAlreadyExists;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {


    @ExceptionHandler
    public ResponseEntity<String> userNotFoundhandleEx(UserNotFoundException ex) {
        return ResponseEntity.status(404).body("User Not Found");
    }

    @ExceptionHandler
    public ResponseEntity<String> userAlreadyExistsEx(UserAlreadyExists ex) {
        return ResponseEntity.status(409).body("User Already Exists");
    }

    @ExceptionHandler
    public ResponseEntity<String> userTooYoungEx(IOException ex) {
        return ResponseEntity.status(500).body("User is too young to register");
    }

    @ExceptionHandler
    public ResponseEntity<String> InvalidCredentials(InvalidCredentials ex) {
        return ResponseEntity.status(400).body("Invalid Credentials");
    }
}
