package com.sazzler.ecommerce.sazzler_auth_service.Controller;

import org.springframework.boot.autoconfigure.AutoConfiguration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@AutoConfiguration
    @RestController
    public class LoginController {

        @GetMapping("/login")
        public String index() {
            return "index";
        }

//        @PostMapping("/login")
//        public ResponseEntity<?> authenticate(UserLogReq userLogReq) {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userLogReq.getUsername(), userLogReq.getPassword()));
//        }
    }