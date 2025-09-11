package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.sazzler_auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.sazzler_auth_service.DTO.UserLogResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    AuthenticationManager authenticationManager;
    CustomUserDetailService userDetailService;

    @Autowired
    LoginService(CustomUserDetailService userDetailService,AuthenticationManager authenticationManager) {
        this.userDetailService = userDetailService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<UserLogResponse> authenticate(UserLogReq userLogReq) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= new UsernamePasswordAuthenticationToken(
                userLogReq.getId(),
                userLogReq.getPassword()
        );
        // Load user details to check if user exists
        Authentication authentication=authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if(authentication.isAuthenticated()){
            String token="Sss";
            return ResponseEntity.ok(new UserLogResponse("Login Successful!",token));
        }

        return ResponseEntity.status(401).body(new UserLogResponse("Authentication Failed!",null));


    }
}
