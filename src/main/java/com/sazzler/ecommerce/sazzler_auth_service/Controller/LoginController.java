package com.sazzler.ecommerce.sazzler_auth_service.Controller;

import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.sazzler_auth_service.Services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class LoginController {

private final LoginService loginService;

//no need for @Autowired here
//Spring injects automatically if there is only one constructor.
public LoginController(LoginService loginService){
    this.loginService=loginService;
}

    @PostMapping("/auth/login")
    public ResponseEntity<UserLogResponse> login(@RequestBody UserLogReq userLogReq) {

        return loginService.authenticate(userLogReq);
    }
    @GetMapping("/auth/validate")
    public ResponseEntity<String> validateToken() {

    return new ResponseEntity<>("auth is running",HttpStatus.OK);
    }
//    private final AuthenticationManager authenticationManager;

//        @PostMapping("/login")
//        public ResponseEntity<?> authenticate(UserLogReq userLogReq) {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userLogReq.getUsername(), userLogReq.getPassword()));
//        }
    }