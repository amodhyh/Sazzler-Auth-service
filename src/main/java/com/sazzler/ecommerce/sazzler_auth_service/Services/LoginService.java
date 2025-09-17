package com.sazzler.ecommerce.sazzler_auth_service.Services;


import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import com.sazzler.ecommerce.util.JWTutil.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Autowired
    public LoginService( AuthenticationManager authenticationManager, @Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = new JWTUtil(secret, expiration);
    }

    public ResponseEntity<UserLogResponse> authenticate(UserLogReq userLogReq) {

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

        return ResponseEntity.status(401).body(new UserLogResponse("Authentication Failed!", null));
    }
}
