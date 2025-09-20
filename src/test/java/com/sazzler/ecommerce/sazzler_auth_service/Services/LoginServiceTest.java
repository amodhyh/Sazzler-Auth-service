package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.InvalidCredentials;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import com.sazzler.ecommerce.util.JWTutil.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;


    private LoginService loginService;

    @BeforeEach
    public void setup() {
        // Manually instantiate the service with its mocked dependencies
        loginService = new LoginService(authenticationManager,  jwtUtil );
    }

    @Test
    public void testAuthenticate_Success() {
        // Arrange
        //make controlled outputs from the depended on services
        UserLogReq userLogReq = new UserLogReq("testuser", "password123");
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        SazzlerUserDetails userDetails = new SazzlerUserDetails(123L, "testuser", "test@email.com", "password123", authorities);
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "mocked_jwt_token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString(), anySet())).thenReturn(expectedToken);
        // Act
        ResponseEntity<UserLogResponse> responseEntity = loginService.authenticate(userLogReq);

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals("Login Successful!", responseEntity.getBody().message());
        assertEquals(expectedToken, responseEntity.getBody().token());
    }

    @Test
    public void testAuthenticate_Fail() {
        // arrange
        UserLogReq userLogReq = new UserLogReq("testuser", "wrongpassword");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenThrow(new InvalidCredentials("Invalid username or password"));

        // act and assert
        try {
            loginService.authenticate(userLogReq);
        } catch (Exception e) {
            assertEquals("Invalid username or password", e.getMessage());
        }
    }
}
