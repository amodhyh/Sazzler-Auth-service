package com.sazzler.ecommerce.sazzler_auth_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserRegReq;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserAlreadyExists;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserTooYound;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.EmptyRegistrationDetails;
import com.sazzler.ecommerce.sazzler_auth_service.Services.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterControllerTest {

    @Mock
    private RegisterService registerService;

    @InjectMocks
    private RegisterController registerController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @ControllerAdvice
    static class RestExceptionHandler {
        @ExceptionHandler(UserAlreadyExists.class)
        public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExists ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
        }

        @ExceptionHandler(UserTooYound.class)
        public ResponseEntity<Object> handleUserTooYoung(UserTooYound ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }

        @ExceptionHandler(EmptyRegistrationDetails.class)
        public ResponseEntity<Object> handleEmptyRegistration(EmptyRegistrationDetails ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(registerController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        UserRegReq request = new UserRegReq("testuser", "test@example.com", "password", "Test", "User", LocalDate.of(2000, 1, 1), "ROLE_USER");
        when(registerService.register(any(UserRegReq.class))).thenReturn(ResponseEntity.ok("User registered successfully"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testRegister_UserAlreadyExists() throws Exception {
        UserRegReq request = new UserRegReq("testuser", "test@example.com", "password", "Test", "User", LocalDate.of(2000, 1, 1), "ROLE_USER");
        when(registerService.register(any(UserRegReq.class))).thenThrow(new UserAlreadyExists("User already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    public void testRegister_UserTooYoung() throws Exception {
        UserRegReq request = new UserRegReq("testuser", "test@example.com", "password", "Test", "User", LocalDate.now().minusYears(15), "ROLE_USER");
        when(registerService.register(any(UserRegReq.class))).thenThrow(new UserTooYound("User must be at least 16 years old to register."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User must be at least 16 years old to register."));
    }

    @Test
    public void testRegister_InvalidInput_BlankUsername() throws Exception {
        UserRegReq request = new UserRegReq("", "test@example.com", "password", "Test", "User", LocalDate.of(2000, 1, 1), "ROLE_USER");
        when(registerService.register(any(UserRegReq.class))).thenThrow(new EmptyRegistrationDetails("Registration request contains invalid data. All fields are required."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registration request contains invalid data. All fields are required."));
    }

    @Test
    public void testRegister_InvalidInput_NullPassword() throws Exception {
        UserRegReq request = new UserRegReq("testuser", "test@example.com", null, "Test", "User", LocalDate.of(2000, 1, 1), "ROLE_USER");
        when(registerService.register(any(UserRegReq.class))).thenThrow(new EmptyRegistrationDetails("Registration request contains invalid data. All fields are required."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registration request contains invalid data. All fields are required."));
    }
}
