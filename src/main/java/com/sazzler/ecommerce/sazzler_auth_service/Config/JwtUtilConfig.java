package com.sazzler.ecommerce.sazzler_auth_service.Config;

import com.sazzler.ecommerce.util.JWTutil.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtUtilConfig {
    @Value("${jwt.secret}" )
    private String secretkry;

    @Value("${jwt.expiration}")
    private Long jwtExpirationInMs;

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil(secretkry, jwtExpirationInMs);
    }
}
