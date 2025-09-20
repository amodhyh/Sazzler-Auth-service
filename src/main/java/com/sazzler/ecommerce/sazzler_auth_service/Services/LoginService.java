package com.sazzler.ecommerce.sazzler_auth_service.Services;


import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.EmptyLoginCredentials;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.InvalidCredentials;
import com.sazzler.ecommerce.sazzler_auth_service.Config.JwtUtilConfig;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import com.sazzler.ecommerce.util.JWTutil.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Autowired
    public LoginService( AuthenticationManager authenticationManager,JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }



    public ResponseEntity<UserLogResponse> authenticate(UserLogReq userLogReq) {

        if (userLogReq.str_id() == null || userLogReq.str_id().isBlank() ||
            userLogReq.password() == null || userLogReq.password().isBlank()) {
            throw new EmptyLoginCredentials(" username and password must not be empty");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userLogReq.str_id(),
                userLogReq.password()
        );

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (authentication.isAuthenticated()) {
            SazzlerUserDetails userDetails = (SazzlerUserDetails) authentication.getPrincipal();
            Set<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            String token = jwtUtil.generateToken(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    authorities
            );
            return ResponseEntity.ok(new UserLogResponse("Login Successful!", token));
        }
        else{
            throw new InvalidCredentials("Invalid username or password");
        }

    }
}
