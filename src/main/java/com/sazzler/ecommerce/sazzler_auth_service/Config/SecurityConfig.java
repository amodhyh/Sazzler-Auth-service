package com.sazzler.ecommerce.sazzler_auth_service.Config;

//import com.sazzler.ecommerce.sazzler_auth_service.Services.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //which url paths should be secured, and which should not
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. CSRF configuration
        // 2. Authorization rules
        // 3. Authentication mechanisms (form login, http basic, etc.)
        // 4. Session management
        // 5. Logout configuration
        // 6. Exception handling
        // 7. Any custom filters

        http.csrf(customizer->customizer.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/register", "/login","/logout").permitAll()
                        .requestMatchers("/home", "/profile").authenticated()
                        .requestMatchers(("/admin/**")).hasRole("ADMIN")


                )
                .addFilterBefore()




        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public static DelegatingPasswordEncoder passwordEncoder() {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(20, 65536, 1, 32, 16);
        return new DelegatingPasswordEncoder("argon2", Map.of("argon2", argon2PasswordEncoder));
    }
}
