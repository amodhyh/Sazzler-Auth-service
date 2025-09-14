package com.sazzler.ecommerce.sazzler_auth_service.Services;


import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import com.sazzler.ecommerce.util.JWTutil.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


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

        SazzlerUserDetails userDetails=userDetailService.loadUserByUsername(userLogReq.str_id());
        Set< GrantedAuthority> role= new HashSet<>(userDetails.getAuthorities());
        JWTUtil jwtUtil=new JWTUtil("{$jwt.secret}", 600000L);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= new UsernamePasswordAuthenticationToken(
                userLogReq.str_id(),
                userLogReq.password()
        );
        // Load user details to check if user exists
        Authentication authentication=authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if(authentication.isAuthenticated()){
            String token= jwtUtil.generateToken(userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getAuthorities()
                    );
            return ResponseEntity.ok(new UserLogResponse("Login Successful!",token));
        }

        return ResponseEntity.status(401).body(new UserLogResponse("Authentication Failed!",null));


    }
}
