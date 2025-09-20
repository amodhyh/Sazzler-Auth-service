package com.sazzler.ecommerce.sazzler_auth_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.EmptyLoginCredentials;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.InvalidCredentials;
import com.sazzler.ecommerce.sazzler_auth_service.Services.LoginService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @ControllerAdvice
    static class RestExceptionHandler {
        @ExceptionHandler(InvalidCredentials.class)
        public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentials ex) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(EmptyLoginCredentials.class)
        public ResponseEntity<Object> handleEmptyLoginCredentials(EmptyLoginCredentials ex) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        UserLogReq request = new UserLogReq("testuser", "password");
        UserLogResponse response = new UserLogResponse("login success", "test-token");

        when(loginService.authenticate(any(UserLogReq.class))).thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.message").value("login success"));

    }

    @Test
    public void testLoginFailure() throws Exception {
        UserLogReq request = new UserLogReq("testuser", "wrongpassword");

        when(loginService.authenticate(any(UserLogReq.class))).thenThrow(new InvalidCredentials("Authentication failed"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication failed"));
    }

    @Test
    public void invalidInputNoUsernameOrEmail() throws Exception {
        UserLogReq request = new UserLogReq("", "password");

        when(loginService.authenticate(any(UserLogReq.class))).thenThrow(new EmptyLoginCredentials("Username and password must not be empty"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username and password must not be empty"));
    }

    @Test
    public void testInvalidInput_NullUsername() throws Exception {
        UserLogReq request = new UserLogReq(null, "password");

        when(loginService.authenticate(any(UserLogReq.class))).thenThrow(new EmptyLoginCredentials("Username and password must not be empty"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username and password must not be empty"));
    }

    @Test
    public void testInvalidInput_EmptyPassword() throws Exception {
        UserLogReq request = new UserLogReq("testuser", "");

        when(loginService.authenticate(any(UserLogReq.class))).thenThrow(new EmptyLoginCredentials("Username and password must not be empty"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username and password must not be empty"));
    }

    @Test
    public void testInvalidInput_NullPassword() throws Exception {
        UserLogReq request = new UserLogReq("testuser", null);

        when(loginService.authenticate(any(UserLogReq.class))).thenThrow(new EmptyLoginCredentials("Username and password must not be empty"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username and password must not be empty"));
    }
}
