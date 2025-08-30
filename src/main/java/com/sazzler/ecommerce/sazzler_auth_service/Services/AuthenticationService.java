package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.sazzler_auth_service.DTO.UserLogResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    UserLogResponse login(String username, String password) {
        // Logic to authenticate user
        // This is a placeholder implementation
        UserLogResponse response = new UserLogResponse();
        response.setUsername(username);
        response.setToken("dummy-token");
        return response;
    }
}
